package com.example.console.service;

import lombok.AllArgsConstructor;
import org.example.AlternativeEntity;
import org.example.AlternativePair;
import org.example.DataContext;
import org.example.RuleEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.System.exit;

@Service
@AllArgsConstructor
public class UserInteractionService {
    private static final String ALTERNATIVE_QUESTION =
        "Choose the best value of the alternative (%s):\n 1 - %s;\n 2 - %s;\n 3 - equals";

    private final DataContext dataContext;

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

    public void compareAlternatives(AlternativePair pair) {
        var reader = new Scanner(System.in);
        System.out.printf(
            (ALTERNATIVE_QUESTION) + "%n", getCriterias(pair.getFirst()),
            getAlternative(pair.getFirst()), getAlternative(pair.getSecond()));
        var nextAnswer = reader.nextInt();
        System.out.println("Answer: " + nextAnswer);
        processAnswer(nextAnswer, pair);
    }

    public void solveConflict(List<RuleEntity> conflictRules) {
        var reader = new Scanner(System.in);
        System.out.println("A contradiction was found in the answers:");
        for (var index = 0; index < conflictRules.size(); ++index) {
            System.out.println(
                (index + 1) + ") " + conflictRules.get(index).toString(dataContext.getCriteriaNames()));
        }
        System.out.print("Enter the number of the rule you want to fix or 0 to shut down the program: ");
        var ruleIndex = reader.nextInt() - 1;
        if (ruleIndex == -1) {
            System.out.println("The program cannot work correctly with a contradictory system");
            exit(-1);
        }
        while (ruleIndex < 0 || ruleIndex >= conflictRules.size()) {
            System.out.println("The rule number is incorrect. Please try again: ");
            ruleIndex = reader.nextInt() - 1;
        }
        dataContext.removeRule(conflictRules.get(ruleIndex));
    }
}
