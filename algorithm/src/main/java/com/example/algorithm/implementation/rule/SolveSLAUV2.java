//package com.example.algorithm.implementation.rule;
//
//import com.example.algorithm.utils.AlternativeUtils;
//import com.example.algorithm.utils.ValueCalculatorUtils;
//import lombok.AllArgsConstructor;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//class Equation {
//    @AllArgsConstructor
//    static class Expression {
//        double[] coefs;
//        double value;
//
//        void subExpression(Expression exp) {
//            if (exp.coefs.length != coefs.length) {
//                throw new IllegalStateException("Запрещено вычитать выражения разных размерностей");
//            }
//            for (var index = 0; index < coefs.length; ++index) {
//                coefs[index] -= exp.coefs[index];
//            }
//            value -= exp.value;
//        }
//
//        void multiplication(double value) {
//            for (var index = 0; index < coefs.length; ++index) {
//                coefs[index] *= value;
//            }
//            this.value *= value;
//        }
//
//        void division(double value) {
//            if (value == .0) {
//                throw new IllegalStateException("Запрещено делить на 0");
//            }
//            multiplication(1. / value);
//        }
//
//        // Возвращает -1, в случае, если все коэффициенты 0
//        int findFirstNonZero() {
//            for (var index = 0; index < coefs.length; ++index) {
//                if (coefs[index] != .0) {
//                    return index;
//                }
//            }
//            return -1;
//        }
//
//        Expression generateByVars(int[] values) {
//            if (values.length != coefs.length) {
//                throw new IllegalStateException("В выражение подставлены данные несоответствующей размерности");
//            }
//            var result = new Expression(coefs.clone(), value);
//            for (var index = 0; index < coefs.length; ++index) {
//                if (values[index] != -1) {
//                    result.value += values[index] * result.coefs[index];
//                    result.coefs[index] = .0;
//                }
//            }
//            return result;
//        }
//    }
//
//    private final Expression leftPart;
//    private final Expression rightPart;
//
//    static Equation division(Equation eq, double value) {
//        var result = new Equation(eq);
//        result.division(value);
//        return result;
//    }
//
//    static Equation multiplication(Equation eq, double value) {
//        var result = new Equation(eq);
//        result.multiplication(value);
//        return result;
//    }
//
//    static Equation substraction(Equation eq1, Equation eq2) {
//        var result = new Equation(eq1);
//        result.subEquation(eq2);
//        return result;
//    }
//
//    Equation(double[] coefs, double value) {
//        leftPart = new Expression(coefs.clone(), .0);
//        rightPart = new Expression(new double[coefs.length], value);
//    }
//
//    Equation(Equation toCopy) {
//        leftPart = new Expression(toCopy.leftPart.coefs.clone(), toCopy.leftPart.value);
//        rightPart = new Expression(toCopy.rightPart.coefs.clone(), toCopy.rightPart.value);
//    }
//
//    int getLength() {
//        return leftPart.coefs.length;
//    }
//
//    Expression getLeftPart() {
//        return leftPart;
//    }
//
//    Expression getRightPart() {
//        return rightPart;
//    }
//
//    void division(double value) {
//        leftPart.division(value);
//        rightPart.division(value);
//    }
//
//    void multiplication(double value) {
//        leftPart.multiplication(value);
//        rightPart.multiplication(value);
//    }
//
//    void subEquation(Equation eq) {
//        leftPart.subExpression(eq.leftPart);
//        rightPart.subExpression(eq.rightPart);
//    }
//
//    void expressVariable(int col) {
//        var rightCoef = rightPart.coefs[col];
//        rightPart.coefs[col] = .0;
//        leftPart.coefs[col] -= rightCoef;
//        if (leftPart.coefs[col] == .0) {
//            throw new IllegalStateException("Невозможно выразить переменную через равенство");
//        }
//        for (var index = 0; index < leftPart.coefs.length; ++index) {
//            if (index != col) {
//                var leftCoef = leftPart.coefs[index];
//                leftPart.coefs[index] = .0;
//                rightPart.coefs[index] -= leftCoef;
//            }
//        }
//        var leftValue = leftPart.value;
//        leftPart.value = .0;
//        rightPart.value -= leftValue;
//    }
//
//    boolean isCorrectValues(int[] values) {
//        var rightExpression = rightPart.generateByVars(values);
//        return !(Arrays.stream(rightExpression.coefs).allMatch(it -> it < .0)
//            && rightExpression.value < .0);
//    }
//}
//
//class SLAU {
//    private List<Equation> equations;
//    private List<Integer> basisIndex;
//    private SLAU() {}
//
//    static class GaussMethod {
//        private static void gaussDirectStepForRow(SLAU slau, int row) {
//            var equation = slau.getEquation(row);
//            var col = equation.getLeftPart().findFirstNonZero();
//            if (col == -1) {
//                return;
//            }
//
//            var divValue = equation.getLeftPart().coefs[col];
//            equation.division(divValue);
//
//            for (var r = row + 1; r < slau.numRows(); ++r) {
//                var eq = slau.getEquation(r);
//                divValue = eq.getLeftPart().coefs[col];
//                if (divValue == .0) continue;
//                var toSub = Equation.multiplication(equation, divValue);
//                eq.subEquation(toSub);
//            }
//        }
//
//        private static void gaussReverseStepForRow(SLAU slau, int row) {
//            if (row == 0) {
//                return;
//            }
//            var equation = slau.getEquation(row);
//            var col = equation.getLeftPart().findFirstNonZero();
//            if (col == -1) {
//                return;
//            }
//
//            for (var r = row - 1; r >= 0; --r) {
//                var eq = slau.getEquation(r);
//                var divValue = eq.getLeftPart().coefs[col];
//                if (divValue == .0) continue;
//                var toSub = Equation.multiplication(equation, divValue);
//                slau.getEquation(r).subEquation(toSub);
//            }
//        }
//
//        static void gaussMethod(SLAU slau) {
//            // Прямой ход
//            for (var row = 0; row < slau.numRows(); ++row) {
//                gaussDirectStepForRow(slau, row);
//            }
//
//            // Обратный ход
//            for (var row = (int) (slau.numRows() - 1); row > 0; --row) {
//                gaussReverseStepForRow(slau, row);
//            }
//        }
//    }
//
//    private void prepareSLAU() {
//        GaussMethod.gaussMethod(this);
//        basisIndex = new ArrayList<>();
//        for (var equation : new ArrayList<>(equations)) {
//            var column = equation.getLeftPart().findFirstNonZero();
//            if (column != -1) {
//                equation.expressVariable(column);
//                basisIndex.add(column);
//            } else if (equation.getRightPart().value == .0) {
//                equations.remove(equation);
//            }
//        }
//    }
//
//    boolean hasCorrectSolution(int[] vars) {
//        for (var equation : equations) {
//            if (!equation.isCorrectValues(vars)) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public static SLAU generateSLAU(
//        AlternativeEntity a, AlternativeEntity b, DataContext dataContext) {
//        var D = ValueCalculatorUtils.calculateDMatrix(dataContext);
//        var E = ValueCalculatorUtils.calculateEMatrix(dataContext);
//        var numCols = D.numCols + 2 * E.numCols;
//        var numRows = D.numRows;
//        var equations = new ArrayList<Equation>();
//        var pair = new AlternativePair(a, b);
//        var deltaAB = AlternativeUtils.calculateDelta(pair, dataContext.getCriterias());
//        for (var row = 0; row < numRows; ++row) {
//            var coefs = new double[numCols];
//            for (var col = 0; col < D.numCols; ++col) {
//                coefs[col] = D.get(row, col);
//            }
//            for (var col = 0; col < E.numCols; ++col) {
//                coefs[col + D.numCols] = E.get(row, col);
//                coefs[col + D.numCols + E.numCols] = -E.get(row, col);
//            }
//            var value = deltaAB.get(row);
//            equations.add(new Equation(coefs, value));
//        }
//        var SLAU = new SLAU();
//        SLAU.equations = equations;
//        SLAU.prepareSLAU();
//        return SLAU;
//    }
//
//    public int numRows() {
//        return equations.size();
//    }
//
//    public int numCols() {
//        if (equations.isEmpty()) {
//            return 0;
//        }
//        return equations.get(0).getLength();
//    }
//
//    public Equation getEquation(int row) {
//        if (row < 0 || row >= equations.size()) {
//            return null;
//        }
//        return equations.get(row);
//    }
//
//    public List<Integer> getBasisIndex() {
//        return basisIndex;
//    }
//
//    public double getValueForBasis(int index, int[] values) {
//        for (var equation : equations) {
//            if (equation.getLeftPart().coefs[index] == 1.0) {
//                return equation.getRightPart().generateByVars(values).value;
//            }
//        }
//        return -1.0;
//    }
//}
//
//public class SolveSLAUV2 {
//    private static final int MAX_RULE_NUM = 3;
//
//    private SLAU slau;
//    private DataContext dataContext;
//    private int[] curPossibleSolution;
//    private int curIndex;
//    private SolveSLAUV2() {}
//
//    private void preparePossibleSolution() {
//        curPossibleSolution = new int[slau.numCols()];
//        for (var index : slau.getBasisIndex()) {
//            curPossibleSolution[index] = -1;
//        }
//        curIndex = curPossibleSolution.length - 1;
//    }
//
//    private boolean checkSolutionForInteger() {
//        for (var index : slau.getBasisIndex()) {
//            var value = slau.getValueForBasis(index, curPossibleSolution);
//            if (value == -1 || (value % 1) != 0) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private void toNextSolution() {
//        var prevIndex = curIndex;
//        do {
//            curIndex += 1;
//        } while (slau.getBasisIndex().contains(curIndex) && curIndex < curPossibleSolution.length);
//        if (curIndex >= curPossibleSolution.length) {
//            return;
//        } else if (prevIndex >= 0) {
//            curPossibleSolution[prevIndex] = 0;
//        }
//        do {
//            curPossibleSolution[curIndex] += 1;
//        } while (!slau.hasCorrectSolution(curPossibleSolution) && curPossibleSolution[curIndex] < MAX_RULE_NUM);
//    }
//
//    private void generateNextSolution() {
//        if (curPossibleSolution == null) return;
//        var findSolution = false;
//        while (!findSolution) {
//            if (curIndex >= curPossibleSolution.length) {
//                curPossibleSolution = null;
//                return;
//            }
//            if (curIndex != -1 && curPossibleSolution[curIndex] < MAX_RULE_NUM) {
//                do {
//                    curIndex -= 1;
//                } while (slau.getBasisIndex().contains(curIndex) && curIndex >= 0);
//                if (curIndex != -1) {
//                    curPossibleSolution[curIndex] = 0;
//                    while (!slau.hasCorrectSolution(curPossibleSolution)
//                        && curPossibleSolution[curIndex] < MAX_RULE_NUM) {
//                        curPossibleSolution[curIndex] += 1;
//                    }
//                }
//            } else if (curIndex != -1 && curPossibleSolution[curIndex] == MAX_RULE_NUM) {
//                toNextSolution();
//            } else {
//                if (!slau.hasCorrectSolution(curPossibleSolution)) {
//                    curPossibleSolution = null;
//                }
//                if (checkSolutionForInteger()) {
//                    findSolution = true;
//                } else {
//                    toNextSolution();
//                }
//            }
//        }
//    }
//
//    private List<Rule> processAnswer() {
//        var result = new ArrayList<Rule>();
//        var answer = curPossibleSolution.clone();
//        for (var index : slau.getBasisIndex()) {
//            var value = slau.getValueForBasis(index, curPossibleSolution);
//            if (value == -1.0) {
//                throw new IllegalStateException("Переменная не может быть выражена");
//            }
//            answer[index] = (int) value;
//        }
//        var index = 0;
//        for (var pIndex = 0; pIndex < dataContext.getP().size(); ++pIndex) {
//            for (var i = 0; i < answer[pIndex]; ++i) {
//                var rule = new Rule(dataContext.getP().get(pIndex));
//                result.add(rule);
//            }
//        }
//        index += dataContext.getP().size();
//        for (var iIndex = 0; iIndex < dataContext.getI().size(); ++iIndex) {
//            for (var i = 0; i < answer[iIndex + index]; ++i) {
//                var rule = new Rule(dataContext.getI().get(iIndex));
//                result.add(rule);
//            }
//        }
//        index += dataContext.getI().size();
//        for (var iIndex = 0; iIndex < dataContext.getI().size(); ++iIndex) {
//            for (var i = 0; i < answer[iIndex + index]; ++i) {
//                var rule = new Rule(dataContext.getI().get(iIndex));
//                var pair = new AlternativePair(rule.getPair().getSecond(), rule.getPair().getFirst());
//                rule.setPair(pair);
//                result.add(rule);
//            }
//        }
//        return result;
//    }
//
//    public static SolveSLAUV2 generateTask(
//        AlternativeEntity a, AlternativeEntity b, DataContext dataContext) {
//        var solver = new SolveSLAUV2();
//        solver.slau = SLAU.generateSLAU(a, b, dataContext);
//        solver.dataContext = dataContext;
//        solver.preparePossibleSolution();
//        if (!solver.slau.hasCorrectSolution(solver.curPossibleSolution)) {
//            solver.curPossibleSolution = null;
//        }
//        return solver;
//    }
//
//    public List<Rule> getNextSolutionOrNull() {
//        generateNextSolution();
//        if (curPossibleSolution == null) return null;
//        var answer = processAnswer();
//        toNextSolution();
//        return answer;
//    }
//}
