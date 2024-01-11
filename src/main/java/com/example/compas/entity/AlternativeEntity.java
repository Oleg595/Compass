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

//    public List<Double> calculateDelta(DataContext dataContext) {
//        var result = new ArrayList<>(Collections.nCopies(dataContext.getM(), 0.0));
//        var count = 0;
//        for (var criteria : dataContext.getCriterias()) {
//            var values = new ArrayList<>(criteria.getValues());
//            var index = values.indexOf(criteriaToValue.get(criteria.getName()));
//            result.set(count + index, 1.0);
//            count += values.size();
//        }
//        return result;
//    }
}
