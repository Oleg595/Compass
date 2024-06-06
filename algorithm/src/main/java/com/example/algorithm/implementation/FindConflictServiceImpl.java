package com.example.algorithm.implementation;

import com.example.algorithm.common.FindConflictService;
import com.example.algorithm.implementation.rule.RuleService;
import com.example.algorithm.implementation.rule.SolveSLAUV2;
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
        var solver = new SolveSLAUV2(new AlternativePair(alt, alt), dataContext);
        var answer = solver.getNextOrEmpty();
        while (!answer.isEmpty()) {
            for (var prepareRule : getPrepareRules(answer)) {
                var prepareAlt = prepareRule.getPair().getFirst();
                var conflictRule = new RuleEntity(
                    new AlternativePair(prepareAlt, prepareAlt), RuleSet.PREPARE);
                var chain = ruleService.generateLogicalChainOrNull(conflictRule, dataContext);
                if (chain != null) {
                    return chain;
                }
            }
            answer = solver.getNextOrEmpty();
        }
        return null;
    }
}
