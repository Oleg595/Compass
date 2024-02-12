package com.example.algorithm.utils;

import com.example.algorithm.context.DataContext;
import com.example.algorithm.entity.CriteriaEntity;
import org.ejml.data.DMatrixRMaj;

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
            var delta = AlternativeUtils.calculateDelta(P.get(col), dataContext.getCriterias());
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
            var delta = AlternativeUtils.calculateDelta(I.get(col), dataContext.getCriterias());
            for (var row = 0; row < delta.size(); ++row) {
                result.set(row, col, delta.get(row));
            }
        }
        return result;
    }
}
