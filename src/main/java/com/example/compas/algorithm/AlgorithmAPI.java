package com.example.compas.algorithm;

import com.example.compas.entity.AlternativePair;
import com.example.compas.entity.ForecastFunctionEntity;
import lombok.RequiredArgsConstructor;
import lpsolve.LpSolveException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlgorithmAPI {
    private final ForecastFunctionCalculator ffCalculator;
    private final CompareAlternativesCalculator caCalculator;

    public ForecastFunctionEntity calculateForecastFunction() throws LpSolveException {
        return ffCalculator.calculateForecastFunction();
    }

    public List<AlternativePair> calculateCompareAlternatives(
        int k, List<Double> v, List<Double> delta) throws LpSolveException {
        return caCalculator.getResultVector(k, v, delta);
    }
}
