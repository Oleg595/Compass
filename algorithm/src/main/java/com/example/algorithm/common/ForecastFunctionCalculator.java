package com.example.algorithm.common;

import com.example.algorithm.function.ForecastFunction;
import lpsolve.LpSolveException;
import org.example.DataContext;

public interface ForecastFunctionCalculator {
    ForecastFunction calculateForecastFunction(DataContext dataContext) throws LpSolveException;
}
