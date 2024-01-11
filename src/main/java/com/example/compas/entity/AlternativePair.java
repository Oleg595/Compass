package com.example.compas.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlternativePair {
    private AlternativeEntity first;
    private AlternativeEntity second;

//    public List<Double> calculateDelta(DataContext dataContext) {
//        var result = new ArrayList<>(Collections.nCopies(dataContext.getM(), 0.0));
//        var count = 0;
//        for (var criteria : dataContext.getCriterias()) {
//            var values = new ArrayList<>(criteria.getValues());
//            var value1 = first.getValueByCriteria(criteria);
//            var value2 = second.getValueByCriteria(criteria);
//            var index1 = values.indexOf(value1);
//            var index2 = values.indexOf(value2);
//            if (index1 != index2) {
//                result.set(count + index1, 1.0);
//                result.set(count + index2, -1.0);
//            }
//            count += values.size();
//        }
//        return result;
//    }
//
//    public static List<Double> calculateDelta(
//        AlternativeEntity first, AlternativeEntity second, DataContext dataContext) {
//        var pair = new AlternativePair(first, second);
//        return pair.calculateDelta(dataContext);
//    }
}
