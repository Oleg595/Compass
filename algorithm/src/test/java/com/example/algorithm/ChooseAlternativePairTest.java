package com.example.algorithm;

import com.example.algorithm.common.ChooseAlternativePairService;
import com.example.algorithm.common.ForecastFunctionCalculator;
import com.example.algorithm.configurator.AlgorithmConfigurator;
import com.example.algorithm.entity.ForecastFunctionEntity;
import com.example.algorithm.factory.ContextFactory;
import com.example.algorithm.utils.AlternativeUtils;
import lpsolve.LpSolveException;
import org.example.AlternativeComparsionEntity;
import org.example.AlternativeEntity;
import org.example.DataContext;
import org.example.RuleEntity;
import org.example.RuleSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AlgorithmConfigurator.class)
public class ChooseAlternativePairTest {
    @Autowired
    private ForecastFunctionCalculator ffCalculator;

    @Autowired
    private ChooseAlternativePairService capService;

    @Autowired
    private ContextFactory contextFactory;

    private void printAlts(
        AlternativeEntity first, AlternativeEntity second, DataContext dataContext) {
        System.out.println("Несравнимые выбранные альтернативы:");
        System.out.println(first.toStringWithName(dataContext.getCriteriaNames()));
        System.out.println(second.toStringWithName(dataContext.getCriteriaNames()));
    }

    private void printRuleSign(RuleSet set) {
        if (set == RuleSet.PREPARE) {
            System.out.println(" лучше альтернативы ");
        }
        if (set == RuleSet.EQUAL) {
            System.out.println(" эквивалентно альтернативе ");
        }
    }

    private AlternativeEntity useRuleAndPrint(AlternativeEntity alt, RuleEntity rule, DataContext dataContext) {
        var criteriaNames = dataContext.getCriteriaNames();
        System.out.print(alt.toString(criteriaNames) + " согласно правилу:\n " + rule.prettyOut(criteriaNames));
        printRuleSign(rule.getSet());
        var toAlt = alt.copy();
        var secondRuleAlt = rule.getPair().getSecond();
        for (var name : criteriaNames) {
            if (secondRuleAlt.getCriteriaToValue().get(name) != null) {
                var value = secondRuleAlt.getCriteriaToValue().get(name);
                toAlt.getCriteriaToValue().put(name, value);
            }
        }
        System.out.println(toAlt.toString(criteriaNames));
        System.out.println();
        return toAlt;
    }

    private void prettyOutLogicalChain(AlternativeComparsionEntity chain, DataContext dataContext) {
        var criteriaNames = dataContext.getCriteriaNames();
        var best = chain.getRule().getPair().getFirst();
        var secondary = chain.getRule().getPair().getSecond();
        var set = chain.getRule().getSet();

        var setName = " эквивалентно ";
        if (set == RuleSet.PREPARE) {
            setName = " лучше ";
        }

        System.out.println(
            best.toStringWithName(criteriaNames) + setName
                + secondary.toStringWithName(criteriaNames) + ":\n");

        for (var rule : chain.getOutputRules()) {
            best = useRuleAndPrint(best, rule, dataContext);
        }
        System.out.println(best.toString(dataContext.getCriteriaNames()));
        System.out.println("Конец вывода цепочки правил");
        System.out.println();
    }

    private void outputLogicalChain(AlternativeEntity alt, DataContext dataContext) {
        var secondaryAlts = new ArrayList<AlternativeEntity>();
        var altChains =
            dataContext.getAltsComparsion().stream()
                .filter(it -> it.component1().getPair().getFirst().isEqual(alt))
                .collect(Collectors.toList());
        for (var chain : altChains) {
            prettyOutLogicalChain(chain, dataContext);
            secondaryAlts.add(chain.getRule().getPair().getSecond());
        }
        for (var secAlt : secondaryAlts) {
            outputLogicalChain(secAlt, dataContext);
        }
    }

    private void printFunctionValueForAlt(
        ForecastFunctionEntity fFunction, AlternativeEntity alt, DataContext dataContext) {
        var criteriaNames  = dataContext.getCriteriaNames();
        var delta = AlternativeUtils.calculateDelta(alt, dataContext.getCriterias());
        var value = fFunction.apply(delta);
        System.out.println(
            "Значение функции прогнозирования для " + alt.toStringWithName(criteriaNames) + " = " + value);
    }

    @Test
    public void testChooseAlternativePair1() throws LpSolveException {
        var dataContext = contextFactory.createContext1();
        var criteriaNames = ContextFactory.getCriteriaNames();
        var values = ContextFactory.getCriteriaValues();

        // Альтернатива 4 приоритетнее, так как имеет лучшие значения по 2 и 3 критериям
        var criteriaToValue1 = Map.of(
            criteriaNames.get(0), values.get(0),
            criteriaNames.get(1), values.get(2),
            criteriaNames.get(2), values.get(1)
        );
        // Альтернатива 5 приоритетнее, так как имеется правило, согласно которому
        // лучше 1-ое значение по 1-ому критерию и 2-ое по 3-ему, чем 2-ое по 1-ому
        // и 1-ое по 3-ему
        var criteriaToValue2 = Map.of(
            criteriaNames.get(0), values.get(1),
            criteriaNames.get(1), values.get(0),
            criteriaNames.get(2), values.get(0)
        );
        // 2-ая альтернатива приоритетнее, так как имеет лучшее значение по 1-ому критерию
        var criteriaToValue3 = Map.of(
            criteriaNames.get(0), values.get(2),
            criteriaNames.get(1), values.get(0),
            criteriaNames.get(2), values.get(0)
        );
        // 5-ая альтернатива приоритетнее, согласно правилу, что лучше
        // 1-ое значение по 2-ому критерию и 2-ое по 3-ему, чем
        // 2-ое по 2-ому критерию и 1-ое по 3-ему
        var criteriaToValue4 = Map.of(
            criteriaNames.get(0), values.get(0),
            criteriaNames.get(1), values.get(1),
            criteriaNames.get(2), values.get(0)
        );
        // Самая приоритетная альтернатива
        var criteriaToValue5 = Map.of(
            criteriaNames.get(0), values.get(0),
            criteriaNames.get(1), values.get(0),
            criteriaNames.get(2), values.get(1)
        );

        var alt1 = new AlternativeEntity(1, "Alt1", criteriaToValue1);
        var alt2 = new AlternativeEntity(2, "Alt2", criteriaToValue2);
        var alt3 = new AlternativeEntity(3, "Alt3", criteriaToValue3);
        var alt4 = new AlternativeEntity(4, "Alt4", criteriaToValue4);
        var alt5 = new AlternativeEntity(5, "Alt5", criteriaToValue5);

        dataContext.setAlts(List.of(alt1, alt2, alt3, alt4, alt5));

        var fFunction = ffCalculator.calculateForecastFunction(dataContext);
        var result = capService.findPair(fFunction, dataContext);
        // Ожидается, что каждый элемент пары - 5ая альтернатива
        Assertions.assertEquals(result.getFirst(), alt5);
        Assertions.assertEquals(alt5, result.getSecond());
        printAlts(result.getFirst(), result.getSecond(), dataContext);

        outputLogicalChain(result.getFirst(), dataContext);
        outputLogicalChain(result.getSecond(), dataContext);
        for (var alt : dataContext.getNonPriorAlts()) {
            printFunctionValueForAlt(fFunction, alt, dataContext);
        }
    }

    @Test
    public void testChooseAlternativePair2() throws LpSolveException {
        var dataContext = contextFactory.createContext2();
        var criteriaNames = ContextFactory.getCriteriaNames();
        var values = ContextFactory.getCriteriaValues();

        // Все альтернативы являются несравнимыми по выявленным оценкам
        var criteriaToValue1 = Map.of(
            criteriaNames.get(0), values.get(0),
            criteriaNames.get(1), values.get(2),
            criteriaNames.get(2), values.get(2)
        );
        var criteriaToValue2 = Map.of(
            criteriaNames.get(0), values.get(1),
            criteriaNames.get(1), values.get(0),
            criteriaNames.get(2), values.get(2)
        );
        var criteriaToValue3 = Map.of(
            criteriaNames.get(0), values.get(2),
            criteriaNames.get(1), values.get(1),
            criteriaNames.get(2), values.get(1)
        );

        var alt1 = new AlternativeEntity(1, "Alt1", criteriaToValue1);
        var alt2 = new AlternativeEntity(2, "Alt2", criteriaToValue2);
        var alt3 = new AlternativeEntity(3, "Alt3", criteriaToValue3);

        dataContext.setAlts(List.of(alt1, alt2, alt3));

        var fFunction = ffCalculator.calculateForecastFunction(dataContext);
        var result = capService.findPair(fFunction, dataContext);
        // Должна быть возвращена пара различных альтернатив
        Assertions.assertNotEquals(result.getFirst(), result.getSecond());
        // По результатм функции прогнозирования, наиболее приоритетными должны
        // быть альтернативы 2 и 1
        Assertions.assertEquals(result.getFirst(), alt2);
        Assertions.assertEquals(result.getSecond(), alt1);
        printAlts(result.getFirst(), result.getSecond(), dataContext);

        outputLogicalChain(result.getFirst(), dataContext);
        outputLogicalChain(result.getSecond(), dataContext);
        for (var alt : dataContext.getNonPriorAlts()) {
            printFunctionValueForAlt(fFunction, alt, dataContext);
        }
    }

    @Test
    public void testChooseAlternativePair3() throws LpSolveException {
        var dataContext = contextFactory.createContext3();
        var criteriaNames = ContextFactory.getCriteriaNames();
        var values = ContextFactory.getCriteriaValues();

        // Все альтернативы являются эквивалентными
        var criteriaToValue1 = Map.of(
            criteriaNames.get(0), values.get(0),
            criteriaNames.get(1), values.get(1),
            criteriaNames.get(2), values.get(2)
        );
        var criteriaToValue2 = Map.of(
            criteriaNames.get(0), values.get(2),
            criteriaNames.get(1), values.get(2),
            criteriaNames.get(2), values.get(0)
        );
        var criteriaToValue3 = Map.of(
            criteriaNames.get(0), values.get(2),
            criteriaNames.get(1), values.get(1),
            criteriaNames.get(2), values.get(1)
        );

        var alt1 = new AlternativeEntity(1, "Alt1", criteriaToValue1);
        var alt2 = new AlternativeEntity(2, "Alt2", criteriaToValue2);
        var alt3 = new AlternativeEntity(3, "Alt3", criteriaToValue3);

        dataContext.setAlts(List.of(alt1, alt2, alt3));

        var fFunction = ffCalculator.calculateForecastFunction(dataContext);
        var result = capService.findPair(fFunction, dataContext);
        // Должна быть возвращена пара одинаковых альтернатив, равных 1-ой альтернативе
        // Для всех альтернатив функция прогнозирования принимает значение 3, но все они эквивалентны
        Assertions.assertEquals(result.getFirst(), result.getSecond());
        Assertions.assertEquals(result.getFirst(), alt1);
        printAlts(result.getFirst(), result.getSecond(), dataContext);

        outputLogicalChain(result.getFirst(), dataContext);
        outputLogicalChain(result.getSecond(), dataContext);
        for (var alt : dataContext.getNonPriorAlts()) {
            printFunctionValueForAlt(fFunction, alt, dataContext);
        }
    }
}
