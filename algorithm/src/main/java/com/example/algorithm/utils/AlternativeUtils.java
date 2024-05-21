package com.example.algorithm.utils;

import org.example.AlternativeEntity;
import org.example.AlternativePair;
import org.example.CriteriaEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AlternativeUtils {
    private static String generateName() {
        return "index_" + UUID.randomUUID();
    }

    public static AlternativeEntity createByDelta(List<Double> delta, List<CriteriaEntity> criterias) {
        var count = 0;
        var criteriaToValue = new HashMap<String, String>();
        for (var criteria: criterias) {
            String value = null;
            for (var index = 0; index < criteria.getValues().size(); ++index) {
                if (delta.get(index + count) > 0.0) {
                    value = criteria.getValues().toArray()[index].toString();
                }
            }
            criteriaToValue.put(criteria.getName(), value);
            count += criteria.getValues().size();
        }
        return new AlternativeEntity(-1, generateName(), criteriaToValue);
    }

    public static AlternativePair calculateAlternativePair(
        List<CriteriaEntity> allCriterias, List<CriteriaEntity> valCrts,
        List<String> val1, List<String> val2) {
        assert valCrts.size() == val1.size() && valCrts.size() == val2.size();
        var alt1 = new AlternativeEntity(-1, generateName(), new HashMap<>());
        var alt2 = new AlternativeEntity(-1, generateName(), new HashMap<>());
        for (var criteria : allCriterias) {
            if (valCrts.contains(criteria)) {
                var index = valCrts.indexOf(criteria);
                alt1.getCriteriaToValue().put(criteria.getName(), val1.get(index));
                alt2.getCriteriaToValue().put(criteria.getName(), val2.get(index));
            } else {
                alt1.getCriteriaToValue().put(criteria.getName(), null);
                alt2.getCriteriaToValue().put(criteria.getName(), null);
            }
        }
        return new AlternativePair(alt1, alt2);
    }

    public static List<Double> calculateDelta(AlternativeEntity alt, List<CriteriaEntity> criterias) {
        var result = new ArrayList<>(
            Collections.nCopies(ValueCalculatorUtils.calculateM(criterias), 0.0));
        var count = 0;
        for (var criteria : criterias) {
            var values = new ArrayList<>(criteria.getValues());
            var index = values.indexOf(alt.getValueByCriteria(criteria));
            if (index != -1) {
                result.set(count + index, 1.0);
            }
            count += values.size();
        }
        return result;
    }

    public static List<Double> calculateDelta(
        AlternativePair pair, List<CriteriaEntity> criterias) {
        var result = new ArrayList<>(
            Collections.nCopies(ValueCalculatorUtils.calculateM(criterias), 0.0));
        var count = 0;
        for (var criteria : criterias) {
            var values = new ArrayList<>(criteria.getValues());
            var value1 = pair.getFirst().getValueByCriteria(criteria);
            var value2 = pair.getSecond().getValueByCriteria(criteria);
            var index1 = values.indexOf(value1);
            var index2 = values.indexOf(value2);
            if (index1 != index2) {
                result.set(count + index1, 1.0);
                result.set(count + index2, -1.0);
            }
            count += values.size();
        }
        return result;
    }
}
