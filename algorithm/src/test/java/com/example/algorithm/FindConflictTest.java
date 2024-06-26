package com.example.algorithm;

import com.example.algorithm.common.FindConflictService;
import com.example.algorithm.configurator.AlgorithmConfigurator;
import com.example.algorithm.factory.ContextFactory;
import org.example.AlternativeEntity;
import org.example.AlternativePair;
import org.example.DataContext;
import org.example.RuleEntity;
import org.example.RuleSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AlgorithmConfigurator.class)
public class FindConflictTest {
    @Autowired
    private ContextFactory contextFactory;
    @Autowired
    private FindConflictService fcService;

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
            if (rule.getSet() == RuleSet.PREPARE) {
                currentState = rule.getPair().getSecond();
            } else {
                currentState = rule.getPair().getFirst();
            }
        }
        var sumRuleSet = (prior) ? RuleSet.PREPARE : RuleSet.EQUAL;
        var sumRule = new RuleEntity(new AlternativePair(startState, endState), sumRuleSet);
        System.out.println(sumRule.toString(criteriaNames));
    }

    @Test
    public void findConflictChainTest1() {
        var dataContext = contextFactory.createClearContext();
        var criteriaNames = ContextFactory.getCriteriaNames();
        var values = ContextFactory.getCriteriaValues();

        var criteriaToValueA1 = Map.of(
            criteriaNames.get(0), values.get(0),
            criteriaNames.get(1), values.get(1)
        );
        var criteriaToValueB1 = Map.of(
            criteriaNames.get(0), values.get(1),
            criteriaNames.get(1), values.get(0)
        );
        var estimationA1 = new AlternativeEntity(-1, "EstimationA1", criteriaToValueA1);
        var estimationB1 = new AlternativeEntity(-1, "EstimationB1", criteriaToValueB1);
        dataContext.addP(new AlternativePair(estimationA1, estimationB1));

        var criteriaToValueA2 = Map.of(
            criteriaNames.get(0), values.get(0),
            criteriaNames.get(1), values.get(1)
        );
        var criteriaToValueB2 = Map.of(
            criteriaNames.get(0), values.get(2),
            criteriaNames.get(1), values.get(0)
        );
        var estimationA2 = new AlternativeEntity(-1, "EstimationA2", criteriaToValueA2);
        var estimationB2 = new AlternativeEntity(-1, "EstimationB2", criteriaToValueB2);
        dataContext.addI(new AlternativePair(estimationA2, estimationB2));

        var chain = fcService.findConflictChainOrNull(dataContext);
        // Должна получиться цепочка вывода:
        // P((value1, value2, -), (value2, value1, -)) and P((value2, value1, -), (value3, value1, -))
        // and I((value3, value1, -), (value1, value2, -)) -> P((value1, value2, -), (value1, value2, -))
        Assertions.assertNotNull(chain);
        printChain(estimationA2, estimationA2, chain, dataContext);
    }
}
