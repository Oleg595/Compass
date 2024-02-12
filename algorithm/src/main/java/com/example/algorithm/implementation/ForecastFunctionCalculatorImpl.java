package com.example.algorithm.implementation;

import com.example.algorithm.common.ForecastFunctionCalculator;
import com.example.algorithm.context.DataContext;
import com.example.algorithm.entity.ForecastFunctionEntity;
import com.example.algorithm.utils.ValueCalculatorUtils;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import org.ejml.dense.row.CommonOps_DDRM;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
class ForecastFunctionCalculatorImpl implements ForecastFunctionCalculator {

    private DataContext dataContext;

    // Ищем оптимальное решение задачи p -> min
    private class CalculateOptimalP {
        // Задаём целевую функцию p -> min
        private void addGoalFunctionForOptP(LpSolve solver) throws LpSolveException {
            var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
            var dim = 1 + M + dataContext.getP().size();
            var goalFunction = new double[dim];
            goalFunction[0] = 1.0;
            solver.setObjFn(goalFunction);
        }

        // Добавляем ограничения D^T * v + \sigma >= l (единичный вектор)
        private void addDConstraints(LpSolve solver) throws LpSolveException {
            var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
            var transposeD = ValueCalculatorUtils.calculateDMatrix(dataContext);
            var dim = 2 + M + dataContext.getP().size();
            CommonOps_DDRM.transpose(transposeD);
            for (var row = 0; row < transposeD.numRows; ++row) {
                var constraint = new double[dim];
                for (var col = 0; col < transposeD.numCols; ++col) {
                    constraint[2 + col] = transposeD.get(row, col);
                }
                constraint[2 + M + row] = 1.0;
                solver.addConstraint(constraint, LpSolve.GE, 1.0);
            }
        }

        // Добавляем ограничения E^T * v = 0
        private void addEConstraints(LpSolve solver) throws LpSolveException {
            var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
            var transposeE = ValueCalculatorUtils.calculateEMatrix(dataContext);
            var dim = 2 + M + dataContext.getP().size();
            CommonOps_DDRM.transpose(transposeE);
            for (var row = 0; row < transposeE.numRows; ++row) {
                var constraint = new double[dim];
                for (var col = 0; col < transposeE.numCols; ++col) {
                    constraint[2 + col] = transposeE.get(row, col);
                }
                solver.addConstraint(constraint, LpSolve.EQ, 0.0);
            }
        }

        // Добавляем ограничения на переменные
        private void addVarsConstraints(LpSolve solver) throws LpSolveException {
            var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
            var dim = 2 + M + dataContext.getP().size();
            for (var index = 0; index < dataContext.getP().size(); ++index) {
                var constraint = new double[dim];
                constraint[1] = -1.0;
                constraint[2 + M + index] = 1.0;
                solver.addConstraint(constraint, LpSolve.LE, 0.0);
            }
            for (var index = 0; index < dataContext.getP().size(); ++index) {
                var constraint = new double[dim];
                constraint[1 + M + index] = 1.0;
                solver.addConstraint(constraint, LpSolve.GE, 0);
            }
        }

        // Создаём задачу
        private LpSolve createSolver() throws LpSolveException {
            var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
            var dim = 1 + M + dataContext.getP().size();
            var result = LpSolve.makeLp(0, dim);
            result.setOutputfile("./result.txt");
            result.setMinim();
            addGoalFunctionForOptP(result);
            addDConstraints(result);
            addEConstraints(result);
            addVarsConstraints(result);
            return result;
        }

        public double getOptP() throws LpSolveException {
            while (true) {
                var solver = createSolver();
                solver.solve();
                if (solver.getStatus() == 0) {
                    var result = solver.getPtrVariables()[0];
                    solver.deleteLp();
                    return result;
                }
                solver.deleteLp();
            }
        }
    }

    // Ищем оптимальное значение v^*
    private class CalculateOptimalV {
        // Задаём целевую функцию (l, v^*) -> min
        private void addGoalFunction(LpSolve solver) throws LpSolveException {
            var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
            var dim = M + dataContext.getP().size();
            var goalFunction = new double[dim];
            for (var index = 0; index < M; ++index) {
                goalFunction[index] = 1.0;
            }
            solver.setObjFn(goalFunction);
        }

        // Добавляем ограничения D^T * v + \sigma >= l (единичный вектор)
        private void addDConstraints(LpSolve solver) throws LpSolveException {
            var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
            var transposeD = ValueCalculatorUtils.calculateDMatrix(dataContext);
            var dim = 1 + M + dataContext.getP().size();
            CommonOps_DDRM.transpose(transposeD);
            for (var row = 0; row < transposeD.numRows; ++row) {
                var constraint = new double[dim];
                for (var col = 0; col < transposeD.numCols; ++col) {
                    constraint[1 + col] = transposeD.get(row, col);
                }
                constraint[1 + M + row] = 1.0;
                solver.addConstraint(constraint, LpSolve.GE, 1.0);
            }
        }

        // Добавляем ограничения E^T * v = 0
        private void addEConstraints(LpSolve solver) throws LpSolveException {
            var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
            var transposeE = ValueCalculatorUtils.calculateEMatrix(dataContext);
            var dim = 1 + M + dataContext.getP().size();
            CommonOps_DDRM.transpose(transposeE);
            for (var row = 0; row < transposeE.numRows; ++row) {
                var constraint = new double[dim];
                for (var col = 0; col < transposeE.numCols; ++col) {
                    constraint[1 + col] = transposeE.get(row, col);
                }
                solver.addConstraint(constraint, LpSolve.EQ, 0.0);
            }
        }

        // Добавляем ограничения на переменные
        private void addVarsConstraints(LpSolve solver, double optP) throws LpSolveException {
            var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
            var dim = 1 + M + dataContext.getP().size();
            for (var index = 0; index < dataContext.getP().size(); ++index) {
                var constraint = new double[dim];
                constraint[1 + M + index] = 1.0;
                solver.addConstraint(constraint, LpSolve.LE, optP);
                solver.addConstraint(constraint, LpSolve.GE, 0);
            }
        }

        // Создаём задачу
        private LpSolve createSolver(double optP) throws LpSolveException {
            var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
            var dim = M + dataContext.getP().size();
            var result = LpSolve.makeLp(0, dim);
            result.setOutputfile("./result.txt");
            result.setMinim();
            addGoalFunction(result);
            addDConstraints(result);
            addEConstraints(result);
            addVarsConstraints(result, optP);
            return result;
        }

        public List<Double> getOptV(double optP) throws LpSolveException {
            var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
            while (true) {
                var solver = createSolver(optP);
                solver.solve();
                if (solver.getStatus() == 0) {
                    var result = new ArrayList<Double>();
                    for (var index = 0; index < M; ++index) {
                        result.add(solver.getPtrVariables()[index]);
                    }
                    solver.deleteLp();
                    return result;
                }
                solver.deleteLp();
            }
        }
    }

    public ForecastFunctionEntity calculateForecastFunction(DataContext dataContext) throws LpSolveException {
        this.dataContext = dataContext;
        var pCalc = new CalculateOptimalP();
        var optP = pCalc.getOptP();
        var vCalc = new CalculateOptimalV();
        return new ForecastFunctionEntity(vCalc.getOptV(optP));
    }
}
