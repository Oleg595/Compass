package com.example.algorithm.context;

import com.example.algorithm.entity.AlternativeEntity;
import com.example.algorithm.entity.AlternativePair;
import com.example.algorithm.entity.CriteriaEntity;
import com.example.algorithm.entity.Rule;
import com.example.algorithm.entity.RuleSet;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class DataContext {

    private List<AlternativeEntity> alts;
    private List<CriteriaEntity> criterias = new ArrayList<>();
    private List<Rule> P = new ArrayList<>();
    private List<Rule> I = new ArrayList<>();

    public CriteriaEntity getCriteriaByNameOrNull(String name) {
        for (var criteria : criterias) {
            if (criteria.getName().equals(name)) {
                return criteria;
            }
        }
        return null;
    }

    public Set<String> getCriteriaNames() {
        var result = new HashSet<String>();
        for (var criteria : criterias) {
            result.add(criteria.getName());
        }
        return result;
    }

    public void addP(AlternativePair statement) {
        P.add(new Rule(statement, RuleSet.PREPARE));
    }

    public void addI(AlternativePair statement) {
        I.add(new Rule(statement, RuleSet.EQUAL));
    }

    public void removeRule(Rule rule) {
        if (rule.getSet() == RuleSet.EQUAL) {
            I.remove(rule);
        } else {
            P.remove(rule);
        }
    }

    public void addCriteria(CriteriaEntity criteria) {
        criterias.add(criteria);
    }
}
