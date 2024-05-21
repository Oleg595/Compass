package org.example.back.configuration;

import com.example.algorithm.AlgorithmAPI;
import com.example.algorithm.common.ChooseAlternativePairService;
import com.example.algorithm.common.CompareAlternativesCalculator;
import com.example.algorithm.common.FindConflictService;
import com.example.algorithm.common.ForecastFunctionCalculator;
import com.example.algorithm.implementation.ChooseAlternativePairServiceImpl;
import com.example.algorithm.implementation.CompareAlternativesCalculatorImpl;
import com.example.algorithm.implementation.FindConflictServiceImpl;
import com.example.algorithm.implementation.ForecastFunctionCalculatorImpl;
import com.example.algorithm.implementation.rule.RuleService;
import com.example.algorithm.implementation.rule.SolveSLAU;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@ConfigurationProperties("service")
public class AlgorithmProperties {
    @Bean
    public ForecastFunctionCalculator forecastFunctionCalculator() {
        return new ForecastFunctionCalculatorImpl();
    }

    @Bean
    public SolveSLAU solveSLAU() {
        return new SolveSLAU();
    }

    @Bean
    public RuleService ruleService(SolveSLAU solveSLAU) {
        return new RuleService(solveSLAU);
    }

    @Bean
    public FindConflictService findConflictService(
        RuleService ruleService, SolveSLAU solveSLAU) {
        return new FindConflictServiceImpl(ruleService, solveSLAU);
    }

    @Bean
    public ChooseAlternativePairService chooseAlternativePairService(
        RuleService ruleService) {
        return new ChooseAlternativePairServiceImpl(ruleService);
    }

    @Bean
    public CompareAlternativesCalculator compareAlternativesCalculator() {
        return new CompareAlternativesCalculatorImpl();
    }

    @Bean
    public AlgorithmAPI algorithmAPI(
        ForecastFunctionCalculator ffCalculator, FindConflictService fcService,
        ChooseAlternativePairService capService, CompareAlternativesCalculator caCalculator
    ) {
        return new AlgorithmAPI(ffCalculator, fcService, capService, caCalculator);
    }
}
