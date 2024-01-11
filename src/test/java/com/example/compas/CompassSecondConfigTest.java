package com.example.compas;

import com.example.compas.algorithm.AlgorithmAPI;
import com.example.compas.entity.AlternativeEntity;
import com.example.compas.service.AlternativeService;
import com.example.compas.service.InitializeDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CompassSecondConfigTest {
    @Autowired
    private InitializeDataService initService;

    @Autowired
    private AlternativeService alternativeService;

    @Autowired
    private AlgorithmAPI algorithmAPI;

    @Test
    public void CompareAlternativesCalculatorTest() {
        var a = new AlternativeEntity("a'", Map.of(
            "Location", "Almost ideal",
            "Type", "Inappropriate"
        ));
        var b = new AlternativeEntity("b'", Map.of(
            "Location", "Below average",
            "Type", "Almost ideal"
        ));

        try {
            initService.init("/config2.json");
            var abDelta = alternativeService.calculateDelta(a, b);
            var forecastFunction = algorithmAPI.calculateForecastFunction();
            var alternativePairs =
                algorithmAPI.calculateCompareAlternatives(2, forecastFunction.getV(), abDelta);
        } catch (IOException e) {
            fail("Инициализация окончилась с ошибками");
        } catch (Exception e) {
            fail("Что-то пошло не так!");
        }
    }
}
