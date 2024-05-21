package com.example.console.service;

import com.example.algorithm.AlgorithmAPI;
import lombok.AllArgsConstructor;
import lpsolve.LpSolveException;
import org.example.DataContext;
import org.springframework.stereotype.Component;

import static java.lang.System.exit;

@Component
@AllArgsConstructor
public class AlgorithmService {
    private final DataContext dataContext;
    private final AlgorithmAPI algorithmAPI;
    private final UserInteractionService cvService;

    private boolean hasAnswer() {
        return dataContext.getNonPriorAlts().size() == 1;
    }

    public void runAlgorithm() throws LpSolveException {
        var k = 2;
        var compareAlts = algorithmAPI.calculateCompareAlternatives(dataContext, k);
        while (!hasAnswer()) {
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
            compareAlts = algorithmAPI.calculateCompareAlternatives(dataContext, k);
        }
        var bestAlt = dataContext.getNonPriorAlts().get(0);
        System.out.println(
            "Наилучшая альтернатива: " + bestAlt.toStringWithName(dataContext.getCriteriaNames()));
    }
}
