package com.example.console.service;

import com.example.algorithm.AlgorithmAPI;
import com.example.algorithm.implementation.rule.RuleService;
import com.example.console.entity.StatisticsEntity;
import lombok.AllArgsConstructor;
import lpsolve.LpSolveException;
import org.example.AlternativeComparsionEntity;
import org.example.AlternativeEntity;
import org.example.AlternativePair;
import org.example.DataContext;
import org.example.RuleEntity;
import org.example.RuleSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class AlgorithmService {
    private final DataContext dataContext;
    private final AlgorithmAPI algorithmAPI;
    private final RuleService ruleService;
    private final UserInteractionService cvService;

    private boolean hasAnswer() {
        return dataContext.getNonPriorAlts().size() == 1;
    }

    private void printRuleSign(RuleSet set) {
        if (set == RuleSet.PREPARE) {
            System.out.println(" лучше альтернативы ");
        }
        if (set == RuleSet.EQUAL) {
            System.out.println(" эквивалентно альтернативе ");
        }
    }

    private AlternativeEntity useRuleAndPrint(AlternativeEntity alt, RuleEntity rule) {
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

    private void prettyOutLogicalChain(AlternativeComparsionEntity chain) {
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
            best = useRuleAndPrint(best, rule);
        }
        System.out.println(best.toString(dataContext.getCriteriaNames()));
        System.out.println("Конец вывода цепочки правил");
        System.out.println();
    }

    private void outputLogicalChain(AlternativeEntity alt) {
        var secondaryAlts = new ArrayList<AlternativeEntity>();
        var altChains =
            dataContext.getAltsComparsion().stream()
                .filter(it -> it.component1().getPair().getFirst().isEqual(alt))
                .collect(Collectors.toList());
        for (var chain : altChains) {
            prettyOutLogicalChain(chain);
            secondaryAlts.add(chain.getRule().getPair().getSecond());
        }
        for (var secAlt : secondaryAlts) {
            outputLogicalChain(secAlt);
        }
    }

    private Map<AlternativeEntity, Integer> rangeAlts(
        AlternativeEntity best, List<AlternativeComparsionEntity> comparsions, int curRange) {
        var prepare = new ArrayList<AlternativeEntity>();
        var equal = new ArrayList<AlternativeEntity>();
        var result = new HashMap<AlternativeEntity, Integer>();
        var newComparsions = new ArrayList<AlternativeComparsionEntity>();
        result.put(best, curRange);
        for (var comparsion : comparsions) {
            var rule = comparsion.getRule();
            if (rule.getSet() == RuleSet.PREPARE && rule.getPair().getFirst().isEqual(best)) {
                prepare.add(rule.getPair().getSecond());
            } else if (rule.getSet() == RuleSet.EQUAL && rule.getPair().getFirst().isEqual(best)) {
                equal.add(rule.getPair().getSecond());
            } else if (rule.getSet() == RuleSet.EQUAL && rule.getPair().getSecond().isEqual(best)) {
                equal.add(rule.getPair().getFirst());
            } else {
                newComparsions.add(comparsion);
            }
        }
        equal.forEach(it -> result.putAll(rangeAlts(it, newComparsions, curRange)));
        prepare.forEach(it -> result.putAll(rangeAlts(it, newComparsions, curRange + 1)));
        return result;
    }

    private void printRange(AlternativeEntity best) {
        var rangeAlts = rangeAlts(best, dataContext.getAltsComparsion(), 1);
        var curRange = 1;
        var curAlts = new ArrayList<AlternativeEntity>();
        var rangeToAlts = new HashMap<Integer, List<AlternativeEntity>>();
        while (curRange == 1 || !curAlts.isEmpty()) {
            curAlts.clear();
            for (var alt : rangeAlts.keySet()) {
                if (rangeAlts.get(alt) == curRange) {
                    curAlts.add(alt);
                }
            }
            for (var alt : curAlts) {
                System.out.println("Ранг альтернативы " + alt.getName() + " : " + curRange);
            }
            rangeToAlts.put(curRange, new ArrayList<>(curAlts));
            curRange++;
        }
        var nonComparableCount = 0;
        for (var range = 1; range < curRange; ++range) {
            var equal = new ArrayList<>(rangeToAlts.get(range));
            var prepare = new ArrayList<AlternativeEntity>();
            for (var r = range + 1; r < curRange; ++r) {
                prepare.addAll(rangeToAlts.get(r));
            }
            for (var i = 0; i < equal.size(); ++i) {
                for (var j = i + 1; j < equal.size(); ++j) {
                    var pair = new AlternativePair(equal.get(i), equal.get(j));
                    if (!ruleService.checkRule(new RuleEntity(pair, RuleSet.EQUAL), dataContext)) {
                        nonComparableCount++;
                    }
                }
                for (var j = 0; j < prepare.size(); ++j) {
                    var pair = new AlternativePair(equal.get(i), prepare.get(j));
                    if (!ruleService.checkRule(new RuleEntity(pair, RuleSet.PREPARE), dataContext)) {
                        nonComparableCount++;
                    }
                }
            }
        }
        System.out.println("Количество несравнимых пар альтернатив: " + nonComparableCount);
    }

    public StatisticsEntity runAlgorithm() throws LpSolveException {
        var k = 2;
        var numQuest = 0;
        var start = System.currentTimeMillis();
        var time = 0L;
        var compareAlts = algorithmAPI.calculateCompareAlternatives(dataContext, k);
        while (!hasAnswer()) {
            if (compareAlts.isEmpty()) {
                if (k < dataContext.getCriterias().size()) {
                    ++k;
                } else {
//                    System.out.println("Алгоритм не смог найти наилучшую альтернативу");
//                    for (var alt : dataContext.getNonPriorAlts()) {
//                        outputLogicalChain(alt);
//                    }
                    return null;
//                        System.out.println(System.currentTimeMillis() - start);
//                        exit(-1);
                }
            } else {
                for (var pair : compareAlts) {
                    time += System.currentTimeMillis() - start;
                    cvService.compareAlternatives(pair);
                    numQuest++;
                    start = System.currentTimeMillis();
                }
                var conflict = algorithmAPI.findConflictChainOrNull(dataContext);
                while (conflict != null) {
                    time += System.currentTimeMillis() - start;
                    cvService.solveConflict(conflict);
                    start = System.currentTimeMillis();
                    conflict = algorithmAPI.findConflictChainOrNull(dataContext);
                }
            }
            compareAlts = algorithmAPI.calculateCompareAlternatives(dataContext, k);
        }
        var bestAlt = dataContext.getNonPriorAlts().get(0);
        System.out.println(
            "Наилучшая альтернатива:\n" + bestAlt.toStringWithName(dataContext.getCriteriaNames()));
        outputLogicalChain(bestAlt);
        printRange(bestAlt);
        time += System.currentTimeMillis() - start;
        return new StatisticsEntity(numQuest, time);
    }
}
