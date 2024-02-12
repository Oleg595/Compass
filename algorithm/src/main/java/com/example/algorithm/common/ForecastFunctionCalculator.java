package com.example.algorithm.common;

import com.example.algorithm.context.DataContext;
import com.example.algorithm.entity.ForecastFunctionEntity;
import lpsolve.LpSolveException;

public interface ForecastFunctionCalculator {
    ForecastFunctionEntity calculateForecastFunction(DataContext dataContext) throws LpSolveException;
}
