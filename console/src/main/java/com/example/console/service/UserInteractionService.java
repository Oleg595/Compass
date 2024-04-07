package com.example.console.service;

import com.example.algorithm.context.DataContext;
import com.example.algorithm.entity.AlternativeEntity;
import com.example.algorithm.entity.AlternativePair;
import com.example.algorithm.entity.Rule;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

@Service
@AllArgsConstructor
public class UserInteractionService {
    private static final String ALTERNATIVE_QUESTION = "Выберите лучшее значение альтернативы (%s): 1 - %s; 2 - %s; 3" +
        " - " +
        "эквивалентны";

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
                throw new IllegalArgumentException("Указан некорректный ответ");
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
        processAnswer(reader.nextInt(), pair);
    }

    public void solveConflict(List<Rule> conflictRules) {
        var reader = new Scanner(System.in);
        System.out.println("В ответах найдено противоречие:");
        for (var index = 0; index < conflictRules.size(); ++index) {
            System.out.println(
                (index + 1) + ") " + conflictRules.get(index).toString(dataContext.getCriteriaNames()));
        }
        System.out.print("Введите номер правила, который хотите исправить или 0 для завершения работы программы: ");
        var ruleIndex = reader.nextInt() - 1;
        if (ruleIndex == -1) {
            System.out.println("Программа не может работать корректно с противоречивой системой");
            exit(-1);
        }
        while (ruleIndex < 0 || ruleIndex >= conflictRules.size()) {
            System.out.println("Указан неверный номер правила. Повторите попытку: ");
            ruleIndex = reader.nextInt() - 1;
        }
        dataContext.removeRule(conflictRules.get(ruleIndex));
    }
}
