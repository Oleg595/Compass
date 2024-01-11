package com.example.compas;

import com.example.compas.algorithm.AlgorithmAPI;
import com.example.compas.context.DataContext;
import com.example.compas.entity.ForecastFunctionEntity;
import com.example.compas.service.InitializeDataService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@SpringBootApplication
public class CompassRunner implements CommandLineRunner {
    private final DataContext dataContext;
    private final InitializeDataService initService;
    private final AlgorithmAPI algorithmAPI;


//    private final AlgorithmService algorithmService;
//    private final CompareValueService compareValueService;

//    @Override
//    public void run(String... args) throws Exception {
//        initService.init("/config.json");
//        var k = 2;
//        while (k <= dataContext.getCriterias().size() && algorithmService.getOptimal() == null) {
//            var alts = algorithmService.calculateCompareAlts(k);
//            compareValueService.processAlternatives(alts);
//            ++k;
//        }
//        System.out.println("Оптимальная альтернатива: " + algorithmService.getOptimal());
//    }

    // Функция прогнозирования имеет следующий вид: u(a) = ()
    // выводим пары: значение критерия в дельта-функции \delta(a)_i^j -
    // соответствующее значение вектора функции прогнозирования
    private void forecastFunctionPrettyOut(ForecastFunctionEntity function) {
        var index = 0;
        var v = function.getV();
        System.out.println(
            "Вывод функции прогнозирования u(a) = (\\delta(a), v^*) :");
        System.out.println("представляется в виде название критерия + список пар " +
            "'значение критерия : значение функции прогнозирования'");
        for (var criteria : dataContext.getCriterias()) {
            System.out.println("Для критерия " + criteria.getName() + " :");
            for (var value : criteria.getValues()) {
                System.out.println(value + " : " + v.get(index));
                ++index;
            }
        }
    }

    @Override
    public void run(String... args) throws Exception {
//        initService.init("/config.json");
        initService.init(args[0]);
        var forecastFunction = algorithmAPI.calculateForecastFunction();
        forecastFunctionPrettyOut(forecastFunction);
    }

    public static void main(String[] args) {
        SpringApplication.run(CompassRunner.class, args);
    }
}
