package com.example.console.service;

import com.example.algorithm.AlgorithmAPI;
import com.example.algorithm.context.DataContext;
import com.example.algorithm.entity.AlternativePair;
import lombok.AllArgsConstructor;
import lpsolve.LpSolveException;
import org.springframework.stereotype.Component;

import static java.lang.System.exit;

@Component
@AllArgsConstructor
public class AlgorithmService {
    private final DataContext dataContext;
    private final AlgorithmAPI algorithmAPI;
    private final UserInteractionService cvService;

    private boolean isAnswer(AlternativePair pair) {
        return pair.getFirst().isEqual(pair.getSecond());
    }

    public void runAlgorithm() throws LpSolveException {
        var fFunciton = algorithmAPI.calculateForecastFunction(dataContext);
        var comparePair = algorithmAPI.findNonComparablePriorPair(fFunciton, dataContext);
        var k = 2;
        while (!isAnswer(comparePair)) {
            var compareAlts = algorithmAPI.calculateCompareAlternatives(
                k, fFunciton.getV(), comparePair, dataContext);
            if (compareAlts.isEmpty()) {
                if (k < dataContext.getCriterias().size()) {
                    ++k;
                } else {
                    System.out.println("Алгоритм не смог найти наилучшую альтернативу");
                    exit(-1);
                }
            } else {
                cvService.compareAlternatives(compareAlts.get(0));
                var conflict = algorithmAPI.findConflictChainOrNull(dataContext);
                while (conflict != null) {
                    cvService.solveConflict(conflict);
                    conflict = algorithmAPI.findConflictChainOrNull(dataContext);
                }
            }
            fFunciton = algorithmAPI.calculateForecastFunction(dataContext);
            comparePair = algorithmAPI.findNonComparablePriorPair(fFunciton, dataContext);
        }
        var bestAlt = comparePair.getFirst();
        System.out.println(
            "Наилучшая альтернатива: " + bestAlt.toString(dataContext.getCriteriaNames()));
    }
}
