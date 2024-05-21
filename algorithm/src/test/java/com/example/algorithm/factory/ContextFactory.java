package com.example.algorithm.factory;

import org.example.AlternativeEntity;
import org.example.AlternativePair;
import org.example.CriteriaEntity;
import org.example.DataContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
public class ContextFactory {
    private static final List<String> values = List.of("value1", "value2", "value3");
    private static final List<String> criteriaNames = List.of("criteria1", "criteria2", "criteria3");

    // Генерация контекста, устроена таким образом, что добавляются 3 критерия, содержащих 3 значения.
    // Для каждого критерия устанавливается, что value1 > value2 > value3
    private DataContext generateContext() {
        var dataContext = new DataContext(
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        for (var criteriaName : criteriaNames) {
            dataContext.addCriteria(
                new CriteriaEntity(criteriaName, new HashSet<>(values), Map.of(
                    "value1", 1, "value2", 2, "value3", 3)));
        }
        for (var criteria : dataContext.getCriterias()) {
            for (var valueNum = 0; valueNum < values.size(); ++valueNum) {
                var altName = criteria.getName() + "_" + values.get(valueNum);
                var criteriaToValue = Map.of(criteria.getName(), values.get(valueNum));
                var priorAlt = new AlternativeEntity(-1, altName, criteriaToValue);
                for (var nextNum = valueNum + 1; nextNum < values.size(); ++nextNum) {
                    altName = criteria.getName() + "_" + values.get(nextNum);
                    criteriaToValue = Map.of(criteria.getName(), values.get(nextNum));
                    var secAlt = new AlternativeEntity(-1, altName, criteriaToValue);
                    dataContext.addP(new AlternativePair(priorAlt, secAlt));
                }
            }
        }
        return dataContext;
    }

    public static List<String> getCriteriaNames() {
        return criteriaNames;
    }

    public static List<String> getCriteriaValues() {
        return values;
    }

    public DataContext createClearContext() {
        return generateContext();
    }

    public DataContext createContext1() {
        var dataContext = generateContext();

        // Добавляем 2 потенциальных ответа ЛПР
        var criteriaToValue1 = Map.of(
            criteriaNames.get(0), values.get(0),
            criteriaNames.get(2), values.get(1));
        var criteriaToValue2 = Map.of(
            criteriaNames.get(0), values.get(1),
            criteriaNames.get(2), values.get(0));
        var expertAlter1 = new AlternativeEntity(-1, "expertAlter1", criteriaToValue1);
        var exportAlter2 = new AlternativeEntity(-1, "expertAlter2", criteriaToValue2);
        dataContext.addP(new AlternativePair(expertAlter1, exportAlter2));

        criteriaToValue1 = Map.of(
            criteriaNames.get(1), values.get(0),
            criteriaNames.get(2), values.get(1));
        criteriaToValue2 = Map.of(
            criteriaNames.get(1), values.get(1),
            criteriaNames.get(2), values.get(0));
        expertAlter1 = new AlternativeEntity(-1, "expertAlter1", criteriaToValue1);
        exportAlter2 = new AlternativeEntity(-1, "expertAlter2", criteriaToValue2);
        dataContext.addP(new AlternativePair(expertAlter1, exportAlter2));

        return dataContext;
    }

    public DataContext createContext2() {
        var dataContext = generateContext();

        // Добавляем 2 потенциальных ответа ЛПР
        var criteriaToValue1 = Map.of(
            criteriaNames.get(0), values.get(0),
            criteriaNames.get(2), values.get(2));
        var criteriaToValue2 = Map.of(
            criteriaNames.get(0), values.get(2),
            criteriaNames.get(2), values.get(1));
        var expertAlter1 = new AlternativeEntity(-1, "expertAlter1", criteriaToValue1);
        var exportAlter2 = new AlternativeEntity(-1, "expertAlter2", criteriaToValue2);
        dataContext.addP(new AlternativePair(expertAlter1, exportAlter2));

        criteriaToValue1 = Map.of(
            criteriaNames.get(1), values.get(2),
            criteriaNames.get(2), values.get(0));
        criteriaToValue2 = Map.of(
            criteriaNames.get(1), values.get(1),
            criteriaNames.get(2), values.get(1));
        expertAlter1 = new AlternativeEntity(-1, "expertAlter1", criteriaToValue1);
        exportAlter2 = new AlternativeEntity(-1, "expertAlter2", criteriaToValue2);
        dataContext.addP(new AlternativePair(expertAlter1, exportAlter2));

        return dataContext;
    }

    public DataContext createContext3() {
        var dataContext = generateContext();

        var criteriaToValue1 = Map.of(
            criteriaNames.get(0), values.get(0),
            criteriaNames.get(2), values.get(2));
        var criteriaToValue2 = Map.of(
            criteriaNames.get(0), values.get(2),
            criteriaNames.get(2), values.get(1));
        var expertAlter1 = new AlternativeEntity(-1, "expertAlter1", criteriaToValue1);
        var exportAlter2 = new AlternativeEntity(-1, "expertAlter2", criteriaToValue2);
        dataContext.addI(new AlternativePair(expertAlter1, exportAlter2));

        criteriaToValue1 = Map.of(
            criteriaNames.get(1), values.get(2),
            criteriaNames.get(2), values.get(0));
        criteriaToValue2 = Map.of(
            criteriaNames.get(1), values.get(1),
            criteriaNames.get(2), values.get(1));
        expertAlter1 = new AlternativeEntity(-1, "expertAlter1", criteriaToValue1);
        exportAlter2 = new AlternativeEntity(-1, "expertAlter2", criteriaToValue2);
        dataContext.addI(new AlternativePair(expertAlter1, exportAlter2));

        return dataContext;
    }
}
