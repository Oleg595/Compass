package com.example.console;


import com.example.algorithm.AlgorithmAPI;
import com.example.algorithm.context.DataContext;
import com.example.algorithm.entity.AlternativeEntity;
import com.example.algorithm.entity.ForecastFunctionEntity;
import com.example.algorithm.utils.AlternativeUtils;
import com.example.console.service.InitializeDataService;
import org.junit.jupiter.api.Assertions;
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
public class CompassFirstConfigTest {

    @Autowired
    private DataContext dataContext;

    @Autowired
    private InitializeDataService initService;

    @Autowired
    private AlgorithmAPI algorithmAPI;

    // Функция прогнозирования имеет следующий вид: u(a) = ()
    // выводим пары: значение критерия в дельта-функции \delta(a)_i^j -
    // соответствующее значение вектора функции прогнозирования
    private void forecastFunctionPrettyOut(ForecastFunctionEntity function) {
        var index = 0;
        var v = function.getV();
        for (var criteria : dataContext.getCriterias()) {
            System.out.println("Для критерия " + criteria.getName() + " :");
            for (var value : criteria.getValues()) {
                System.out.println(value + " : " + v.get(index));
                ++index;
            }
        }
    }

    @Test
    public void FormalizationTest() {
        try {
            initService.init("/config1.json");
        } catch (IOException e) {
            fail("Инициализация окончилась с ошибками");
        }
    }

    @Test
    public void ForecastFunctionCalculatorTest() {
        try {
            initService.init("/config1.json");
            var forecastFunction = algorithmAPI.calculateForecastFunction(dataContext);
            // Ожидаемое значение вектора v
            var expectResult = new Double[] {2., 1., 0., 2., 1., 0., 3., 2., 1., 0.};
            // Сравнение ожидаемого и полученного значений
            Assertions.assertArrayEquals(
                forecastFunction.getV().toArray(new Double[0]), expectResult);
            // Вывод функции прогнозирования
            forecastFunctionPrettyOut(forecastFunction);
        } catch (IOException e) {
            fail("Инициализация окончилась с ошибками");
        } catch (Exception e) {
            fail("Что-то пошло не так!");
        }
    }

    @Test
    public void CompareAlternativesCalculatorTest() {
        var a = new AlternativeEntity("a'", Map.of(
            "Salary", "Average",
            "Location", "Some distance",
            "Type", "Inappropriate"
        ));
        var b = new AlternativeEntity("b'", Map.of(
            "Salary", "Below average",
            "Location", "Almost ideal",
            "Type", "Good enough"
        ));

        try {
            initService.init("/config1.json");
            var abDelta = AlternativeUtils.calculateDelta(a, b, dataContext.getCriterias());
            var forecastFunction = algorithmAPI.calculateForecastFunction(dataContext);
//            var alternativePairs =
//                algorithmAPI.calculateCompareAlternatives(2, forecastFunction.getV(), abDelta);
        } catch (IOException e) {
            fail("Инициализация окончилась с ошибками");
        } catch (Exception e) {
            fail("Что-то пошло не так!");
        }
    }
}
