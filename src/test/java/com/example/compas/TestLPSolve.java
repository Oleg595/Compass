package com.example.compas;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

// Пример использования библиотеки LPSolve
public class TestLPSolve {
    private class Solver {
        private void addGoalFunctionForOptP(LpSolve solver) throws LpSolveException {
            var goalFunction = new double[] {1., 1., 1., 1.};

            solver.setInt(1, true);
            solver.setInt(2, true);
            solver.setInt(3, true);
            solver.setInt(4, true);
            solver.setLowbo(1, 0.0);
            solver.setLowbo(2, 0.0);
            solver.setLowbo(3, 0.0);
            solver.setLowbo(4, 0.0);
            solver.setColName(1, "x1");
            solver.setColName(2, "x2");
            solver.setColName(3, "x3");
            solver.setColName(4, "x4");

            solver.setObjFn(goalFunction);
        }

        private void addVarsConstraints(LpSolve solver) throws LpSolveException {
            solver.addConstraintex(4, new double[] {1., 1., 0., 0.}, new int[] {1, 2, 3, 4}, LpSolve.EQ, 2.);
            solver.addConstraintex(4, new double[] {0., 0., 1., 1.}, new int[] {1, 2, 3, 4}, LpSolve.EQ, 3.);
        }

        private LpSolve createSolver() throws LpSolveException {
            var result = LpSolve.makeLp(0, 4);
            result.setSolutionlimit(10);
            result.setOutputfile("./result.txt");
            result.setMinim();
            addGoalFunctionForOptP(result);
            addVarsConstraints(result);
            return result;
        }

        public double[] getSolve() throws LpSolveException {
            var solver = createSolver();
            solver.solve();
            var vars = new double[4];
            var constraints = new double[2];
            solver.getVariables(vars);
            solver.getConstraints(constraints);
            return solver.getPtrVariables();
        }
    }

    // Ожидаемый результат [2, 0, 0, 3]
    @Test
    public void getAllResults() {
        try {
            var solver = new Solver();
            System.out.println(Arrays.toString(solver.getSolve()));
        } catch (LpSolveException e) {
            System.out.println(e);
        }
    }
}
