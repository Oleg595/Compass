package com.example.algorithm.implementation;

import com.example.algorithm.common.FindConflictService;
import com.example.algorithm.context.DataContext;
import com.example.algorithm.entity.AlternativeEntity;
import com.example.algorithm.entity.AlternativePair;
import com.example.algorithm.entity.Rule;
import com.example.algorithm.entity.RuleSet;
import com.example.algorithm.implementation.rule.RuleService;
import com.example.algorithm.implementation.rule.SolveSLAUV1;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class FindConflictServiceImpl implements FindConflictService {
    private final RuleService ruleService;
    private final SolveSLAUV1 solveSLAU;

    private List<Rule> getPrepareRules(List<Rule> rules) {
        var result = new ArrayList<Rule>();
        for (var rule : rules) {
            if (rule.getSet() == RuleSet.PREPARE) {
                result.add(rule);
            }
        }
        return result;
    }

    @Override
    public List<Rule> findConflictChainOrNull(DataContext dataContext) {
        var alt = new AlternativeEntity("Zero alt", Map.of());
        var startTime = System.currentTimeMillis();
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
        System.out.println("Время решения СЛАУ: " + (System.currentTimeMillis() - startTime) + " миллисекунд");
        startTime = System.currentTimeMillis();
        for (var rules : answers) {
            for (var prepareRule : getPrepareRules(rules)) {
                var prepareAlt = prepareRule.getPair().getFirst();
                var conflictRule = new Rule(
                    new AlternativePair(prepareAlt, prepareAlt), RuleSet.PREPARE);
                var chain = ruleService.generateLogicalChainOrNull(conflictRule, dataContext);
                if (chain != null) {
                    System.out.println("Время поиска цепи: " + (System.currentTimeMillis() - startTime) + " " +
                        "миллисекунд");
                    return chain;
                }
            }
        }
        System.out.println("Время поиска цепи: " + (System.currentTimeMillis() - startTime) + " миллисекунд");
        return null;
    }
}
