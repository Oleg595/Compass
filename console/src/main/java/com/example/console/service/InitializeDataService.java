package com.example.console.service;

import com.example.algorithm.utils.AlternativeUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.IOUtils;
import org.example.AlternativeEntity;
import org.example.CriteriaEntity;
import org.example.DataContext;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InitializeDataService {
    private final DataContext dataContext;
    private static final Logger logger = LogManager.getLogger(InitializeDataService.class);

    @Data
    private class ReadCriteriaData {
        private final String name;
        private final List<Map<String, Object>> values;
    }

    @Data
    private static class ReadData {
        private List<ReadCriteriaData> criterias;
        private List<AlternativeEntity> alts;
    }

    private void generateMorePriorities(CriteriaEntity criteria, Collection<String> more, Collection<String> less) {
        for (var dominateValue: more) {
            for (var secondaryValue: less) {
                var pair = AlternativeUtils.calculateAlternativePair(
                    dataContext.getCriterias(), List.of(criteria), List.of(dominateValue), List.of(secondaryValue));
                dataContext.addP(pair);
            }
        }
    }

    private void generateEqualPriorities(CriteriaEntity criteria, Collection<String> priorNames) {
        var copyPriorNames = new ArrayList<>(priorNames);
        for (var name: priorNames) {
            copyPriorNames.remove(name);
            for (var newName: copyPriorNames) {
                var pair = AlternativeUtils.calculateAlternativePair(
                    dataContext.getCriterias(), List.of(criteria), List.of(name), List.of(newName));
                dataContext.addI(pair);
            }
        }
    }

    private void addCompares(CriteriaEntity criteria, Map<String, Integer> valueToPrior) {
        var curPrior = 1;
        while (!valueToPrior.isEmpty()) {
           var valuesWithCurPrior = new ArrayList<String>();
           var values = new HashSet<>(valueToPrior.keySet());
           for (var value: values) {
               if (curPrior == valueToPrior.get(value)) {
                   valuesWithCurPrior.add(value);
                   valueToPrior.remove(value);
               }
           }
           generateMorePriorities(criteria, valuesWithCurPrior, valueToPrior.keySet());
           generateEqualPriorities(criteria, valuesWithCurPrior);
           ++curPrior;
        }
    }

    private List<String> getSortValues(ReadCriteriaData readCriteria) {
        final String valueFieldName = "value";
        final String priorFieldName = "priority";

        var valueToPrior = new HashMap<String, Integer>();
        for (var value: readCriteria.getValues()) {
            valueToPrior.put(
                (String)value.get(valueFieldName), ((Double)value.get(priorFieldName)).intValue());
        }

        var result = new ArrayList<String>();
        while (!valueToPrior.isEmpty()) {
            String insertValue = null;
            for (var value : valueToPrior.keySet()) {
                if (insertValue == null || valueToPrior.get(insertValue) > valueToPrior.get(value)) {
                    insertValue = value;
                }
            }
            result.add(insertValue);
            valueToPrior.remove(insertValue);
        }
        return result;
    }

    private void processCriterias(List<ReadCriteriaData> readCriterias) {
        final String valueFieldName = "value";
        final String priorFieldName = "priority";
        for (var readCriteria : readCriterias) {
            var values = getSortValues(readCriteria);
            var criteria = new CriteriaEntity(readCriteria.getName(), new HashSet<>(values), new HashMap<>());
            dataContext.addCriteria(criteria);
        }
        for (var readCriteria: readCriterias) {
            var valueToPrior = new HashMap<String, Integer>();
            for (var value: readCriteria.getValues()) {
                valueToPrior.put(
                    (String)value.get(valueFieldName), ((Double)value.get(priorFieldName)).intValue());
            }
            var criteria = dataContext.getCriteriaByNameOrNull(readCriteria.getName());
            addCompares(criteria, valueToPrior);
        }
    }

    public void init(String pathConfigFile) throws IOException {
        String json = IOUtils.toString(new InputStreamReader(new FileInputStream(pathConfigFile)));
        try {
            var gson = new Gson();
            var context = gson.fromJson(json, ReadData.class);
            processCriterias(context.getCriterias());
            dataContext.setAlts(context.getAlts());
        } catch (JsonSyntaxException e) {
            logger.error("The configuration file cannot be read");
            System.exit(-1);
        }
    }

    public void clearContext() {
        dataContext.getAltsComparsion().clear();
        dataContext.getI().clear();
        dataContext.getP().clear();
        dataContext.getAlts().clear();
        dataContext.getCriterias().clear();
    }
}
