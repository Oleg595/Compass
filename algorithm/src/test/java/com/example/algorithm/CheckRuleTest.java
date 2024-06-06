package com.example.algorithm;

import com.example.algorithm.configurator.AlgorithmConfigurator;
import com.example.algorithm.factory.ContextFactory;
import com.example.algorithm.implementation.rule.RuleService;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AlgorithmConfigurator.class)
public class CheckRuleTest {

    @Autowired
    private RuleService crService;

    @Autowired
    private ContextFactory contextFactory;

    // Вывод списка правил
    private void prettyOutRules(List<RuleEntity> rules, DataContext dataContext) {
        var criteriaNames = new HashSet<String>();
        for (var criteria : dataContext.getCriterias()) {
            criteriaNames.add(criteria.getName());
        }
        for (var p : rules) {
            System.out.println(p.toString(criteriaNames));
        }
    }

    private void printTask(RuleEntity ruleToCheck, DataContext dataContext) {
        System.out.println("Множество пар, удовлетворяющих предикату P (прочерк = \\omega):");
        prettyOutRules(dataContext.getP(), dataContext);
        System.out.println("Множество пар, удовлетворяющих предикату I:");
        prettyOutRules(dataContext.getI(), dataContext);
        System.out.println("Проверяемое утверждение:");
        prettyOutRules(List.of(ruleToCheck), dataContext);
    }

    private void printRuleSign(RuleSet set) {
        if (set == RuleSet.PREPARE) {
            System.out.println("                  лучше ");
        }
        if (set == RuleSet.EQUAL) {
            System.out.println("                  эквивалентно ");
        }
    }

    private AlternativeEntity useRuleAndPrint(AlternativeEntity alt, RuleEntity rule, DataContext dataContext) {
        var criteriaNames = dataContext.getCriteriaNames();
        System.out.println(alt.toString(criteriaNames) + " согласно правилу " + rule.toString(criteriaNames));
        printRuleSign(rule.getSet());
        var toAlt = alt.copy();
        var secondRuleAlt = rule.getPair().getSecond();
        for (var name : criteriaNames) {
            if (secondRuleAlt.getCriteriaToValue().get(name) != null) {
                var value = secondRuleAlt.getCriteriaToValue().get(name);
                toAlt.getCriteriaToValue().put(name, value);
            }
        }
        return toAlt;
    }

    private void printResult(
        AlternativeEntity start, List<RuleEntity> rules, DataContext dataContext) {
        System.out.println("Цепочка вывода утверждения:");
        var currentState = start.copy();
        for (var rule : rules) {
            currentState = useRuleAndPrint(currentState, rule, dataContext);
        }
        System.out.println(currentState.toString(dataContext.getCriteriaNames()));
    }

    @Test
    public void testCheckRule1() {
        var dataContext = contextFactory.createContext1();
        var criteriaNames = ContextFactory.getCriteriaNames();
        var values = ContextFactory.getCriteriaValues();

        // Создаём альтернативы A, B:
        var criteriaToValueA = Map.of(
            criteriaNames.get(0), values.get(0),
            criteriaNames.get(1), values.get(0),
            criteriaNames.get(2), values.get(1)
        );
        var A = new AlternativeEntity(-1, "A", criteriaToValueA);
        var criteriaToValueB = Map.of(
            criteriaNames.get(0), values.get(1),
            criteriaNames.get(1), values.get(1),
            criteriaNames.get(2), values.get(0)
        );
        var B = new AlternativeEntity(-1, "B", criteriaToValueB);

        var rule = new RuleEntity(new AlternativePair(A, B), RuleSet.PREPARE);
        printTask(rule, dataContext);

        var chain = crService.generateLogicalChainOrNull(rule, dataContext);
        printResult(A, chain, dataContext);
        Assertions.assertNotNull(chain);
    }

    @Test
    public void testCheckRule2() {
        var dataContext = contextFactory.createContext2();
        var criteriaNames = ContextFactory.getCriteriaNames();
        var values = ContextFactory.getCriteriaValues();

        // Создаём альтернативы A, B:
        var criteriaToValueA = Map.of(
            criteriaNames.get(0), values.get(0),
            criteriaNames.get(1), values.get(2),
            criteriaNames.get(2), values.get(0)
        );
        var A = new AlternativeEntity(-1, "A", criteriaToValueA);
        var criteriaToValueB = Map.of(
            criteriaNames.get(0), values.get(2),
            criteriaNames.get(1), values.get(1),
            criteriaNames.get(2), values.get(1)
        );
        var B = new AlternativeEntity(-1, "B", criteriaToValueB);

        var rule = new RuleEntity(new AlternativePair(A, B), RuleSet.PREPARE);
        printTask(rule, dataContext);

        Assertions.assertTrue(crService.checkRule(rule, dataContext));
    }

    @Test
    public void testCheckRule3() {
        var dataContext = contextFactory.createContext3();
        var criteriaNames = ContextFactory.getCriteriaNames();
        var values = ContextFactory.getCriteriaValues();

        // Создаём альтернативы A, B:
        var criteriaToValueA = Map.of(
            criteriaNames.get(0), values.get(0),
            criteriaNames.get(1), values.get(2),
            criteriaNames.get(2), values.get(0)
        );
        var A = new AlternativeEntity(-1, "A", criteriaToValueA);
        var criteriaToValueB = Map.of(
            criteriaNames.get(0), values.get(2),
            criteriaNames.get(1), values.get(1),
            criteriaNames.get(2), values.get(1)
        );
        var B = new AlternativeEntity(-1, "B", criteriaToValueB);

        var rule = new RuleEntity(new AlternativePair(A, B), RuleSet.PREPARE);
        printTask(rule, dataContext);

        Assertions.assertTrue(crService.checkRule(rule, dataContext));
    }
}
