package com.example.compas.algorithm;

import com.example.compas.context.DataContext;
import com.example.compas.entity.ForecastFunctionEntity;
import lombok.RequiredArgsConstructor;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import org.ejml.dense.row.CommonOps_DDRM;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
class ForecastFunctionCalculator {
    private final DataContext dataContext;

    // Ищем оптимальное решение задачи p -> min
    private class CalculateOptimalP {
        // Задаём целевую функцию p -> min
        private void addGoalFunctionForOptP(LpSolve solver) throws LpSolveException {
            var dim = 1 + dataContext.getM() + dataContext.getP().size();
            var goalFunction = new double[dim];
            goalFunction[0] = 1.0;
            solver.setObjFn(goalFunction);
        }

        // Добавляем ограничения D^T * v + \sigma >= l (единичный вектор)
        private void addDConstraints(LpSolve solver) throws LpSolveException {
            var transposeD = dataContext.calculateDMatrix();
            var dim = 2 + dataContext.getM() + dataContext.getP().size();
            CommonOps_DDRM.transpose(transposeD);
            for (var row = 0; row < transposeD.numRows; ++row) {
                var constraint = new double[dim];
                for (var col = 0; col < transposeD.numCols; ++col) {
                    constraint[2 + col] = transposeD.get(row, col);
                }
                constraint[2 + dataContext.getM() + row] = 1.0;
                solver.addConstraint(constraint, LpSolve.GE, 1.0);
            }
        }

        // Добавляем ограничения E^T * v = 0
        private void addEConstraints(LpSolve solver) throws LpSolveException {
            var transposeE = dataContext.calculateEMatrix();
            var dim = 2 + dataContext.getM() + dataContext.getP().size();
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
            var dim = 2 + dataContext.getM() + dataContext.getP().size();
            for (var index = 0; index < dataContext.getP().size(); ++index) {
                var constraint = new double[dim];
                constraint[1] = -1.0;
                constraint[2 + dataContext.getM() + index] = 1.0;
                solver.addConstraint(constraint, LpSolve.LE, 0.0);
            }
            for (var index = 0; index < dataContext.getP().size(); ++index) {
                var constraint = new double[dim];
                constraint[1 + dataContext.getM() + index] = 1.0;
                solver.addConstraint(constraint, LpSolve.GE, 0);
            }
        }

        // Создаём задачу
        private LpSolve createSolver() throws LpSolveException {
            var dim = 1 + dataContext.getM() + dataContext.getP().size();
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
            var dim = dataContext.getM() + dataContext.getP().size();
            var goalFunction = new double[dim];
            for (var index = 0; index < dataContext.getM(); ++index) {
                goalFunction[index] = 1.0;
            }
            solver.setObjFn(goalFunction);
        }

        // Добавляем ограничения D^T * v + \sigma >= l (единичный вектор)
        private void addDConstraints(LpSolve solver) throws LpSolveException {
            var transposeD = dataContext.calculateDMatrix();
            var dim = 1 + dataContext.getM() + dataContext.getP().size();
            CommonOps_DDRM.transpose(transposeD);
            for (var row = 0; row < transposeD.numRows; ++row) {
                var constraint = new double[dim];
                for (var col = 0; col < transposeD.numCols; ++col) {
                    constraint[1 + col] = transposeD.get(row, col);
                }
                constraint[1 + dataContext.getM() + row] = 1.0;
                solver.addConstraint(constraint, LpSolve.GE, 1.0);
            }
        }

        // Добавляем ограничения E^T * v = 0
        private void addEConstraints(LpSolve solver) throws LpSolveException {
            var transposeE = dataContext.calculateEMatrix();
            var dim = 1 + dataContext.getM() + dataContext.getP().size();
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
            var dim = 1 + dataContext.getM() + dataContext.getP().size();
            for (var index = 0; index < dataContext.getP().size(); ++index) {
                var constraint = new double[dim];
                constraint[1 + dataContext.getM() + index] = 1.0;
                solver.addConstraint(constraint, LpSolve.LE, optP);
                solver.addConstraint(constraint, LpSolve.GE, 0);
            }
        }

        // Создаём задачу
        private LpSolve createSolver(double optP) throws LpSolveException {
            var dim = dataContext.getM() + dataContext.getP().size();
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
            while (true) {
                var solver = createSolver(optP);
                solver.solve();
                if (solver.getStatus() == 0) {
                    var result = new ArrayList<Double>();
                    for (var index = 0; index < dataContext.getM(); ++index) {
                        result.add(solver.getPtrVariables()[index]);
                    }
                    solver.deleteLp();
                    return result;
                }
                solver.deleteLp();
            }
        }
    }

    ForecastFunctionEntity calculateForecastFunction() throws LpSolveException {
        var pCalc = new CalculateOptimalP();
        var optP = pCalc.getOptP();
        var vCalc = new CalculateOptimalV();
        return new ForecastFunctionEntity(vCalc.getOptV(optP));
    }
}
