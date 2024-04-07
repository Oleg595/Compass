package com.example.algorithm.implementation.rule;

import com.example.algorithm.context.DataContext;
import com.example.algorithm.entity.AlternativeEntity;
import com.example.algorithm.entity.AlternativePair;
import com.example.algorithm.entity.Rule;
import com.example.algorithm.entity.RuleSet;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class RuleService {
    private final SolveSLAUV1 solver;

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

    private List<Rule> generateChain(
        AlternativeEntity currentState, AlternativeEntity endState, List<Rule> rules) {
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

    private boolean checkChain(List<Rule> chain, Rule rule) {
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

    private Rule generateRule(AlternativeEntity state, Rule rule, Set<String> criteriaNames) {
        var first = rule.getPair().getFirst();
        var second = rule.getPair().getSecond();
        for (var name : criteriaNames) {
            if (first.getCriteriaToValue().get(name) == null) {
                var stateValue = state.getCriteriaToValue().get(name);
                var firstMap = new HashMap<>(first.getCriteriaToValue());
                var secondMap = new HashMap<>(second.getCriteriaToValue());
                first = new AlternativeEntity(first.getName(), firstMap);
                second = new AlternativeEntity(second.getName(), secondMap);
                first.getCriteriaToValue().put(name, stateValue);
                second.getCriteriaToValue().put(name, stateValue);
            }
        }
        return new Rule(new AlternativePair(first, second), rule.getSet());
    }

    private void printChain(
        AlternativeEntity startState, AlternativeEntity endState,
        List<Rule> chain, DataContext dataContext) {
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
        var sumRule = new Rule(new AlternativePair(startState, endState), sumRuleSet);
        System.out.println(sumRule.toString(criteriaNames));
    }

    private List<Rule> generateChainFromRulesOrNull(List<Rule> rules, Rule rule, DataContext dataContext) {
        var startState = rule.getPair().getFirst();
        var endState = rule.getPair().getSecond();
        var chain = generateChain(startState, endState, rules);
        if (chain == null || !checkChain(chain, rule)) {
            return null;
        }
//        printChain(startState, endState, chain, dataContext);
        return chain;
    }

    public List<Rule> generateLogicalChainOrNull(Rule rule, DataContext dataContext) {
        var answers = solver.generateAndSolve(
            rule.getPair().getFirst(), rule.getPair().getSecond(), dataContext);
        for (var rules : answers) {
            var chain = generateChainFromRulesOrNull(rules, rule, dataContext);
            if (chain != null) {
                return chain;
            }
        }
//        var solver = SolveSLAUV2.generateTask(rule.getPair().getFirst(), rule.getPair().getSecond(), dataContext);
//        var rules = solver.getNextSolutionOrNull();
//        while (rules != null) {
//            var chain = generateChainFromRulesOrNull(rules, rule, dataContext);
//            if (chain != null) {
//                return chain;
//            }
//            rules = solver.getNextSolutionOrNull();
//        }
        return null;
    }

    public boolean checkRule(Rule rule, DataContext dataContext) {
        return generateLogicalChainOrNull(rule, dataContext) != null;
    }
}
