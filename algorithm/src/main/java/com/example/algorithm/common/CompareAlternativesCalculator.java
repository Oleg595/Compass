package com.example.algorithm.common;

import lpsolve.LpSolveException;
import org.example.AlternativePair;
import org.example.DataContext;

import java.util.List;

public interface CompareAlternativesCalculator {
    List<AlternativePair> getResultVector(
        int k, List<Double> v, AlternativePair pair, DataContext dataContext)
        throws LpSolveException;
}
