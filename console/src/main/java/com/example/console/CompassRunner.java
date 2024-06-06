package com.example.console;

import com.example.console.service.AlgorithmService;
import com.example.console.service.InitializeDataService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@SpringBootApplication
public class CompassRunner implements CommandLineRunner {
    private final InitializeDataService initService;
    private final AlgorithmService algorithmService;
    private static final Logger logger = LogManager.getLogger(CompassRunner.class);


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
//    private void forecastFunctionPrettyOut(ForecastFunctionEntity function) {
//        var index = 0;
//        var v = function.getV();
//        System.out.println(
//            "Вывод функции прогнозирования u(a) = (\\delta(a), v^*) :");
//        System.out.println("представляется в виде название критерия + список пар " +
//            "'значение критерия : значение функции прогнозирования'");
//        for (var criteria : dataContext.getCriterias()) {
//            System.out.println("Для критерия " + criteria.getName() + " :");
//            for (var value : criteria.getValues()) {
//                System.out.println(value + " : " + v.get(index));
//                ++index;
//            }
//        }
//    }
//
//    private void printDelta(List<Double> delta) {
//        System.out.print("Дельта-функция: (");
//        for (var val : delta) {
//            System.out.print(val.intValue() + " ");
//        }
//        System.out.println(")^T");
//    }

    // Значения критериев вводятся в виде <название критерия:значение критерия\n>
    // Окончанием ввода является строка состоящая из символа переноса строки
    // В случае ввода несуществующего критерия или значения критерия в оценке не учитывается
    // В случае, если не задан ни один из критериев, возвращает false и ввод оценок заканчивается
//    private boolean calculateForecastFunction(
//        ForecastFunctionEntity function) {
//        System.out.println(
//            "Формат ввода векторной оценки <название критерия:значение критерия> " +
//                "с переходом на другую строку");
//        System.out.println("Окончанием ввода оценки служит пустая строка");
//        System.out.println("Введите значение векторной оценки:");
//        var scanner = new Scanner(System.in);
//        var criteriaToValue = new HashMap<String, String>();
//        var criteriaToValueRow = scanner.nextLine();
//        while (!criteriaToValueRow.isEmpty()) {
//           var parts = criteriaToValueRow.split(":");
//           var criteria = parts[0].trim();
//           var value = parts[1].trim();
//           criteriaToValue.put(criteria, value);
//           criteriaToValueRow = scanner.nextLine();
//        }
//        if (criteriaToValue.isEmpty()) {
//            return false;
//        }
//        var alternative = new AlternativeEntity("User alternative", criteriaToValue);
//        var delta = AlternativeUtils.calculateDelta(alternative, dataContext.getCriterias());
//        printDelta(delta);
//        var ffResult = function.apply(delta);
//        System.out.printf(
//            "Результат применения функции прогнозирования к вектору %f%n", ffResult);
//        return true;
//    }

    @Override
    public void run(String... args) throws Exception {
        var averageQuest = .0;
        var countsNum = 1;
        var averageTime = .0;
        for (var index = 0; index < countsNum; index++) {
            initService.init(args[0]);
            var stat = algorithmService.runAlgorithm();
            initService.clearContext();
            if (stat == null) {
                index--;
                continue;
            }
            averageQuest += (double) stat.getNumQuests() / countsNum;
            averageTime += (double) stat.getTime() / countsNum;
        }
        logger.info("The average number of questions to the user: " + averageQuest);
        logger.info("The average running time of the algorithm: " + averageTime + " ms");
    }

    public static void main(String[] args) {
        SpringApplication.run(CompassRunner.class, args);
    }
}
