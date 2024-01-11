package com.example.compas.context;

import com.example.compas.entity.AlternativeEntity;
import com.example.compas.entity.AlternativePair;
import com.example.compas.entity.CriteriaEntity;
import com.example.compas.service.AlternativeService;
import lombok.Data;
import org.ejml.data.DMatrixRMaj;
import org.ejml.data.Matrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
public class DataContext {

    @Lazy
    @Autowired
    private AlternativeService alternativeService;

    private List<AlternativeEntity> alts;
    private List<CriteriaEntity> criterias = new ArrayList<>();
    private List<AlternativePair> P = new ArrayList<>();
    private List<AlternativePair> I = new ArrayList<>();

    public CriteriaEntity getCriteriaByNameOrNull(String name) {
        for (var criteria : criterias) {
            if (criteria.getName().equals(name)) {
                return criteria;
            }
        }
        return null;
    }

    public int getM() {
        var result = 0;
        for (var criteria : criterias) {
            result += criteria.getValues().size();
        }
        return result;
    }

    public void addP(List<CriteriaEntity> crts, List<String> val1, List<String> val2) {
        P.add(alternativeService.calculate(crts, val1, val2));
    }

    public void addI(List<CriteriaEntity> crts, List<String> val1, List<String> val2) {
        I.add(alternativeService.calculate(crts, val1, val2));
    }

    public void addCriteria(CriteriaEntity criteria) {
        criterias.add(criteria);
    }

    public DMatrixRMaj calculateDMatrix() {
        var result = new DMatrixRMaj(getM(), P.size());
        for (var col = 0; col < P.size(); ++col) {
            var delta = alternativeService.calculateDelta(P.get(col));
            for (var row = 0; row < delta.size(); ++row) {
                result.set(row, col, delta.get(row));
            }
        }
        return result;
    }

    public DMatrixRMaj calculateEMatrix() {
        var result = new DMatrixRMaj(getM(), I.size());
        for (var col = 0; col < I.size(); ++col) {
            var delta = alternativeService.calculateDelta(I.get(col));
            for (var row = 0; row < delta.size(); ++row) {
                result.set(row, col, delta.get(row));
            }
        }
        return result;
    }
}
