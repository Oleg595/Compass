package com.example.algorithm;

import com.example.algorithm.common.CompareAlternativesCalculator;
import com.example.algorithm.common.ForecastFunctionCalculator;
import com.example.algorithm.configurator.AlgorithmConfigurator;
import com.example.algorithm.entity.AlternativeEntity;
import com.example.algorithm.entity.AlternativePair;
import com.example.algorithm.factory.ContextFactory;
import lpsolve.LpSolveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AlgorithmConfigurator.class)
public class CompareAlternativesCalculatorTest {
    @Autowired
    private ContextFactory contextFactory;
    @Autowired
    private ForecastFunctionCalculator ffCalculator;
    @Autowired
    private CompareAlternativesCalculator caCalculator;

    private boolean compareCriteriaValues(AlternativeEntity alt1, AlternativeEntity alt2) {
        var map1 = alt1.getCriteriaToValue();
        var map2 = alt2.getCriteriaToValue();
        var keys = new HashSet<>(map1.keySet());
        keys.addAll(map2.keySet());

        for (var key : keys) {
            var value1 = map1.get(key);
            var value2 = map2.get(key);
            if (value1 == null && value2 != null) {
                return false;
            }
            if (value1 != null && !value1.equals(value2)) {
                return false;
            }
        }
        return true;
    }

    @Test
    public void testCalculator1() throws LpSolveException {
        var dataContext = contextFactory.createClearContext();
        var criteriaNames = ContextFactory.getCriteriaNames();
        var values = ContextFactory.getCriteriaValues();

        var criteriaToValueA = Map.of(
            criteriaNames.get(0), values.get(0),
            criteriaNames.get(1), values.get(2),
            criteriaNames.get(2), values.get(0)
        );
        var criteriaToValueB = Map.of(
            criteriaNames.get(0), values.get(1),
            criteriaNames.get(1), values.get(1),
            criteriaNames.get(2), values.get(1)
        );
        var A = new AlternativeEntity("A", criteriaToValueA);
        var B = new AlternativeEntity("B", criteriaToValueB);
        var pair = new AlternativePair(A, B);

        var fFunction = ffCalculator.calculateForecastFunction(dataContext);
        var result = caCalculator.getResultVector(2, fFunction.getV(), pair, dataContext);

        var expectedA = new AlternativeEntity(
            "expectedA", Map.of(
            criteriaNames.get(0), values.get(0),
            criteriaNames.get(1), values.get(2)
        ));
        var expectedB = new AlternativeEntity(
            "expectedB", Map.of(
            criteriaNames.get(0), values.get(1),
            criteriaNames.get(1), values.get(1)
        ));
        Assertions.assertEquals(1, result.size());
        Assertions.assertTrue(compareCriteriaValues(expectedA, result.get(0).getFirst()));
        Assertions.assertTrue(compareCriteriaValues(expectedB, result.get(0).getSecond()));
    }

    @Test
    public void testCalculator2() throws LpSolveException {
        var dataContext = contextFactory.createContext1();
        var criteriaNames = ContextFactory.getCriteriaNames();
        var values = ContextFactory.getCriteriaValues();

        var criteriaToValueA = Map.of(
            criteriaNames.get(0), values.get(0),
            criteriaNames.get(1), values.get(1),
            criteriaNames.get(2), values.get(1)
        );
        var criteriaToValueB = Map.of(
            criteriaNames.get(0), values.get(1),
            criteriaNames.get(1), values.get(0),
            criteriaNames.get(2), values.get(0)
        );
        var A = new AlternativeEntity("A", criteriaToValueA);
        var B = new AlternativeEntity("B", criteriaToValueB);
        var pair = new AlternativePair(A, B);

        var fFunction = ffCalculator.calculateForecastFunction(dataContext);
        var result = caCalculator.getResultVector(2, fFunction.getV(), pair, dataContext);

        var expectedA = new AlternativeEntity(
            "expectedA", Map.of(
            criteriaNames.get(0), values.get(0),
            criteriaNames.get(1), values.get(2)
        ));
        var expectedB = new AlternativeEntity(
            "expectedB", Map.of(
            criteriaNames.get(0), values.get(1),
            criteriaNames.get(1), values.get(0)
        ));
        Assertions.assertEquals(1, result.size());
        Assertions.assertTrue(compareCriteriaValues(expectedA, result.get(0).getFirst()));
        Assertions.assertTrue(compareCriteriaValues(expectedB, result.get(0).getSecond()));
    }
}
