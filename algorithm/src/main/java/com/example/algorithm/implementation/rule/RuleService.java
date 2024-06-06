package com.example.algorithm.implementation.rule;

import lombok.AllArgsConstructor;
import org.example.AlternativeEntity;
import org.example.AlternativePair;
import org.example.DataContext;
import org.example.RuleEntity;
import org.example.RuleSet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class RuleService {
    private boolean canBeNextState(
        AlternativeEntity currentState, AlternativeEntity nextState) {
        var signCriterias = nextState.getCriteriaToValue().keySet();
        for (var name : signCriterias) {
            var curCriteriaValue = currentState.getCriteriaToValue().get(name);
            var nextCriteriaValue = nextState.getCriteriaToValue().get(name);
            if (curCriteriaValue != null && nextCriteriaValue != null && !curCriteriaValue.equals(nextCriteriaValue)) {
                return false;
            }
        }
        return true;
    }

    private AlternativeEntity generateNextState(
        AlternativeEntity currentState, AlternativeEntity lessPrior) {
        var result = currentState.copy();
        for (var name : lessPrior.getCriteriaToValue().keySet()) {
            var value = lessPrior.getCriteriaToValue().get(name);
            if (value != null) {
                result.getCriteriaToValue().put(name, value);
            }
        }
        return result;
    }

    private List<RuleEntity> generateChain(
        AlternativeEntity currentState, AlternativeEntity endState, List<RuleEntity> rules) {
        if (endState.isEqual(currentState) && rules.isEmpty()) {
            return new ArrayList<>();
        }
        for (var rule : rules) {
            var firstAlt = rule.getPair().getFirst();
            if (canBeNextState(currentState, firstAlt)) {
                var nextState = generateNextState(currentState, rule.getPair().getSecond());
                var newRules = new ArrayList<>(rules);
                newRules.remove(rule);
                var result = generateChain(nextState, endState, newRules);
                if (result != null) {
                    result.add(rule);
                    return result;
                }
            }
        }
        return null;
    }

    private boolean checkChain(List<RuleEntity> chain, RuleEntity rule) {
        if (rule.getSet() == RuleSet.PREPARE) {
            for (var r : chain) {
                if (r.getSet() == RuleSet.PREPARE) {
                    return true;
                }
            }
            return false;
        } else {
            for (var r : chain) {
                if (r.getSet() == RuleSet.PREPARE) {
                    return false;
                }
            }
            return true;
        }
    }

    private RuleEntity generateRule(AlternativeEntity state, RuleEntity rule, Set<String> criteriaNames) {
        var first = rule.getPair().getFirst();
        var second = rule.getPair().getSecond();
        for (var name : criteriaNames) {
            if (first.getCriteriaToValue().get(name) == null) {
                var stateValue = state.getCriteriaToValue().get(name);
                var firstMap = new HashMap<>(first.getCriteriaToValue());
                var secondMap = new HashMap<>(second.getCriteriaToValue());
                first = new AlternativeEntity(first.getId(), first.getName(), firstMap);
                second = new AlternativeEntity(second.getId(), second.getName(), secondMap);
                first.getCriteriaToValue().put(name, stateValue);
                second.getCriteriaToValue().put(name, stateValue);
            }
        }
        return new RuleEntity(new AlternativePair(first, second), rule.getSet());
    }

    private void printChain(
        AlternativeEntity startState, AlternativeEntity endState,
        List<RuleEntity> chain, DataContext dataContext) {
        var criteriaNames = dataContext.getCriteriaNames();
        System.out.println("Цепочка вывода утверждения:");
        System.out.println(startState.toString(criteriaNames) + " -> ");
        var currentState = startState;
        var prior = false;
        for (var index = chain.size() - 1; index >= 0; --index) {
            var rule = generateRule(currentState, chain.get(index), criteriaNames);
            if (rule.getSet() == RuleSet.PREPARE) {
                prior = true;
            }
            System.out.print(chain.get(index).toString(criteriaNames) + " -> ");
            System.out.println(rule.toString(criteriaNames) + " -> ");
            currentState = rule.getPair().getSecond();
        }
        var sumRuleSet = (prior) ? RuleSet.PREPARE : RuleSet.EQUAL;
        var sumRule = new RuleEntity(new AlternativePair(startState, endState), sumRuleSet);
        System.out.println(sumRule.toString(criteriaNames));
    }

    private List<RuleEntity> generateChainFromRulesOrNull(List<RuleEntity> rules, RuleEntity rule) {
        var startState = rule.getPair().getFirst();
        var endState = rule.getPair().getSecond();
        var chain = generateChain(startState, endState, rules);
        if (chain == null || !checkChain(chain, rule)) {
            return null;
        }
        return chain;
    }

    public List<RuleEntity> generateLogicalChainOrNull(RuleEntity rule, DataContext dataContext) {
        var sol = new SolveSLAUV2(rule.getPair(), dataContext);
        var answer = sol.getNextOrEmpty();
        while (!answer.isEmpty()) {
            var chain = generateChainFromRulesOrNull(answer, rule);
            if (chain != null) {
                return chain;
            }
            answer = sol.getNextOrEmpty();
        }
        return null;
    }

    public boolean checkRule(RuleEntity rule, DataContext dataContext) {
        return generateLogicalChainOrNull(rule, dataContext) != null;
    }
}
