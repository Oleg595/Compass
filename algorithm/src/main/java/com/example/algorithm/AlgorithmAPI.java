package com.example.algorithm;

import com.example.algorithm.common.ChooseAlternativePairService;
import com.example.algorithm.common.CompareAlternativesCalculator;
import com.example.algorithm.common.FindConflictService;
import com.example.algorithm.common.ForecastFunctionCalculator;
import lombok.AllArgsConstructor;
import lpsolve.LpSolveException;
import org.example.AlternativePair;
import org.example.DataContext;
import org.example.RuleEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AlgorithmAPI {
    private final ForecastFunctionCalculator ffCalculator;
    private final FindConflictService fcService;
    private final ChooseAlternativePairService capService;
    private final CompareAlternativesCalculator caCalculator;

    public List<RuleEntity> findConflictChainOrNull(DataContext dataContext) {
        return fcService.findConflictChainOrNull(dataContext);
    }

    public List<AlternativePair> calculateCompareAlternatives(DataContext dataContext, int k) throws LpSolveException {
        var forecastFunction = ffCalculator.calculateForecastFunction(dataContext);
        var alternativePair = capService.findPair(forecastFunction, dataContext);
        return caCalculator.getResultVector(k, forecastFunction.getV(), alternativePair, dataContext);
    }
}
