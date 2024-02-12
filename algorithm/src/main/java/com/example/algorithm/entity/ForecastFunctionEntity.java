package com.example.algorithm.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.cglib.core.internal.Function;

import java.util.List;

@Getter
@AllArgsConstructor
public class ForecastFunctionEntity implements Function<List<Double>, Double> {
    List<Double> v;

    @Override
    public Double apply(List<Double> delta) {
        assert v.size() == delta.size();
        var result = 0.0;
        for (var index = 0; index < v.size(); ++index) {
            result += v.get(index) * delta.get(index);
        }
        return result;
    }
}
