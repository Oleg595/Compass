package com.example.algorithm.implementation;

import com.example.algorithm.common.CompareAlternativesCalculator;
import com.example.algorithm.context.DataContext;
import com.example.algorithm.entity.AlternativePair;
import com.example.algorithm.utils.AlternativeUtils;
import com.example.algorithm.utils.ValueCalculatorUtils;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// Перепроверить ограничения
@Component
class CompareAlternativesCalculatorImpl implements CompareAlternativesCalculator {
    private DataContext dataContext;
    private int k;
    private List<Double> v;
    private List<Double> delta;

    private int getT() {
        return dataContext.getCriterias().size() - k + 1;
    }

    private int getDim() {
        var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
        return 2 * getT() * M + dataContext.getP().size() + dataContext.getI().size() + getT() - 1;
    }

    private double calcMv() {
        return v.stream().mapToDouble(it -> it).sum() + 1.;
    }

    // Целевая функция представлена в виде r^+_1 - r^-_1 + r^+_2 - r^-_2 + ...
    private void addGoalFunction(LpSolve solver) throws LpSolveException {
        var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
        var goalFunction = new double[getDim()];
        for (var index = 0; index < M; ++index) {
            goalFunction[2 * index] = v.get(index);
            goalFunction[2 * index + 1] = -v.get(index);
        }
        for (var index = 0; index < getT() * M; ++index) {
            solver.setBinary(2 * index + 1, true);
            solver.setBinary(2 * index + 2, true);
        }
        solver.setObjFn(goalFunction);
    }

    private void addConstraints4(LpSolve solver) throws LpSolveException {
        var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
        var D = ValueCalculatorUtils.calculateDMatrix(dataContext);
        var E = ValueCalculatorUtils.calculateEMatrix(dataContext);
        for (var row = 0; row < M; ++row) {
            var constraint = new double[1 + getDim()];
            for (var col = 0; col < getT(); ++col) {
                constraint[1 + 2 * (col * M + row)] = 1.0;
                constraint[1 + 2 * (col * M + row) + 1] = -1.0;
            }
            for (var dCol = 0; dCol < dataContext.getP().size(); ++dCol) {
                constraint[1 + dCol + 2 * getT() * M] = D.get(row, dCol);
            }
            for (var eCol = 0; eCol < dataContext.getI().size(); ++eCol) {
                constraint[1 + D.numCols + 2 * getT() * M + eCol] = E.get(row, eCol);
            }
            solver.addConstraint(constraint, LpSolve.EQ, delta.get(row));
        }
    }

    private void addConstraints5(LpSolve solver) throws LpSolveException {
        var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
        var Mv = calcMv();
        for (var index = 1; index < getT(); ++index) {
            var constraint = new double[1 + getDim()];
            for (var col = 0; col < M; ++col) {
                constraint[1 + 2 * col] = v.get(col);
                constraint[1 + 2 * col + 1] = -v.get(col);
            }
            for (var col = index * M; col < M; ++col) {
                constraint[1 + 2 * (index * M + col)] = -v.get(col);
                constraint[1 + 2 * (index * M + col) + 1] = v.get(col);
            }
            constraint[getDim() - getT() + index] = Mv;
            solver.addConstraint(constraint, LpSolve.LE, Mv);
        }
    }

    private void addConstraints6(LpSolve solver) throws LpSolveException {
        var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
        for (var index = 1; index < getT(); ++index) {
            var count = 0;
            for (var criteria : dataContext.getCriterias()) {
                var constraint = new double[1 + getDim()];
                for (var col = 0; col < criteria.getValues().size(); ++col) {
                    constraint[1 + 2 * (index * M + col) + count] = 1.0;
                }
                count += 2 * criteria.getValues().size();
                constraint[1 + getDim() - getT() + index] = -1.0;
                solver.addConstraint(constraint, LpSolve.LE, 0.0);
            }
        }
    }

    private void addConstraints7(LpSolve solver) throws LpSolveException {
        var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
        var constraint = new double[1 + getDim()];
        for (var col = 0; col < M; ++col) {
            constraint[1 + 2 * col] = 1.0;
        }
        solver.addConstraint(constraint, LpSolve.GE, 1.0);
    }

    private void addConstraints8(LpSolve solver) throws LpSolveException {
        var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
        for (var rNum = 0; rNum < getT(); ++rNum) {
            var count = 0;
            for (var criteria : dataContext.getCriterias()) {
                var constraint1 = new double[1 + getDim()];
                var constraint2 = new double[1 + getDim()];
                for (var col = 0; col < criteria.getValues().size(); ++col) {
                    constraint1[1 + 2 * (rNum * M + col) + count] = 1.0;
                    constraint1[1 + 2 * (rNum * M + col) + count + 1] = -1.0;
                    constraint2[1 + 2 * (rNum * M + col) + count] = 1.0;
                }
                count += 2 * criteria.getValues().size();
                solver.addConstraint(constraint1, LpSolve.EQ, 0.0);
                solver.addConstraint(constraint2, LpSolve.LE, 1.0);
            }
        }
    }

    private void addConstraints9(LpSolve solver) throws LpSolveException {
        var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
        for (var rNum = 0; rNum < getT(); ++rNum) {
            var constraint = new double[1 + getDim()];
            for (var col = rNum * M; col < (rNum + 1) * M; ++col) {
                constraint[1 + 2 * col] = 1.0;
            }
            solver.addConstraint(constraint, LpSolve.LE, k);
        }
    }

    private void addConstraint10(LpSolve solver) throws LpSolveException {
        var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
        for (var col = 0; col < dataContext.getP().size(); ++col) {
            var constraint = new double[1 + getDim()];
            constraint[1 + 2 * M + col] = 1.0;
            solver.addConstraint(constraint, LpSolve.GE, 0.0);
        }
        for (var col = 0; col < dataContext.getI().size(); ++col) {
            var constraint = new double[1 + getDim()];
            constraint[1 + 2 * M + dataContext.getP().size() + col] = 1.0;
            solver.addConstraint(constraint, LpSolve.GE, 0.0);
        }
    }

    private LpSolve createSolver() throws LpSolveException {
        var solver = LpSolve.makeLp(0, getDim());
        solver.setOutputfile("./result.txt");
        solver.setMaxim();
        addGoalFunction(solver);
        addConstraints4(solver);
        addConstraints5(solver);
        addConstraints6(solver);
        addConstraints7(solver);
        addConstraints8(solver);
        addConstraints9(solver);
        addConstraint10(solver);
        return solver;
    }

    private List<AlternativePair> extractAlternatives(double[] data) {
        var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
        var result = new ArrayList<AlternativePair>();
        for (var index = 0; index < getT(); ++index) {
            var delta1 = new ArrayList<Double>();
            var delta2 = new ArrayList<Double>();
            for (var col = index * M; col < (index + 1) * M; ++col) {
                delta1.add(data[2 * col]);
                delta2.add(data[2 * col + 1]);
            }
            if (delta1.stream().anyMatch(it -> it != 0.0) && delta2.stream().anyMatch(it -> it != 0.0)) {
                var alt1 = AlternativeUtils.createByDelta(delta1, dataContext.getCriterias());
                var alt2 = AlternativeUtils.createByDelta(delta2, dataContext.getCriterias());
                result.add(new AlternativePair(alt1, alt2));
            }
        }
        return result;
    }

    public List<AlternativePair> getResultVector(
        DataContext dataContext, int k, List<Double> v, List<Double> delta)
        throws LpSolveException {
        this.dataContext = dataContext;
        this.k = k;
        this.v = v;
        this.delta = delta;

        var solver = createSolver();
        solver.solve();
        return extractAlternatives(solver.getPtrVariables());
    }
}
