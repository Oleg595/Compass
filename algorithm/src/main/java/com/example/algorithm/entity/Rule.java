package com.example.algorithm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class Rule {
    private AlternativePair pair;
    private RuleSet set;

    public Rule(Rule toCopy) {
        this.pair = toCopy.getPair();
        this.set = toCopy.getSet();
    }

    public String toString(Set<String> criteriaNames) {
        var result = new StringBuilder();
        if (set == RuleSet.PREPARE) {
            result.append("P(");
        } else {
            result.append("I(");
        }

        var first = pair.getFirst();
        var second = pair.getSecond();
        result
            .append(first.toString(criteriaNames))
            .append(", ")
            .append(second.toString(criteriaNames))
            .append(")");

        return result.toString();
    }
}
