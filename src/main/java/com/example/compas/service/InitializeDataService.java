package com.example.compas.service;

import com.example.compas.context.DataContext;
import com.example.compas.entity.AlternativeEntity;
import com.example.compas.entity.CriteriaEntity;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.core.util.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class InitializeDataService {
    private final CompareValueService compareValueService;
    private final DataContext dataContext;

    @Data
    private static class ReadData {
        private List<CriteriaEntity> criterias;
        private List<AlternativeEntity> alts;
    }

    public void init(String pathConfigFile) throws IOException {
        String json = IOUtils.toString(new InputStreamReader(
            Objects.requireNonNull(DataContext.class.getResourceAsStream(pathConfigFile))));
        try {
            var gson = new Gson();
            var context = gson.fromJson(json, ReadData.class);
            dataContext.setCriterias(context.getCriterias());
            dataContext.setAlts(context.getAlts());
            compareValueService.processCriterias();
        } catch (JsonSyntaxException e) {
            System.out.println("Невозможно прочитать конфигурационный файл");
            System.exit(-1);
        }
    }
}
