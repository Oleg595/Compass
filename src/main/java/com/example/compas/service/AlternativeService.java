package com.example.compas.service;

import com.example.compas.context.DataContext;
import com.example.compas.entity.AlternativeEntity;
import com.example.compas.entity.AlternativePair;
import com.example.compas.entity.CriteriaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
public class AlternativeService {

    @Autowired
    private DataContext dataContext;

    private String generateName() {
        return "index_" + (dataContext.getP().size() + dataContext.getI().size());
    }

    public AlternativeEntity createByDelta(List<Double> delta) {
        var count = 0;
        var criteriaToValue = new HashMap<String, String>();
        for (var criteria: dataContext.getCriterias()) {
            String value = null;
            for (var index = 0; index < criteria.getValues().size(); ++index) {
                if (delta.get(index + count) > 0.0) {
                    value = criteria.getValues().get(index);
                }
            }
            criteriaToValue.put(criteria.getName(), value);
            count += criteria.getValues().size();
        }
        return new AlternativeEntity(generateName(), criteriaToValue);
    }

    public AlternativePair calculate(List<CriteriaEntity> crts, List<String> val1, List<String> val2) {
        assert crts.size() == val1.size() && crts.size() == val2.size();
        var alt1 = new AlternativeEntity(generateName(), new HashMap<>());
        var alt2 = new AlternativeEntity(generateName(), new HashMap<>());
        for (var criteria : dataContext.getCriterias()) {
            if (crts.contains(criteria)) {
                var index = crts.indexOf(criteria);
                alt1.getCriteriaToValue().put(criteria.getName(), val1.get(index));
                alt2.getCriteriaToValue().put(criteria.getName(), val2.get(index));
            } else {
                alt1.getCriteriaToValue().put(criteria.getName(), null);
                alt2.getCriteriaToValue().put(criteria.getName(), null);
            }
        }
        return new AlternativePair(alt1, alt2);
    }

    public List<Double> calculateDelta(AlternativeEntity alt) {
        var result = new ArrayList<>(Collections.nCopies(dataContext.getM(), 0.0));
        var count = 0;
        for (var criteria : dataContext.getCriterias()) {
            var values = new ArrayList<>(criteria.getValues());
            var index = values.indexOf(alt.getValueByCriteria(criteria));
            result.set(count + index, 1.0);
            count += values.size();
        }
        return result;
    }

    public List<Double> calculateDelta(AlternativePair pair) {
        var result = new ArrayList<>(Collections.nCopies(dataContext.getM(), 0.0));
        var count = 0;
        for (var criteria : dataContext.getCriterias()) {
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

    public List<Double> calculateDelta(
        AlternativeEntity first, AlternativeEntity second) {
        var pair = new AlternativePair(first, second);
        return calculateDelta(pair);
    }
}
