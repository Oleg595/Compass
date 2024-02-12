package com.example.algorithm.common;

import com.example.algorithm.context.DataContext;
import com.example.algorithm.entity.AlternativePair;
import lpsolve.LpSolveException;

import java.util.List;

public interface CompareAlternativesCalculator {
    List<AlternativePair> getResultVector(
        DataContext dataContext, int k, List<Double> v, List<Double> delta) throws LpSolveException;
}
