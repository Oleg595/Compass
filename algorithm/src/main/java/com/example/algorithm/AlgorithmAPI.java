package com.example.algorithm;

import com.example.algorithm.common.ForecastFunctionCalculator;
import com.example.algorithm.context.DataContext;
import com.example.algorithm.entity.ForecastFunctionEntity;
import lombok.RequiredArgsConstructor;
import lpsolve.LpSolveException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlgorithmAPI {
    private final ForecastFunctionCalculator ffCalculator;
//    private final CompareAlternativesCalculator caCalculator;

    public ForecastFunctionEntity calculateForecastFunction(
        DataContext dataContext) throws LpSolveException {
        return ffCalculator.calculateForecastFunction(dataContext);
    }

//    public List<AlternativePair> calculateCompareAlternatives(
//        int k, List<Double> v, List<Double> delta) throws LpSolveException {
//        return caCalculator.getResultVector(k, v, delta);
//    }
}
