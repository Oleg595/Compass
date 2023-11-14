package com.example.compas.service;

import com.example.compas.context.DataContext;
import com.example.compas.entity.AlternativeEntity;
import com.example.compas.util.AlternativePair;
import lombok.AllArgsConstructor;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@AllArgsConstructor
public class AlgorithmService {
    private final DataContext dataContext;

    private List<Double> calculateDelta(AlternativeEntity alt) {
        var result = new ArrayList<>(Collections.nCopies(dataContext.getM(), 0.0));
        var count = 0;
        for (var criteria : dataContext.getCriterias()) {
            var values = new ArrayList<>(criteria.getValues());
            var index = values.indexOf(alt.getValueByCriteria(criteria));
            result.set(count + index, 1.0);
            count += values.size();
        }
        return result;
    }

    private List<Double> calculateDelta(AlternativeEntity alt1, AlternativeEntity alt2) {
        var result = new ArrayList<>(Collections.nCopies(dataContext.getM(), 0.0));
        var count = 0;
        for (var criteria : dataContext.getCriterias()) {
            var values = new ArrayList<>(criteria.getValues());
            var value1 = alt1.getValueByCriteria(criteria);
            var value2 = alt2.getValueByCriteria(criteria);
            var index1 = values.indexOf(value1);
            var index2 = values.indexOf(value2);
            if (index1 != index2) {
                result.set(count + index1, 1.0);
                result.set(count + index2, -1.0);
            }
            count += values.size();
        }
        return result;
    }

    private DMatrixRMaj calculateDMatrix() {
        var p = dataContext.getP();
        var result = new DMatrixRMaj(dataContext.getM(), p.size());
        for (var col = 0; col < p.size(); ++col) {
            var delta = calculateDelta(p.get(col).getFirst(), p.get(col).getSecond());
            for (var row = 0; row < delta.size(); ++row) {
                result.set(row, col, delta.get(row));
            }
        }
        return result;
    }

    private DMatrixRMaj calculateEMatrix() {
        var i = dataContext.getI();
        var result = new DMatrixRMaj(dataContext.getM(), i.size());
        for (var col = 0; col < i.size(); ++col) {
            var delta = calculateDelta(i.get(col).getFirst(), i.get(col).getSecond());
            for (var row = 0; row < delta.size(); ++row) {
                result.set(row, col, delta.get(row));
            }
        }
        return result;
    }

    private class CalculateOptimalP {
        private void addGoalFunctionForOptP(LpSolve solver) throws LpSolveException {
            var dim = 2 + dataContext.getM() + dataContext.getP().size();
            var goalFunction = new double[dim];
            goalFunction[0] = 1.0;
            goalFunction[1] = -1.0;
            solver.setObjFn(goalFunction);
        }

        private void addDConstraints(LpSolve solver) throws LpSolveException {
            var transposeD = calculateDMatrix();
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

        private void addEConstraints(LpSolve solver) throws LpSolveException {
            var transposeE = calculateEMatrix();
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

        private void addVarsConstraints(LpSolve solver) throws LpSolveException {
            var dim = 2 + dataContext.getM() + dataContext.getP().size();
            for (var index = 0; index < dataContext.getP().size(); ++index) {
                var constraint = new double[dim];
                constraint[0] = -1.0;
                constraint[1] = 1.0;
                constraint[2 + dataContext.getM() + index] = 1.0;
                solver.addConstraint(constraint, LpSolve.LE, 0.0);
            }
        }

        private LpSolve createSolver() throws LpSolveException {
            var dim = 2 + dataContext.getM() + dataContext.getP().size();
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
            var solver = createSolver();
            solver.solve();
            return solver.getPtrVariables()[0] - solver.getPtrVariables()[1];
        }
    }

    private class CalculateOptimalV {
        private void addGoalFunctionForOptP(LpSolve solver) throws LpSolveException {
            var dim = dataContext.getM() + dataContext.getP().size();
            var goalFunction = new double[dim];
            for (var index = 0; index < dataContext.getM(); ++index) {
                goalFunction[index] = 1.0;
            }
            solver.setObjFn(goalFunction);
        }

        private void addDConstraints(LpSolve solver) throws LpSolveException {
            var transposeD = calculateDMatrix();
            var dim = dataContext.getM() + dataContext.getP().size();
            CommonOps_DDRM.transpose(transposeD);
            for (var row = 0; row < transposeD.numRows; ++row) {
                var constraint = new double[dim];
                for (var col = 0; col < transposeD.numCols; ++col) {
                    constraint[col] = transposeD.get(row, col);
                }
                constraint[dataContext.getM() + row] = 1.0;
                solver.addConstraint(constraint, LpSolve.GE, 1.0);
            }
        }

        private void addEConstraints(LpSolve solver) throws LpSolveException {
            var transposeE = calculateEMatrix();
            var dim = dataContext.getM() + dataContext.getP().size();
            CommonOps_DDRM.transpose(transposeE);
            for (var row = 0; row < transposeE.numRows; ++row) {
                var constraint = new double[dim];
                for (var col = 0; col < transposeE.numCols; ++col) {
                    constraint[col] = transposeE.get(row, col);
                }
                solver.addConstraint(constraint, LpSolve.EQ, 0.0);
            }
        }

        private void addVarsConstraints(LpSolve solver, double optP) throws LpSolveException {
            var dim = 2 + dataContext.getM() + dataContext.getP().size();
            for (var index = 0; index < dataContext.getP().size(); ++index) {
                var constraint = new double[dim];
                constraint[2 + dataContext.getM() + index] = 1.0;
                solver.addConstraint(constraint, LpSolve.LE, optP);
            }
        }

        private LpSolve createSolver(double optP) throws LpSolveException {
            var dim = 2 + dataContext.getM() + dataContext.getP().size();
            var result = LpSolve.makeLp(0, dim);
            result.setOutputfile("./result.txt");
            result.setMinim();
            addGoalFunctionForOptP(result);
            addDConstraints(result);
            addEConstraints(result);
            addVarsConstraints(result, optP);
            return result;
        }

        public double[] getOptV(double optP) throws LpSolveException {
            var solver = createSolver(optP);
            solver.solve();
            var result = new double[dataContext.getM()];
            for (var index = 0; index < dataContext.getM(); ++index) {
                result[index] = solver.getPtrVariables()[index];
            }
            return result;
        }
    }

    private List<Double> calculateForecastFunction() throws LpSolveException {
        var pCalc = new CalculateOptimalP();
        var optP = pCalc.getOptP();
        var vCalc = new CalculateOptimalV();
        var result = new ArrayList<Double>();
        for (double v : vCalc.getOptV(optP)) {
            result.add(v);
        }
        return result;
    }

    private double multVectors(List<Double> vec1, List<Double> vec2) {
        assert vec1.size() == vec2.size();
        var result = 0.0;
        for (var index = 0; index < vec1.size(); ++index) {
            result += vec1.get(index) * vec2.get(index);
        }
        return result;
    }

    private AlternativeEntity findMaxAlt(List<AlternativeEntity> alts, List<Double> optV) {
        var maxVal = Double.MIN_VALUE;
        AlternativeEntity result = null;
        for (var alt : alts) {
            var altValue = multVectors(calculateDelta(alt), optV);
            if (altValue > maxVal) {
                result = alt;
                maxVal = altValue;
            }
        }
        return result;
    }

    private class ModusPonensCalculation {
        private void addGoalFunction(LpSolve solver) throws LpSolveException {
            var dim = dataContext.getP().size() + dataContext.getI().size();
            var goalFunction = new double[dim];
            for (var index = 0; index < dataContext.getP().size(); ++index) {
                goalFunction[index] = 1.0;
            }
            solver.setObjFn(goalFunction);
        }

        private void addEqualConstraints(LpSolve solver, List<Double> delta) throws LpSolveException {
            var dim = dataContext.getP().size() + dataContext.getI().size();
            var D = calculateDMatrix();
            var E = calculateEMatrix();
            for (var row = 0; row < dataContext.getM(); ++row) {
                var constraint = new double[dim];
                for (var dCol = 0; dCol < dataContext.getP().size(); ++dCol) {
                    constraint[dCol] = D.get(row, dCol);
                }
                for (var eCol = 0; eCol < dataContext.getI().size(); ++eCol) {
                    constraint[D.numCols + eCol] = E.get(row, eCol);
                }
                solver.addConstraint(constraint, LpSolve.EQ, delta.get(row));
            }
        }

        private LpSolve createSolver(List<Double> delta) throws LpSolveException {
            var dim = dataContext.getP().size() + dataContext.getI().size();
            var solver = LpSolve.makeLp(0, dim);
            solver.setOutputfile("./result.txt");
            solver.setMaxim();
            addGoalFunction(solver);
            addEqualConstraints(solver, delta);
            return solver;
        }

        public List<Double> getResultVector(AlternativeEntity a, AlternativeEntity b) throws LpSolveException {
            var solver = createSolver(calculateDelta(a, b));
            solver.solve();
            if (solver.getStatus() > 1) {
                return new ArrayList<>(Collections.nCopies(dataContext.getP().size(), -1.));
            }
            var result = new ArrayList<Double>();
            for (var index = 0; index < dataContext.getP().size(); ++index) {
                result.add(solver.getPtrVariables()[index]);
            }
            return result;
        }
    }

    private boolean altMorePrior(AlternativeEntity alt1, AlternativeEntity alt2) throws LpSolveException {
        var altsDelta = calculateDelta(alt1, alt2);
        for (var p : dataContext.getP()) {
            var pDelta = calculateDelta(p.getFirst(), p.getSecond());
            if (altsDelta.equals(pDelta)) {
                return true;
            }
        }
        var modusPonens = new ModusPonensCalculation();
        return modusPonens.getResultVector(alt1, alt2).stream().anyMatch(it -> it > 0.0);
    }

    private List<AlternativeEntity> getOptimalAlts() throws LpSolveException {
        var alts = new ArrayList<>(dataContext.getAlts());
        var nonOptAlts = new ArrayList<AlternativeEntity>();
        for (var index1 = 0; index1 < alts.size(); ++index1) {
            for (var index2 = 0; index2 < alts.size(); ++index2) {
                var alt1 = alts.get(index1);
                var alt2 = alts.get(index2);
                if (index1 != index2 && altMorePrior(alt1, alt2)) {
                    nonOptAlts.add(alt2);
                }
            }
        }
        alts.removeAll(nonOptAlts);
        return alts;
    }

    private boolean equalAlts(AlternativeEntity alt1, AlternativeEntity alt2) {
        var altsDelta = calculateDelta(alt1, alt2);
        for (var i : dataContext.getI()) {
            var iDelta = calculateDelta(i.getFirst(), i.getSecond());
            if (altsDelta == iDelta) {
                return true;
            }
        }
        return false;
    }

    private List<AlternativeEntity> getEqualAlts(AlternativeEntity alt) {
        var result = new ArrayList<AlternativeEntity>();
        for (var a : dataContext.getAlts()) {
            if (alt != a && equalAlts(alt, a)) {
                result.add(a);
            }
        }
        return result;
    }

    public AlternativePair calculateOptimalAlternatives(List<Double> optV) throws LpSolveException {
        var alts = getOptimalAlts();

        var a = findMaxAlt(alts, optV);
        alts.remove(a);
        if (a == null) {
            return null;
        }

        var equalAAlts = getEqualAlts(a);
        alts.removeAll(equalAAlts);

        if (alts.size() > 0) {
            if (findMaxAlt(alts, optV) != null) {
                return new AlternativePair(a, findMaxAlt(alts, optV));
            }
            return null;
        } else {
            System.out.println("Лучшая альтернатива " + a);
            return null;
        }
    }

    private boolean kDiff(AlternativeEntity alt1, AlternativeEntity alt2, int k) {
        var delta = calculateDelta(alt1, alt2);
        var diff = 0;
        var count = 0;
        for (var criteria: dataContext.getCriterias()) {
            for (var index = 0; index < criteria.getValues().size(); ++index) {
                if (delta.get(index + count) == 1.0) {
                    ++diff;
                }
            }
            count += criteria.getValues().size();
        }
        return diff == k;
    }

    private AlternativePair pairWithoutSimilars(AlternativeEntity alt1, AlternativeEntity alt2) {
        var delta1 = calculateDelta(alt1);
        var delta2 = calculateDelta(alt2);
        for (var index = 0; index < dataContext.getM(); ++index) {
            if (delta1.get(index) == 1 && delta2.get(index) == 1) {
                delta1.set(index, 0.0);
                delta2.set(index, 0.0);
            }
        }
        return new AlternativePair(
            dataContext.createAltByDelta(delta1), dataContext.createAltByDelta(delta2));
    }

    private List<AlternativePair> getDefaultPairs(List<Double> optV, int k) throws LpSolveException {
        var alts = getOptimalAlts();
        var a = findMaxAlt(alts, optV);
        if (a != null) {
            var equalAAlts = getEqualAlts(a);
            alts.removeAll(equalAAlts);
        }
        var result = new ArrayList<AlternativePair>();
        for (var index1 = 0; index1 < alts.size(); ++index1) {
            for (var index2 = index1 + 1; index2 < alts.size(); ++index2) {
                var alt1 = alts.get(index1);
                var alt2 = alts.get(index2);
                result.add(pairWithoutSimilars(alt1, alt2));
            }
        }
        return result;
    }

    @AllArgsConstructor
    private class AlternativesCalculation {
        private final int k;
        private final List<Double> optV;
        private final List<Double> delta;

        private int getT() {
            return dataContext.getCriterias().size() - k + 1;
        }

        private int getDim() {
            return 2 * getT() * dataContext.getM() + dataContext.getP().size() + dataContext.getI().size() + getT();
        }

        // Целевая функция представлена в виде r1+_1 - r1-_1 + r1+_2 - r1-_1 + ...
        private void addGoalFunction(LpSolve solver) throws LpSolveException {
            var goalFunction = new double[getDim()];
            for (var index = 0; index < dataContext.getM(); ++index) {
                goalFunction[2 * index] = optV.get(index);
                goalFunction[2 * index + 1] = -optV.get(index);
            }
            solver.setObjFn(goalFunction);
            for (var index = 0; index < getT() * dataContext.getM(); ++index) {
                solver.setBinary(2 * index + 1, true);
                solver.setBinary(2 * index + 2, true);
            }
            solver.setBinary(getDim(), true);
        }

        private void addConstraints4(LpSolve solver) throws LpSolveException {
            var D = calculateDMatrix();
            var E = calculateEMatrix();
            for (var row = 0; row < dataContext.getM(); ++row) {
                var constraint = new double[getDim()];
                for (var col = 0; col < getT(); ++col) {
                    constraint[2 * col * dataContext.getM()] = 1.0;
                    constraint[2 * col * dataContext.getM() + 1] = -1.0;
                }
                for (var dCol = 0; dCol < dataContext.getP().size(); ++dCol) {
                    constraint[dCol + 2 * getT() * dataContext.getM()] = D.get(row, dCol);
                }
                for (var eCol = 0; eCol < dataContext.getI().size(); ++eCol) {
                    constraint[D.numCols + 2 * getT() * dataContext.getM() + eCol] = E.get(row, eCol);
                }
                solver.addConstraint(constraint, LpSolve.EQ, delta.get(row));
            }
        }

        private double calcMv() {
            return optV.stream().mapToDouble(it -> it).sum() + 1.;
        }

        private void addConstraints5(LpSolve solver) throws LpSolveException {
            var Mv = calcMv();
            for (var index = 1; index < getT(); ++index) {
                var constraint = new double[getDim()];
                for (var col = 0; col < dataContext.getM(); ++col) {
                    constraint[2 * col] = optV.get(col);
                    constraint[2 * col + 1] = -optV.get(col);
                }
                for (var col = index * dataContext.getM(); col < dataContext.getM(); ++col) {
                    constraint[2 * (index * dataContext.getM() + col)] = -optV.get(col);
                    constraint[2 * (index * dataContext.getM() + col) + 1] = optV.get(col);
                }
                constraint[getDim() - getT() + index] = Mv;
                solver.addConstraint(constraint, LpSolve.LE, Mv);
            }
        }

        private void addConstraints6(LpSolve solver) throws LpSolveException {
            for (var index = 1; index < getT(); ++index) {
                var count = 0;
                for (var criteria : dataContext.getCriterias()) {
                    var constraint = new double[getDim()];
                    for (var col = 0; col < criteria.getValues().size(); ++col) {
                        constraint[2 * (index * dataContext.getM() + col) + count] = 1.0;
                    }
                    count += 2 * criteria.getValues().size();
                    constraint[getDim() - getT() + index] = -1.0;
                    solver.addConstraint(constraint, LpSolve.LE, 0.0);
                }
            }
        }

        private void addConstraints7(LpSolve solver) throws LpSolveException {
            var constraint = new double[getDim()];
            for (var col = 0; col < dataContext.getM(); ++col) {
                constraint[2 * col] = 1.0;
            }
            solver.addConstraint(constraint, LpSolve.GE, 1.0);
        }

        private void addConstraints8(LpSolve solver) throws LpSolveException {
            for (var rNum = 0; rNum < getT(); ++rNum) {
                var count = 0;
                for (var criteria: dataContext.getCriterias()) {
                    var constraint1 = new double[getDim()];
                    var constraint2 = new double[getDim()];
                    for (var col = 0; col < criteria.getValues().size(); ++col) {
                        constraint1[2 * (rNum * dataContext.getM() + col) + count] = 1.0;
                        constraint1[2 * (rNum * dataContext.getM() + col) + count + 1] = -1.0;
                        constraint2[2 * rNum * dataContext.getM() + count + col] = 1.0;
                    }
                    count += 2 * criteria.getValues().size();
                    solver.addConstraint(constraint1, LpSolve.EQ, 0.0);
                    solver.addConstraint(constraint2, LpSolve.LE, 1.0);
                }
            }
        }

        private void addConstraints9(LpSolve solver) throws LpSolveException {
            for (var rNum = 0; rNum < getT(); ++rNum) {
                var constraint = new double[getDim()];
                for (var col = rNum * dataContext.getM(); col < (rNum + 1) * dataContext.getM(); ++col) {
                    constraint[2 * col] = 1.0;
                }
                solver.addConstraint(constraint, LpSolve.LE, k);
            }
        }

        private LpSolve createSolver() throws LpSolveException {
            var solver = LpSolve.makeLp(0, getDim());
            solver.setOutputfile("./result.txt");
            addGoalFunction(solver);
            addConstraints4(solver);
            addConstraints5(solver);
            addConstraints6(solver);
            addConstraints7(solver);
            addConstraints8(solver);
            addConstraints9(solver);
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
                    var alt1 = dataContext.createAltByDelta(delta1);
                    var alt2 = dataContext.createAltByDelta(delta2);
                    result.add(new AlternativePair(alt1, alt2));
                }
            }
            return result;
        }

        public List<AlternativePair> getResultVector() throws LpSolveException {
            var solver = createSolver();
            solver.solve();
            return extractAlternatives(solver.getPtrVariables());
        }
    }

    public List<AlternativePair> calculateCompareAlts(int k) throws LpSolveException {
        var optV = calculateForecastFunction();
        var altPair = calculateOptimalAlternatives(optV);
        if (altPair == null) {
            return Collections.emptyList();
        }
        var altCalculator = new AlternativesCalculation(
            k, optV, calculateDelta(altPair.getFirst(), altPair.getSecond()));
        var result = altCalculator.getResultVector();
        if (result.size() > 0) {
            return result;
        }
        return getDefaultPairs(optV, k);
    }

    public AlternativeEntity getOptimal() throws LpSolveException {
        var optV = calculateForecastFunction();
        var alts = getOptimalAlts();
        var maxAlt = findMaxAlt(alts, optV);
        if (maxAlt != null) {
            var equalAAlts = getEqualAlts(maxAlt);
            alts.removeAll(equalAAlts);
        }
        if (alts.size() == 1) {
           return alts.get(0);
        }
        return null;
    }
}
