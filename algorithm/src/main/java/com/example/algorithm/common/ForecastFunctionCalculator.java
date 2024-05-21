package com.example.algorithm.common;

import com.example.algorithm.entity.ForecastFunctionEntity;
import lpsolve.LpSolveException;
import org.example.DataContext;

public interface ForecastFunctionCalculator {
    ForecastFunctionEntity calculateForecastFunction(DataContext dataContext) throws LpSolveException;
}
