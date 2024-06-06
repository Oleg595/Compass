package com.example.algorithm.implementation.rule;

import com.example.algorithm.utils.AlternativeUtils;
import com.example.algorithm.utils.ValueCalculatorUtils;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import org.example.AlternativePair;
import org.example.DataContext;
import org.example.RuleEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SolveSLAUV2 {
    private int prevMin = 1;
    private int counter = 0;
    private List<List<Integer>> prevAnswers = new ArrayList<>();
    private DataContext dataContext;
    private List<Double> delta;

    private SolveSLAUV2() {}

    public SolveSLAUV2(AlternativePair pair, DataContext dataContext) {
        this.dataContext = dataContext;
        this.delta = AlternativeUtils.calculateDelta(pair, dataContext.getCriterias());
    }

    private int getDim() {
        return dataContext.getP().size() + 2 * dataContext.getI().size();
    }

    private void addGoalFunction(LpSolve solver) throws LpSolveException {
        var goalFunction = new double[1 + getDim()];
        var P = dataContext.getP();
        var I = dataContext.getI();
        for (var index = 0; index < P.size(); ++index) {
            solver.setInt(1 + index, true);
            goalFunction[1 + index] = 1.;
        }
        for (var index = 0; index < I.size(); ++index) {
            solver.setInt(1 + P.size() + index, true);
            goalFunction[1 + P.size() + 2 * index] = 1.;
            goalFunction[1 + P.size() + 2 * index + 1] = 1.;
        }
        solver.setObjFn(goalFunction);
    }

    private void addVarsConstraints(LpSolve solver) throws LpSolveException {
        for (var index = 0; index < getDim(); ++index) {
            var constraint = new double[1 + getDim()];
            constraint[1 + index] = 1.;
            solver.addConstraint(constraint, LpSolve.GE, .0);
        }
    }

    private void addIPConstraints(LpSolve solver) throws LpSolveException {
        var D = ValueCalculatorUtils.calculateDMatrix(dataContext);
        var E = ValueCalculatorUtils.calculateEMatrix(dataContext);
        for (var row = 0; row < D.numRows; ++row) {
            var constraint = new double[1 + getDim()];
            for (var col = 0; col < D.numCols; ++col) {
                constraint[1 + col] = D.get(row, col);
            }
            for (var col = 0; col < E.numCols; ++col) {
                constraint[1 + D.numCols + 2 * col] = E.get(row, col);
                constraint[1 + D.numCols + 2 * col + 1] = -E.get(row, col);
            }
            solver.addConstraint(constraint, LpSolve.EQ, delta.get(row));
        }
    }

    private void addAnswerConstraints(LpSolve solver) throws LpSolveException {
        for (var answer : prevAnswers) {
            var constraint = new double[1 + getDim()];
            for (var index = 0; index < getDim(); ++index) {
                constraint[1 + index] = answer.get(index);
            }
            solver.addConstraint(constraint, LpSolve.LE, prevMin - 1);
        }
        var constraint = new double[1 + getDim()];
        for (var index = 0; index < getDim(); ++index) {
            constraint[1 + index] = 1.;
        }
        solver.addConstraint(constraint, LpSolve.GE, prevMin);
    }

    private LpSolve createSolver() throws LpSolveException {
        var solver = LpSolve.makeLp(0, getDim());
        solver.setOutputfile("./result.txt");
        solver.setMinim();
        addGoalFunction(solver);
        addVarsConstraints(solver);
        addIPConstraints(solver);
        addAnswerConstraints(solver);
        return solver;
    }

    private List<RuleEntity> processAnswer(LpSolve solver) throws LpSolveException {
        var answer = solver.getPtrVariables();
        var min = (int)Arrays.stream(answer).sum();
        var castAnswer = new ArrayList<Integer>();
        for (var index = 0; index < answer.length; ++index) {
            castAnswer.add(index, (int)answer[index]);
        }
        if (min == prevMin) {
            prevAnswers.add(castAnswer);
        } else {
            prevMin = min;
            prevAnswers.clear();
            prevAnswers.add(castAnswer);
        }
        var result = new ArrayList<RuleEntity>();
        var index = 0;
        for (var pIndex = 0; pIndex < dataContext.getP().size(); ++pIndex) {
            for (var i = 0; i < answer[pIndex]; ++i) {
                var rule = dataContext.getP().get(pIndex).copy();
                result.add(rule);
            }
        }
        index += dataContext.getP().size();
        for (var iIndex = 0; iIndex < dataContext.getI().size(); ++iIndex) {
            for (var i = 0; i < answer[iIndex + index]; ++i) {
                var rule = dataContext.getI().get(iIndex).copy();
                result.add(rule);
            }
        }
        index += dataContext.getI().size();
        for (var iIndex = 0; iIndex < dataContext.getI().size(); ++iIndex) {
            for (var i = 0; i < answer[iIndex + index]; ++i) {
                var rule = dataContext.getI().get(iIndex).copy();
                var pair = new AlternativePair(rule.getPair().getSecond(), rule.getPair().getFirst());
                rule.setPair(pair);
                result.add(rule);
            }
        }
        return result;
    }

    public List<RuleEntity> getNextOrEmpty() {
        try {
            if (counter == 10) return new ArrayList<>();
            counter++;
            var solver = createSolver();
            solver.solve();
            if (solver.getStatus() != 0) {
                solver = createSolver();
                solver.solve();
            }
            if (solver.getStatus() != 0) return new ArrayList<>();
            return processAnswer(solver);
        } catch (LpSolveException e) {
            return new ArrayList<>();
        }
    }
}
