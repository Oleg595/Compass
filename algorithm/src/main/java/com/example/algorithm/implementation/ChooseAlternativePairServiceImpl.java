package com.example.algorithm.implementation;

import com.example.algorithm.common.ChooseAlternativePairService;
import com.example.algorithm.context.DataContext;
import com.example.algorithm.entity.AlternativeEntity;
import com.example.algorithm.entity.AlternativePair;
import com.example.algorithm.entity.ForecastFunctionEntity;
import com.example.algorithm.entity.Rule;
import com.example.algorithm.entity.RuleSet;
import com.example.algorithm.implementation.rule.RuleService;
import com.example.algorithm.utils.AlternativeUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class ChooseAlternativePairServiceImpl implements ChooseAlternativePairService {
    private final RuleService crService;

    private List<AlternativeEntity> generateBSet(DataContext dataContext) {
        var A = new ArrayList<>(dataContext.getAlts());
        var B = new ArrayList<AlternativeEntity>();
        for (var alt : dataContext.getAlts()) {
            var havePrior = false;
            for (var aAlt : A) {
                if (aAlt != alt) {
                    var rule = new Rule(new AlternativePair(aAlt, alt), RuleSet.PREPARE);
                    if (crService.checkRule(rule, dataContext)) {
                        havePrior = true;
                        break;
                    }
                }
            }
            if (!havePrior) {
                B.add(alt);
            }
        }
        return B;
    }

    private AlternativeEntity findMorePrior(
        List<AlternativeEntity> altSet, ForecastFunctionEntity fFunction,
        DataContext dataContext) {
        var maxValue = -1.0;
        var maxIndex = -1;
        for (var index = 0; index < altSet.size(); ++index) {
            var delta = AlternativeUtils.calculateDelta(
                altSet.get(index), dataContext.getCriterias());
            var ffValue = fFunction.apply(delta);
            if (ffValue > maxValue) {
                maxValue = ffValue;
                maxIndex = index;
            }
        }
        return altSet.get(maxIndex);
    }

    @Override
    public AlternativePair findPair(
        ForecastFunctionEntity fFunction, DataContext dataContext) {
        var B = generateBSet(dataContext);
        var first = findMorePrior(B, fFunction, dataContext);
        B.remove(first);
        while (!B.isEmpty()) {
            var second = findMorePrior(B, fFunction, dataContext);
            var rule = new Rule(new AlternativePair(first, second), RuleSet.EQUAL);
            if (!crService.checkRule(rule, dataContext)) {
                return new AlternativePair(first, second);
            }
            B.remove(second);
        }
        return new AlternativePair(first, first);
    }
}
