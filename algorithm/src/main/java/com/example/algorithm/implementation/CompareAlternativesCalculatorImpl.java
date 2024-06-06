package com.example.algorithm.implementation;

import com.example.algorithm.common.CompareAlternativesCalculator;
import com.example.algorithm.implementation.rule.RuleService;
import com.example.algorithm.utils.AlternativeUtils;
import com.example.algorithm.utils.ValueCalculatorUtils;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import org.example.AlternativeEntity;
import org.example.AlternativePair;
import org.example.DataContext;
import org.example.RuleEntity;
import org.example.RuleSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class CompareAlternativesCalculatorImpl implements CompareAlternativesCalculator {
    private DataContext dataContext;
    private int k;
    private List<Double> v;
    private List<Double> delta;
    @Autowired
    private RuleService ruleService;

    private int getT() {
        return dataContext.getCriterias().size() - k + 1;
    }

    private int getDim() {
        var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
        return 2 * getT() * M + dataContext.getP().size() + dataContext.getI().size() + getT();
    }

    private double calcMv() {
        return v.stream().mapToDouble(it -> it).sum() + 1.;
    }

    // Целевая функция представлена в виде r^+_1 - r^-_1 + r^+_2 - r^-_2 + ...
    private void addGoalFunction(LpSolve solver) throws LpSolveException {
        var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
        var goalFunction = new double[getDim()];
        for (var index = 0; index < M; ++index) {
            goalFunction[1 + 2 * index] = v.get(index);
            goalFunction[1 + 2 * index + 1] = -v.get(index);
        }
        for (var index = 0; index < getT() * M; ++index) {
            solver.setBinary(2 * index + 1, true);
            solver.setBinary(2 * index + 2, true);
        }
        for (var index = 0; index < getT(); ++index) {
            solver.setBinary(goalFunction.length - index, true);
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
            for (var col = 0; col < M; ++col) {
                constraint[1 + 2 * (index * M + col)] = -v.get(col);
                constraint[1 + 2 * (index * M + col) + 1] = v.get(col);
            }
            constraint[1 + getDim() - getT() + index] = Mv;
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
        for (var index = 0; index < Integer.min(getT(), 2); ++index) {
            var count = 0;
            for (var criteria : dataContext.getCriterias()) {
                var constraint1 = new double[1 + getDim()];
                var constraint2 = new double[1 + getDim()];
                for (var col = 0; col < criteria.getValues().size(); ++col) {
                    constraint1[1 + 2 * col + count + 2 * index * M] = 1.0;
                    constraint1[1 + 2 * col + 1 + count + 2 * index * M] = -1.0;
                    constraint2[1 + 2 * col + count + 2 * index * M] = 1.0;
                }
                count += 2 * criteria.getValues().size();
                solver.addConstraint(constraint1, LpSolve.EQ, 0.0);
                solver.addConstraint(constraint2, LpSolve.LE, 1.0);
            }
        }
    }

    private void addConstraints9(LpSolve solver) throws LpSolveException {
        var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
        for (var index = 0; index < Integer.min(getT(), 2); ++index) {
            var constraint = new double[1 + getDim()];
            for (var col = 0; col < M; ++col) {
                constraint[1 + 2 * col + 2 * index * M] = 1.0;
            }
            solver.addConstraint(constraint, LpSolve.LE, k);
        }
    }

    private void addConstraint10(LpSolve solver) throws LpSolveException {
        var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
        for (var col = 0; col < dataContext.getP().size(); ++col) {
            var constraint = new double[1 + getDim()];
            constraint[1 + 2 * M * getT() + col] = 1.0;
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
//        solver.setOutputfile("./result.txt");
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

    private boolean comparableAlternatives(AlternativeEntity alt1, AlternativeEntity alt2) {
        var rule1 = new RuleEntity(new AlternativePair(alt1, alt2), RuleSet.PREPARE);
        var rule2 = new RuleEntity(new AlternativePair(alt2, alt1), RuleSet.PREPARE);
        var rule3 = new RuleEntity(new AlternativePair(alt1, alt2), RuleSet.EQUAL);
        return ruleService.checkRule(rule1, dataContext)
            || ruleService.checkRule(rule2, dataContext)
            || ruleService.checkRule(rule3, dataContext);
    }

    private List<AlternativePair> extractAlternatives(double[] data) {
        var M = ValueCalculatorUtils.calculateM(dataContext.getCriterias());
        var result = new ArrayList<AlternativePair>();
        for (var index = 0; index < Integer.min(getT(), 2); ++index) {
            var delta1 = new ArrayList<Double>();
            var delta2 = new ArrayList<Double>();
            for (var col = 0; col < M; ++col) {
                var value1 = data[2 * col + 2 * M * index];
                var value2 = data[2 * col + 1 + 2 * M * index];
                if (value1 == 0 || value1 != value2) {
                    delta1.add(data[2 * col + 2 * M * index]);
                    delta2.add(data[2 * col + 1 + 2 * M * index]);
                } else {
                    delta1.add(.0);
                    delta2.add(.0);
                }
            }
            if (delta1.stream().anyMatch(it -> it != 0.0) && delta2.stream().anyMatch(it -> it != 0.0)) {
                var alt1 = AlternativeUtils.createByDelta(delta1, dataContext.getCriterias());
                var alt2 = AlternativeUtils.createByDelta(delta2, dataContext.getCriterias());
                var pair = new AlternativePair(alt1, alt2);
                var inP = dataContext.getP().stream().anyMatch(it -> it.getPair().isEqual(pair));
                var inI = dataContext.getI().stream().anyMatch(it -> it.getPair().isEqual(pair));
                if (!inP && !inI && alt1.comparable(alt2)
                    && !comparableAlternatives(alt1, alt2)
                    && alt1.getCriteriaToValue().values().stream()
                    .filter(Objects::nonNull).count() <= k) {
                    result.add(pair);
                }
            }
        }
        return result;
    }

    @Override
    public List<AlternativePair> getResultVector(
        int k, List<Double> v, AlternativePair pair, DataContext dataContext)
        throws LpSolveException {
        this.dataContext = dataContext;
        this.k = k;
        this.v = v;
        this.delta = AlternativeUtils.calculateDelta(pair, dataContext.getCriterias());

        var solver = createSolver();
        solver.setEpsel(.005);
        solver.setTimeout(35L);
        solver.solve();
        return extractAlternatives(solver.getPtrVariables());
    }
}
