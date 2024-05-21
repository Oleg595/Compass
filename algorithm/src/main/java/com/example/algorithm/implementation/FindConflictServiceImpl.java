package com.example.algorithm.implementation;

import com.example.algorithm.common.FindConflictService;
import com.example.algorithm.implementation.rule.RuleService;
import com.example.algorithm.implementation.rule.SolveSLAU;
import lombok.AllArgsConstructor;
import org.example.AlternativeEntity;
import org.example.AlternativePair;
import org.example.DataContext;
import org.example.RuleEntity;
import org.example.RuleSet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class FindConflictServiceImpl implements FindConflictService {
    private final RuleService ruleService;
    private final SolveSLAU solveSLAU;

    private List<RuleEntity> getPrepareRules(List<RuleEntity> rules) {
        var result = new ArrayList<RuleEntity>();
        for (var rule : rules) {
            if (rule.getSet() == RuleSet.PREPARE) {
                result.add(rule);
            }
        }
        return result;
    }

    @Override
    public List<RuleEntity> findConflictChainOrNull(DataContext dataContext) {
        var alt = new AlternativeEntity(0, "Zero alt", Map.of());
//        var startTime = System.currentTimeMillis();
//        var solver = SolveSLAUV2.generateTask(alt, alt, dataContext);
//        var rules = solver.getNextSolutionOrNull();
//        if (rules != null) {
//            var conflictRule = new Rule(new AlternativePair(
//                lastAnswer.getPair().getFirst(), lastAnswer.getPair().getFirst()), RuleSet.PREPARE);
//            var chain = ruleService.generateLogicalChainOrNull(conflictRule, dataContext);
//            if (chain != null) {
//                System.out.println("Время поиска цепи: " + (System.currentTimeMillis() - startTime) + " миллисекунд");
//                return chain;
//            }
//        }
        var answers = solveSLAU.generateAndSolve(alt, alt, dataContext);
        for (var rules : answers) {
            for (var prepareRule : getPrepareRules(rules)) {
                var prepareAlt = prepareRule.getPair().getFirst();
                var conflictRule = new RuleEntity(
                    new AlternativePair(prepareAlt, prepareAlt), RuleSet.PREPARE);
                var chain = ruleService.generateLogicalChainOrNull(conflictRule, dataContext);
                if (chain != null) {
                    return chain;
                }
            }

        }
        return null;
    }
}
