package com.example.algorithm;

import com.example.algorithm.common.ChooseAlternativePairService;
import com.example.algorithm.common.CompareAlternativesCalculator;
import com.example.algorithm.common.FindConflictService;
import com.example.algorithm.common.ForecastFunctionCalculator;
import com.example.algorithm.context.DataContext;
import com.example.algorithm.entity.AlternativePair;
import com.example.algorithm.entity.ForecastFunctionEntity;
import com.example.algorithm.entity.Rule;
import lombok.RequiredArgsConstructor;
import lpsolve.LpSolveException;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@SpringBootConfiguration
public class AlgorithmAPI {
    private final ForecastFunctionCalculator ffCalculator;
    private final FindConflictService fcService;
    private final ChooseAlternativePairService capService;
    private final CompareAlternativesCalculator caCalculator;

    public AlternativePair findNonComparablePriorPair(
        ForecastFunctionEntity fFunction, DataContext dataContext) {
        return capService.findPair(fFunction, dataContext);
    }

    public List<Rule> findConflictChainOrNull(DataContext dataContext) {
        return fcService.findConflictChainOrNull(dataContext);
    }

    public ForecastFunctionEntity calculateForecastFunction(
        DataContext dataContext) throws LpSolveException {
        return ffCalculator.calculateForecastFunction(dataContext);
    }

    public List<AlternativePair> calculateCompareAlternatives(
        int k, List<Double> v, AlternativePair pair, DataContext dataContext) throws LpSolveException {
        return caCalculator.getResultVector(k, v, pair, dataContext);
    }
}
