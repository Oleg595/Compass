package com.example.algorithm.utils;

import org.ejml.data.DMatrixRMaj;
import org.example.CriteriaEntity;
import org.example.DataContext;

import java.util.List;

public class ValueCalculatorUtils {
    public static int calculateM(List<CriteriaEntity> criterias) {
        var result = 0;
        for (var criteria : criterias) {
            result += criteria.getValues().size();
        }
        return result;
    }

    public static DMatrixRMaj calculateDMatrix(DataContext dataContext) {
        var P = dataContext.getP();
        var result = new DMatrixRMaj(calculateM(dataContext.getCriterias()), P.size());
        for (var col = 0; col < P.size(); ++col) {
            var delta = AlternativeUtils.calculateDelta(
                P.get(col).getPair(), dataContext.getCriterias());
            for (var row = 0; row < delta.size(); ++row) {
                result.set(row, col, delta.get(row));
            }
        }
        return result;
    }

    public static DMatrixRMaj calculateEMatrix(DataContext dataContext) {
        var I = dataContext.getI();
        var result = new DMatrixRMaj(calculateM(dataContext.getCriterias()), I.size());
        for (var col = 0; col < I.size(); ++col) {
            var delta = AlternativeUtils.calculateDelta(
                I.get(col).getPair(), dataContext.getCriterias());
            for (var row = 0; row < delta.size(); ++row) {
                result.set(row, col, delta.get(row));
            }
        }
        return result;
    }
}
