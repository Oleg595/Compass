package com.example.compas.algorithm;

import com.example.compas.context.DataContext;
import com.example.compas.entity.AlternativePair;
import com.example.compas.service.AlternativeService;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
class CompareAlternativesCalculator {
    private final DataContext dataContext;
    private final AlternativeService alternativeService;
    private int k;
    private List<Double> v;
    private List<Double> delta;

    CompareAlternativesCalculator(
        DataContext dataContext, AlternativeService alternativeService) {
        this.dataContext = dataContext;
        this.alternativeService = alternativeService;
    }

    private int getT() {
        return dataContext.getCriterias().size() - k + 1;
    }

    private int getDim() {
        return 2 * getT() * dataContext.getM() + dataContext.getP().size() + dataContext.getI().size() + getT() - 1;
    }

    private double calcMv() {
        return v.stream().mapToDouble(it -> it).sum() + 1.;
    }

    // Целевая функция представлена в виде r^+_1 - r^-_1 + r^+_2 - r^-_2 + ...
    private void addGoalFunction(LpSolve solver) throws LpSolveException {
        var goalFunction = new double[getDim()];
        for (var index = 0; index < dataContext.getM(); ++index) {
            goalFunction[2 * index] = v.get(index);
            goalFunction[2 * index + 1] = -v.get(index);
        }
        for (var index = 0; index < getT() * dataContext.getM(); ++index) {
            solver.setBinary(2 * index + 1, true);
            solver.setBinary(2 * index + 2, true);
        }
        solver.setObjFn(goalFunction);
    }

    private void addConstraints4(LpSolve solver) throws LpSolveException {
        var D = dataContext.calculateDMatrix();
        var E = dataContext.calculateEMatrix();
        for (var row = 0; row < dataContext.getM(); ++row) {
            var constraint = new double[1 + getDim()];
            for (var col = 0; col < getT(); ++col) {
                constraint[1 + 2 * (col * dataContext.getM() + row)] = 1.0;
                constraint[1 + 2 * (col * dataContext.getM() + row) + 1] = -1.0;
            }
            for (var dCol = 0; dCol < dataContext.getP().size(); ++dCol) {
                constraint[1 + dCol + 2 * getT() * dataContext.getM()] = D.get(row, dCol);
            }
            for (var eCol = 0; eCol < dataContext.getI().size(); ++eCol) {
                constraint[1 + D.numCols + 2 * getT() * dataContext.getM() + eCol] = E.get(row, eCol);
            }
            solver.addConstraint(constraint, LpSolve.EQ, delta.get(row));
        }
    }

    private void addConstraints5(LpSolve solver) throws LpSolveException {
        var Mv = calcMv();
        for (var index = 1; index < getT(); ++index) {
            var constraint = new double[1 + getDim()];
            for (var col = 0; col < dataContext.getM(); ++col) {
                constraint[1 + 2 * col] = v.get(col);
                constraint[1 + 2 * col + 1] = -v.get(col);
            }
            for (var col = index * dataContext.getM(); col < dataContext.getM(); ++col) {
                constraint[1 + 2 * (index * dataContext.getM() + col)] = -v.get(col);
                constraint[1 + 2 * (index * dataContext.getM() + col) + 1] = v.get(col);
            }
            constraint[getDim() - getT() + index] = Mv;
            solver.addConstraint(constraint, LpSolve.LE, Mv);
        }
    }

    private void addConstraints6(LpSolve solver) throws LpSolveException {
        for (var index = 1; index < getT(); ++index) {
            var count = 0;
            for (var criteria : dataContext.getCriterias()) {
                var constraint = new double[1 + getDim()];
                for (var col = 0; col < criteria.getValues().size(); ++col) {
                    constraint[1 + 2 * (index * dataContext.getM() + col) + count] = 1.0;
                }
                count += 2 * criteria.getValues().size();
                constraint[1 + getDim() - getT() + index] = -1.0;
                solver.addConstraint(constraint, LpSolve.LE, 0.0);
            }
        }
    }

    private void addConstraints7(LpSolve solver) throws LpSolveException {
        var constraint = new double[1 + getDim()];
        for (var col = 0; col < dataContext.getM(); ++col) {
            constraint[1 + 2 * col] = 1.0;
        }
        solver.addConstraint(constraint, LpSolve.GE, 1.0);
    }

    private void addConstraints8(LpSolve solver) throws LpSolveException {
        for (var rNum = 0; rNum < getT(); ++rNum) {
            var count = 0;
            for (var criteria: dataContext.getCriterias()) {
                var constraint1 = new double[1 + getDim()];
                var constraint2 = new double[1 + getDim()];
                for (var col = 0; col < criteria.getValues().size(); ++col) {
                    constraint1[1 + 2 * (rNum * dataContext.getM() + col) + count] = 1.0;
                    constraint1[1 + 2 * (rNum * dataContext.getM() + col) + count + 1] = -1.0;
                    constraint2[1 + 2 * (rNum * dataContext.getM() + col) + count ] = 1.0;
                }
                count += 2 * criteria.getValues().size();
                solver.addConstraint(constraint1, LpSolve.EQ, 0.0);
                solver.addConstraint(constraint2, LpSolve.LE, 1.0);
            }
        }
    }

    private void addConstraints9(LpSolve solver) throws LpSolveException {
        for (var rNum = 0; rNum < getT(); ++rNum) {
            var constraint = new double[1 + getDim()];
            for (var col = rNum * dataContext.getM(); col < (rNum + 1) * dataContext.getM(); ++col) {
                constraint[1 + 2 * col] = 1.0;
            }
            solver.addConstraint(constraint, LpSolve.LE, k);
        }
    }

    private void addConstraint10(LpSolve solver) throws LpSolveException {
        for (var col = 0; col < dataContext.getP().size(); ++col) {
            var constraint = new double[1 + getDim()];
            constraint[1 + 2 * dataContext.getM() + col] = 1.0;
            solver.addConstraint(constraint, LpSolve.GE, 0.0);
        }
        for (var col = 0; col < dataContext.getI().size(); ++col) {
            var constraint = new double[1 + getDim()];
            constraint[1 + 2 * dataContext.getM() + dataContext.getP().size() + col] = 1.0;
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
        var result = new ArrayList<AlternativePair>();
        for (var index = 0; index < getT(); ++index) {
            var delta1 = new ArrayList<Double>();
            var delta2 = new ArrayList<Double>();
            for (var col = index * dataContext.getM(); col < (index + 1) * dataContext.getM(); ++col) {
                delta1.add(data[2 * col]);
                delta2.add(data[2 * col + 1]);
            }
            if (delta1.stream().anyMatch(it -> it != 0.0) && delta2.stream().anyMatch(it -> it != 0.0)) {
                var alt1 = alternativeService.createByDelta(delta1);
                var alt2 = alternativeService.createByDelta(delta2);
                result.add(new AlternativePair(alt1, alt2));
            }
        }
        return result;
    }

    List<AlternativePair> getResultVector(
        int k, List<Double> v, List<Double> delta) throws LpSolveException {
        this.k = k;
        this.v = v;
        this.delta = delta;

        var solver = createSolver();
        solver.solve();
        return extractAlternatives(solver.getPtrVariables());
    }
}
