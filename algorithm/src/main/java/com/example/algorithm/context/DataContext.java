package com.example.algorithm.context;

import com.example.algorithm.entity.AlternativeEntity;
import com.example.algorithm.entity.AlternativePair;
import com.example.algorithm.entity.CriteriaEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataContext {

    private List<AlternativeEntity> alts;
    private List<CriteriaEntity> criterias = new ArrayList<>();
    private List<AlternativePair> P = new ArrayList<>();
    private List<AlternativePair> I = new ArrayList<>();

    public CriteriaEntity getCriteriaByNameOrNull(String name) {
        for (var criteria : criterias) {
            if (criteria.getName().equals(name)) {
                return criteria;
            }
        }
        return null;
    }

    public void addP(AlternativePair statement) {
        P.add(statement);
    }

    public void addI(AlternativePair statement) {
        I.add(statement);
    }

    public void addCriteria(CriteriaEntity criteria) {
        criterias.add(criteria);
    }
}
