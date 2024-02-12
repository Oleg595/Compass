package com.example.algorithm;

import com.example.algorithm.context.DataContext;
import com.example.algorithm.entity.AlternativeEntity;
import com.example.algorithm.entity.AlternativePair;
import com.example.algorithm.entity.CriteriaEntity;
import com.example.algorithm.implementation.rule.SolveSLAU;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class SolveSLAUTest {
    private static final List<String> values = List.of("value1", "value2", "value3");
    private static final List<String> criteriaNames = List.of("criteria1", "criteria2", "criteria3");

    // Генерация контекста, устроена таким образом, что добавляются 3 критерия, содержащих 3 значения.
    // Для каждого критерия устанавливается, что value1 > value2 > value3
    private DataContext generateContext() {
        var dataContext = new DataContext();
        for (var criteriaName : criteriaNames) {
            dataContext.addCriteria(new CriteriaEntity(criteriaName, values));
        }
        for (var criteria : dataContext.getCriterias()) {
            for (var valueNum = 0; valueNum < values.size(); ++valueNum) {
                var altName = criteria.getName() + "_" + values.get(valueNum);
                var criteriaToValue = Map.of(criteria.getName(), values.get(valueNum));
                var priorAlt = new AlternativeEntity(altName, criteriaToValue);
                for (var nextNum = valueNum + 1; nextNum < values.size(); ++nextNum) {
                    altName = criteria.getName() + "_" + values.get(nextNum);
                    criteriaToValue = Map.of(criteria.getName(), values.get(nextNum));
                    var secAlt = new AlternativeEntity(altName, criteriaToValue);
                    dataContext.addP(new AlternativePair(priorAlt, secAlt));
                }
            }
        }
        return dataContext;
    }

    @Test
    public void testSLAUSolve() {
        var dataContext = generateContext();

        // Добавляем 2 потенциальных ответа ЛПР
        var criteriaToValue1 = Map.of(
            criteriaNames.get(0), values.get(0),
            criteriaNames.get(2), values.get(1));
        var criteriaToValue2 = Map.of(
            criteriaNames.get(0), values.get(1),
            criteriaNames.get(2), values.get(0));
        var expertAlter1 = new AlternativeEntity("expertAlter1", criteriaToValue1);
        var exportAlter2 = new AlternativeEntity("expertAlter2", criteriaToValue2);
        dataContext.addP(new AlternativePair(expertAlter1, exportAlter2));

        criteriaToValue1 = Map.of(
            criteriaNames.get(1), values.get(0),
            criteriaNames.get(2), values.get(1));
        criteriaToValue2 = Map.of(
            criteriaNames.get(1), values.get(1),
            criteriaNames.get(2), values.get(0));
        expertAlter1 = new AlternativeEntity("expertAlter1", criteriaToValue1);
        exportAlter2 = new AlternativeEntity("expertAlter2", criteriaToValue2);
        dataContext.addP(new AlternativePair(expertAlter1, exportAlter2));

        // Создаём альтернативы A,B:
        var criteriaToValueA = Map.of(
            criteriaNames.get(0), values.get(0),
            criteriaNames.get(1), values.get(0),
            criteriaNames.get(2), values.get(1)
        );
        var A = new AlternativeEntity("A", criteriaToValueA);
        var criteriaToValueB = Map.of(
            criteriaNames.get(0), values.get(1),
            criteriaNames.get(1), values.get(1),
            criteriaNames.get(2), values.get(0)
        );
        var B = new AlternativeEntity("B", criteriaToValueB);

        // Решаем СЛАУ для поиска вывода правила P(A,B):
        SolveSLAU.generateAndSolve(A, B, dataContext);
    }
}
