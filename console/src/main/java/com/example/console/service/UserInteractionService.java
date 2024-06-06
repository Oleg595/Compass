package com.example.console.service;

import org.example.AlternativeEntity;
import org.example.AlternativePair;
import org.example.DataContext;
import org.example.RuleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.System.exit;

@Service
public class UserInteractionService {
    private static final String ALTERNATIVE_QUESTION =
        "Choose the best value of the alternative (%s):\n 1 - %s;\n 2 - %s;\n 3 - equals";

    @Value("${user-interaction.isUser:true}")
    private boolean isUser;
    @Autowired
    private DataContext dataContext;

    @PostConstruct
    private void postConstruct() {
        if (isUser) {
            System.out.println("INFO: User input");
        } else {
            System.out.println("INFO: System input");
        }
    }

    private void processAnswer(int answer, AlternativePair pair) {
        switch (answer) {
            case 1:
                dataContext.addP(new AlternativePair(pair.getFirst(), pair.getSecond()));
                break;
            case 2:
                dataContext.addP(new AlternativePair(pair.getSecond(), pair.getFirst()));
                break;
            case 3:
                dataContext.addI(pair);
                break;
            default:
                throw new IllegalArgumentException("An incorrect response was specified");
        }
    }

    private String getAlternative(AlternativeEntity alt) {
        String result = null;
        for (var criteria: dataContext.getCriterias()) {
            var value = alt.getValueByCriteria(criteria);
            if (value != null) {
                if (result == null) {
                    result = value;
                } else {
                    result = result.concat(", " + value);
                }
            }
        }
        return result;
    }

    private String getCriterias(AlternativeEntity alt) {
        String result = null;
        for (var criteria: dataContext.getCriterias()) {
            var value = alt.getValueByCriteria(criteria);
            if (value != null) {
                if (result == null) {
                    result = criteria.getName();
                } else {
                    result = result.concat(", " + criteria.getName());
                }
            }
        }
        return result;
    }

    private int getChooseAlternativeAnswer() {
        if (isUser) {
            var reader = new Scanner(System.in);
            var result = reader.nextInt();
            while (result < 1 || result > 3) {
                System.out.println("Uncorrect answer. Please try again: ");
                result = reader.nextInt();
            }
            return result;
        } else {
            return ThreadLocalRandom.current().nextInt(1, 4);
        }
    }

    private int solveConflictAnswer(int rulesSize) {
        if (isUser) {
            var reader = new Scanner(System.in);
            var ruleIndex = reader.nextInt() - 1;
            if (ruleIndex == -1) {
                System.out.println("The program cannot work correctly with a contradictory system");
                exit(-1);
            }
            while (ruleIndex < 0 || ruleIndex >= rulesSize) {
                System.out.println("The rule number is incorrect. Please try again: ");
                ruleIndex = reader.nextInt() - 1;
            }
            return ruleIndex;
        } else {
            return ThreadLocalRandom.current().nextInt(1, rulesSize);
        }
    }

    public void compareAlternatives(AlternativePair pair) {
        System.out.printf(
            (ALTERNATIVE_QUESTION) + "%n", getCriterias(pair.getFirst()),
            getAlternative(pair.getFirst()), getAlternative(pair.getSecond()));
        var nextAnswer = getChooseAlternativeAnswer();
        System.out.println("Answer: " + nextAnswer);
        processAnswer(nextAnswer, pair);
    }

    public void solveConflict(List<RuleEntity> conflictRules) {
        System.out.println("A contradiction was found in the answers:");
        for (var index = 0; index < conflictRules.size(); ++index) {
            System.out.println(
                (index + 1) + ") " + conflictRules.get(index).toString(dataContext.getCriteriaNames()));
        }
        System.out.print("Enter the number of the rule you want to fix or 0 to shut down the program: ");
        var ruleIndex = solveConflictAnswer(conflictRules.size());
        dataContext.removeRule(conflictRules.get(ruleIndex));
    }
}
