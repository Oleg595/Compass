package com.example.compas.context;

import com.example.compas.entity.AlternativeEntity;
import com.example.compas.entity.CriteriaEntity;
import com.example.compas.util.AlternativePair;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@Component
public class DataContext {
    private List<CriteriaEntity> criterias;
    private List<AlternativeEntity> alts;
    private List<AlternativePair> P = new ArrayList<>();
    private List<AlternativePair> I = new ArrayList<>();

    public int getM() {
        var result = 0;
        for (var criteria : criterias) {
            result += criteria.getValues().size();
        }
        return result;
    }

    private String generateAltName() {
        return "index_" + (P.size() + I.size());
    }

    private AlternativePair calculateAlts(List<CriteriaEntity> crts, List<String> val1, List<String> val2) {
        assert crts.size() == val1.size() && crts.size() == val2.size();
        var alt1 = new AlternativeEntity(generateAltName(), new HashMap<>());
        var alt2 = new AlternativeEntity(generateAltName(), new HashMap<>());
        for (var criteria : criterias) {
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

    public void addP(List<CriteriaEntity> crts, List<String> val1, List<String> val2) {
        P.add(calculateAlts(crts, val1, val2));
    }

    public void addI(List<CriteriaEntity> crts, List<String> val1, List<String> val2) {
        I.add(calculateAlts(crts, val1, val2));
    }

    public AlternativeEntity createAltByDelta(List<Double> delta) {
        var count = 0;
        var criteriaToValue = new HashMap<String, String>();
        for (var criteria: criterias) {
            String value = null;
            for (var index = 0; index < criteria.getValues().size(); ++index) {
                if (delta.get(index + count) > 0.0) {
                    value = criteria.getValues().get(index);
                }
            }
            criteriaToValue.put(criteria.getName(), value);
            count += criteria.getValues().size();
        }
        return new AlternativeEntity(generateAltName(), criteriaToValue);
    }
}
