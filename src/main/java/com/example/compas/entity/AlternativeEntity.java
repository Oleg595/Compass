package com.example.compas.entity;

import lombok.Data;

import java.util.Map;

@Data
public class AlternativeEntity {
    private final String name;
    private final Map<String, String> criteriaToValue;

    public String getValueByCriteria(CriteriaEntity criteria) {
        var criteriaName = criteria.getName();
        return criteriaToValue.get(criteriaName);
    }
}
