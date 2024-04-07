package com.example.algorithm.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
public class AlternativeEntity {
    private final String name;
    private final Map<String, String> criteriaToValue;

    public String getValueByCriteria(CriteriaEntity criteria) {
        var criteriaName = criteria.getName();
        return criteriaToValue.get(criteriaName);
    }

    public boolean isEqual(AlternativeEntity alt) {
        var critNames = alt.criteriaToValue.keySet();
        if (critNames.equals(criteriaToValue.keySet())) {
            for (var name : critNames) {
                var curCritValue = criteriaToValue.get(name);
                var altCritValue = alt.criteriaToValue.get(name);
                if ((curCritValue == null && altCritValue != null)
                    || (curCritValue != null && !curCritValue.equals(altCritValue))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public AlternativeEntity copy() {
        var copyCriterias = new HashMap<>(criteriaToValue);
        return new AlternativeEntity(name, copyCriterias);
    }

    public String toString(Set<String> criteriaNames) {
        var result = new StringBuilder("(");
        for (var name : criteriaNames) {
            var value = criteriaToValue.get(name);
            if (value == null) {
                result.append("- , ");
            } else {
                result.append(value).append(", ");
            }
        }
        result.delete(result.length() - 2, result.length());
        result.append(")");
        return result.toString();
    }
}
