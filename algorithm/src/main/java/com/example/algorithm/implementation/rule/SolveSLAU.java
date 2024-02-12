package com.example.algorithm.implementation.rule;

import com.example.algorithm.context.DataContext;
import com.example.algorithm.entity.AlternativeEntity;
import com.example.algorithm.entity.AlternativePair;
import com.example.algorithm.utils.AlternativeUtils;
import com.example.algorithm.utils.ValueCalculatorUtils;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum LimitationSign {
    EQ, LEQ, GEQ
}

// Принимает вид coef1 * x1 + coef2 * x2 + ... <= value, где sign - <=, =, >=
@AllArgsConstructor
class Limitation {
    double[] coefs;
    double value;
    LimitationSign sign;

    static Limitation mul(Limitation limitation, double val) {
        var result = new Limitation(limitation);
        result.mul(val);
        return result;
    }

    static Limitation sub(Limitation lim1, Limitation lim2) {
        var result = new Limitation(lim1);
        result.sub(lim2);
        return result;
    }

    Limitation(Limitation toCopy) {
        this.coefs = toCopy.coefs.clone();
        this.value = toCopy.value;
        this.sign = toCopy.sign;
    }

    void mul(double val) {
        if (val < .0) {
            if (sign == LimitationSign.LEQ) {
                sign = LimitationSign.GEQ;
            } else if (sign == LimitationSign.GEQ) {
                sign = LimitationSign.LEQ;
            }
        }
        value *= val;
        for (var i = 0; i < coefs.length; ++i) {
            coefs[i] *= val;
        }
    }

    void div(double val) {
        mul(1. / val);
    }

    private void calculateSignToAdd(Limitation lim) {
        if ((sign == LimitationSign.GEQ && lim.sign == LimitationSign.LEQ)
            || (sign == LimitationSign.LEQ && lim.sign == LimitationSign.GEQ)) {
            throw new IllegalStateException("Бессмысленно складывать/вычитать ограничения знаков <=, >=");
        }

        if (sign != lim.sign && sign == LimitationSign.EQ) {
            sign = lim.sign;
        }
    }

    void add(Limitation toAdd) {
        for (var i = 0; i < coefs.length; ++i) {
            coefs[i] += toAdd.coefs[i];
        }
        value += toAdd.value;
        calculateSignToAdd(toAdd);
    }

    void sub(Limitation toSub) {
        var toAdd = mul(toSub, -1.);
        add(toAdd);
    }

    int findFirstNonZero() {
        for (var i = 0; i < coefs.length; ++i) {
            if (coefs[i] != .0) {
                return i;
            }
        }
        return -1;
    }
}

@AllArgsConstructor
class SLAU {
    List<Limitation> limits;

    int numRows() {
        if (limits == null) {
            return 0;
        }
        return limits.size();
    }

    int numCols() {
        if (numRows() > 0) {
            return limits.get(0).coefs.length;
        }
        return 0;
    }

    double getElement(int row, int col) {
        return limits.get(row).coefs[col];
    }
}

class SLAUAnswerFinder {
    private boolean[] varIsExpressed;
    private List<Limitation> limitations;
    private Map<Integer, Limitation> basisVarToLimitation;

    private SLAUAnswerFinder() {
    }

    SLAUAnswerFinder(SLAU slau) {
        varIsExpressed = new boolean[slau.numCols()];
        Arrays.fill(varIsExpressed, false);
        limitations = new ArrayList<>();
        basisVarToLimitation = new HashMap<>();
        for (var limit : slau.limits) {
            var coefs = limit.coefs;
            if (Arrays.stream(coefs).anyMatch(it -> it != .0)) {
                limitations.add(limit);
            }
        }
    }

    private int findFirstNotExpressed() {
        for (var i = 0; i < varIsExpressed.length; ++i) {
            if (!varIsExpressed[i]) {
                return i;
            }
        }
        return -1;
    }

    private List<Limitation> findLimitsWithVar(int index) {
        var result = new ArrayList<Limitation>();
        for (var limit : limitations) {
            if (limit.coefs[index] != .0) {
                var limitCopy = new Limitation(limit);
                limitCopy.mul(limit.coefs[index]);
                result.add(limitCopy);
            }
        }
        return result;
    }

    private void addLeqLimit(List<Limitation> leqLimits, Limitation add) {
        var copyLimits = new ArrayList<>(leqLimits);
        for (var limit : copyLimits) {
            var subValue = limit.value - add.value;
            var subCoefs = limit.coefs.clone();
            for (var i = 0; i < subCoefs.length; ++i) {
                subCoefs[i] -= add.coefs[i];
            }
            if (Arrays.stream(subCoefs).noneMatch(it -> it > .0) && subValue < .0) {
                return;
            }
            if (Arrays.stream(subCoefs).noneMatch(it -> it < .0) && subValue > .0) {
                leqLimits.remove(limit);
            }
        }
        leqLimits.add(add);
    }

    private void addGeqLimit(List<Limitation> geqLimits, Limitation add) {
        var copyLimits = new ArrayList<>(geqLimits);
        for (var limit : copyLimits) {
            var subValue = limit.value - add.value;
            var subCoefs = limit.coefs.clone();
            for (var i = 0; i < subCoefs.length; ++i) {
                subCoefs[i] -= add.coefs[i];
            }
            if (Arrays.stream(subCoefs).noneMatch(it -> it < .0) && subValue > .0) {
                return;
            }
            if (Arrays.stream(subCoefs).noneMatch(it -> it > .0) && subValue < .0) {
                geqLimits.remove(limit);
            }
        }
        geqLimits.add(add);
    }

    private Limitation generateAddLimit(Limitation geqLimit, Limitation leqLimit) {
        var value = leqLimit.value - geqLimit.value;
        var coefs = leqLimit.coefs.clone();
        for (var index = 0; index < coefs.length; ++index) {
            coefs[index] -= geqLimit.coefs[index];
        }
        return new Limitation(coefs, value, LimitationSign.LEQ);
    }

    private void processLimitsForVar(int varIndex, List<Limitation> varLimits) {
        var leqLimits = new ArrayList<Limitation>();
        var geqLimits = new ArrayList<Limitation>();

        for (var limit : varLimits) {
            limit.div(limit.coefs[varIndex]);
            if (limit.sign == LimitationSign.LEQ) {
                addLeqLimit(leqLimits, limit);
            } else if (limit.sign == LimitationSign.GEQ) {
                addGeqLimit(geqLimits, limit);
            }
        }

        for (var index = 0; index < limitations.size(); ++index) {
            if (limitations.get(index).coefs[varIndex] != .0) {
                limitations.remove(index);
                index -= 1;
            }
        }

        for (var geqLimit : geqLimits) {
            for (var leqLimit : leqLimits) {
                limitations.add(generateAddLimit(geqLimit, leqLimit));
            }
        }
    }

    // Предназначена для поиска возможного решения для конкретной переменной
    private List<List<Integer>> addResults(
        List<List<Integer>> currentResults, List<Limitation> varLimits, int varIndex) {
        var copyResults = new ArrayList<>(currentResults);
        for (var result : currentResults) {
            copyResults.remove(result);
            var min = 0;
            var max = Integer.MAX_VALUE;
            for (var limit : varLimits) {
                var sumVars = .0;
                for (var index = 0; index < result.size(); ++index) {
                    sumVars += result.get(index) * limit.coefs[index];
                }
                var value = limit.value - sumVars;
                if (limit.sign == LimitationSign.LEQ && max > value) {
                    max = (int) Math.floor(value);
                } else if (limit.sign == LimitationSign.GEQ && min < value) {
                    min = (int) Math.ceil(value);
                }
            }
            for (var value = min; value <= max; ++value) {
                var copyResult = new ArrayList<>(result);
                copyResult.set(varIndex, value);
                copyResults.add(copyResult);
            }
        }
        return copyResults;
    }

    private List<List<Integer>> resolveLimits() {
        var index = findFirstNotExpressed();
        if (index == -1) {
            var result = new ArrayList<Integer>();
            for (var i = 0; i < varIsExpressed.length; ++i) {
                result.add(0);
            }
            return List.of(result);
        }

        var varLimits = findLimitsWithVar(index);
        processLimitsForVar(index, varLimits);
        varIsExpressed[index] = true;

        var currentResult = resolveLimits();
        return addResults(currentResult, varLimits, index);
    }

    private void setEquations() {
        for (var index = 0; index < varIsExpressed.length; ++index) {
            Limitation basisLimit = null;
            for (var limit : limitations) {
                if (limit.coefs[index] != .0) {
                    if (basisLimit != null) {
                        basisLimit = null;
                        break;
                    }
                    basisLimit = limit;
                }
            }
            if (basisLimit != null) {
                basisLimit.div(basisLimit.coefs[index]);
                varIsExpressed[index] = true;
                basisVarToLimitation.put(index, basisLimit);
            }
        }
    }

    private void generateLimitsFromBasis() {
        for (var basisIndex : basisVarToLimitation.keySet()) {
            var limit = basisVarToLimitation.get(basisIndex);
            var value = limit.value;
            var coefs = limit.coefs.clone();
            coefs[basisIndex] = .0;
            limitations.remove(limit);
            limitations.add(new Limitation(coefs, value, LimitationSign.LEQ));
        }
    }

    List<List<Integer>> findAnswers() {
        setEquations();
        generateLimitsFromBasis();
        var results = new ArrayList<List<Integer>>();
        for (var result : resolveLimits()) {
            var needAdd = true;
            for (var basisIndex : basisVarToLimitation.keySet()) {
                var limit = basisVarToLimitation.get(basisIndex);
                var varSum = .0;
                for (var i = 0; i < limit.coefs.length; ++i) {
                    varSum += limit.coefs[i] * result.get(i);
                }
                var value = limit.value - varSum;
                if ((value % 1) != 0) {
                    needAdd = false;
                    break;
                }
                result.set(basisIndex, (int) value);
            }
            if (needAdd) {
                results.add(result);
            }
        }
        return results;
    }
}

public class SolveSLAU {
    private static SLAU generateSLAU(
        AlternativeEntity a, AlternativeEntity b, DataContext dataContext) {
        var D = ValueCalculatorUtils.calculateDMatrix(dataContext);
        var E = ValueCalculatorUtils.calculateEMatrix(dataContext);
        var numCols = D.numCols + 2 * E.numCols;
        var numRows = D.numRows;
        var limitaions = new ArrayList<Limitation>();
        var pair = new AlternativePair(a, b);
        var deltaAB = AlternativeUtils.calculateDelta(pair, dataContext.getCriterias());
        for (var row = 0; row < numRows; ++row) {
            var coefs = new double[numCols];
            for (var col = 0; col < D.numCols; ++col) {
                coefs[col] = D.get(row, col);
            }
            for (var col = 0; col < E.numCols; ++col) {
                coefs[col + D.numCols] = E.get(row, col);
                coefs[col + D.numCols + E.numCols] = E.get(row, col);
            }
            var value = deltaAB.get(row);
            limitaions.add(new Limitation(coefs, value, LimitationSign.EQ));
        }
        return new SLAU(limitaions);
    }

    private static void gaussDirectStepForRow(SLAU slau, int row) {
        var col = slau.limits.get(row).findFirstNonZero();
        if (col == -1) {
            return;
        }

        var coef = slau.getElement(row, col);
        slau.limits.get(row).div(coef);

        for (var r = row + 1; r < slau.numRows(); ++r) {
            coef = slau.getElement(r, col);
            if (coef == .0) continue;
            var toSub = Limitation.mul(slau.limits.get(row), coef);
            slau.limits.get(r).sub(toSub);
        }
    }

    private static void gaussReverseStepForRow(SLAU slau, int row) {
        if (row == 0) {
            return;
        }
        var col = slau.limits.get(row).findFirstNonZero();
        if (col == -1) {
            return;
        }

        for (var r = row - 1; r >= 0; --r) {
            var coef = slau.getElement(r, col);
            ;
            if (coef == .0) continue;
            var toSub = Limitation.mul(slau.limits.get(row), coef);
            slau.limits.get(r).sub(toSub);
        }
    }

    private static boolean isCorrectGaussSolution(SLAU slau) {
        for (var row = 0; row < slau.numRows(); ++row) {
            if (slau.limits.get(row).value == .0) continue;
            var allZero = true;
            var allPos = true;
            var allNeg = true;
            for (var col = 0; col < slau.numCols(); ++col) {
                if (slau.getElement(row, col) > .0) {
                    allNeg = false;
                    allZero = false;
                } else if (slau.getElement(row, col) < .0) {
                    allPos = false;
                    allZero = false;
                } else {
                    allNeg = false;
                    allPos = false;
                }
            }
            if (allZero || (allPos && slau.limits.get(row).value < 0.)
                || (allNeg && slau.limits.get(row).value > 0.)) {
                return false;
            }
        }
        return true;
    }

    private static boolean gaussMethod(SLAU slau) {
        // Прямой ход
        for (var row = 0; row < slau.numRows(); ++row) {
            gaussDirectStepForRow(slau, row);
        }

        // Обратный ход
        for (var row = (int) (slau.numRows() - 1); row > 0; --row) {
            gaussReverseStepForRow(slau, row);
        }

        // Проверка результата на валидность
        return isCorrectGaussSolution(slau);
    }

    // Возвращается null, если решений нет
    public static List<List<Integer>> generateAndSolve(
        AlternativeEntity a, AlternativeEntity b, DataContext dataContext) {
        var slau = generateSLAU(a, b, dataContext);
        var correctGaussSolution = gaussMethod(slau);
        if (!correctGaussSolution) {
            return null;
        }
        var finder = new SLAUAnswerFinder(slau);
        return finder.findAnswers();
    }
}
