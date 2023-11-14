package com.example.compas;

import com.example.compas.context.DataContext;
import com.example.compas.service.AlgorithmService;
import com.example.compas.service.CompareValueService;
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
    private final AlgorithmService algorithmService;
    private final CompareValueService compareValueService;

    @Override
    public void run(String... args) throws Exception {
        initService.init("/config.json");
        var k = 2;
        while (k <= dataContext.getCriterias().size() && algorithmService.getOptimal() == null) {
            var alts = algorithmService.calculateCompareAlts(k);
            compareValueService.processAlternatives(alts);
            ++k;
        }
        System.out.println("Оптимальная альтернатива: " + algorithmService.getOptimal());
    }

    public static void main(String[] args) {
        SpringApplication.run(CompassRunner.class, args);
    }
}
