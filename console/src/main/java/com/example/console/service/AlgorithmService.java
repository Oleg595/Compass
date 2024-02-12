package com.example.console.service;

import com.example.algorithm.AlgorithmAPI;
import com.example.algorithm.context.DataContext;
import com.example.algorithm.entity.AlternativeEntity;
import com.example.algorithm.entity.ForecastFunctionEntity;
import com.example.algorithm.utils.AlternativeUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// Необходимо перенести в модуль algorithm
@Component
@AllArgsConstructor
public class AlgorithmService {
    private final DataContext dataContext;
    private final AlgorithmAPI algorithmAPI;

//    private AlternativeEntity findMaxAlt(
//        List<AlternativeEntity> alts, ForecastFunctionEntity forecastFunction) {
//        var maxVal = Double.MIN_VALUE;
//        AlternativeEntity result = null;
//        for (var alt : alts) {
//            var delta = AlternativeUtils.calculateDelta(alt, dataContext.getCriterias());
//            var altValue = forecastFunction.apply(delta);
//            if (altValue > maxVal) {
//                result = alt;
//                maxVal = altValue;
//            }
//        }
//        return result;
//    }

//    private class ModusPonensCalculation {
//        private void addGoalFunction(LpSolve solver) throws LpSolveException {
//            var dim = dataContext.getP().size() + dataContext.getI().size();
//            var goalFunction = new double[dim];
//            for (var index = 0; index < dataContext.getP().size(); ++index) {
//                goalFunction[index] = 1.0;
//            }
//            solver.setObjFn(goalFunction);
//        }
//
//        private void addEqualConstraints(LpSolve solver, List<Double> delta) throws LpSolveException {
//            var dim = dataContext.getP().size() + dataContext.getI().size();
//            var D = dataContext.calculateDMatrix();
//            var E = dataContext.calculateEMatrix();
//            for (var row = 0; row < dataContext.getM(); ++row) {
//                var constraint = new double[dim];
//                for (var dCol = 0; dCol < dataContext.getP().size(); ++dCol) {
//                    constraint[dCol] = D.get(row, dCol);
//                }
//                for (var eCol = 0; eCol < dataContext.getI().size(); ++eCol) {
//                    constraint[D.numCols + eCol] = E.get(row, eCol);
//                }
//                solver.addConstraint(constraint, LpSolve.EQ, delta.get(row));
//            }
//        }
//
//        private LpSolve createSolver(List<Double> delta) throws LpSolveException {
//            var dim = dataContext.getP().size() + dataContext.getI().size();
//            var solver = LpSolve.makeLp(0, dim);
//            solver.setOutputfile("./result.txt");
//            solver.setMaxim();
//            addGoalFunction(solver);
//            addEqualConstraints(solver, delta);
//            return solver;
//        }
//
//        public List<Double> getResultVector(AlternativeEntity a, AlternativeEntity b) throws LpSolveException {
//            var delta = alternativeService.calculateDelta(a, b);
//            var solver = createSolver(delta);
//            solver.solve();
//            if (solver.getStatus() > 1) {
//                return new ArrayList<>(Collections.nCopies(dataContext.getP().size(), -1.));
//            }
//            var result = new ArrayList<Double>();
//            for (var index = 0; index < dataContext.getP().size(); ++index) {
//                result.add(solver.getPtrVariables()[index]);
//            }
//            return result;
//        }
//    }

//    private boolean altMorePrior(AlternativeEntity alt1, AlternativeEntity alt2) throws LpSolveException {
//        var criterias = dataContext.getCriterias();
//        var altsDelta = AlternativeUtils.calculateDelta(alt1, alt2, criterias);
//        for (var p : dataContext.getP()) {
//            var pDelta = AlternativeUtils.calculateDelta(p, criterias);
//            if (altsDelta.equals(pDelta)) {
//                return true;
//            }
//        }
//        var modusPonens = new ModusPonensCalculation();
//        return modusPonens.getResultVector(alt1, alt2).stream().anyMatch(it -> it > 0.0);
//    }

//    private List<AlternativeEntity> getOptimalAlts() throws LpSolveException {
//        var alts = new ArrayList<>(dataContext.getAlts());
//        var nonOptAlts = new ArrayList<AlternativeEntity>();
//        for (var index1 = 0; index1 < alts.size(); ++index1) {
//            for (var index2 = 0; index2 < alts.size(); ++index2) {
//                var alt1 = alts.get(index1);
//                var alt2 = alts.get(index2);
//                if (index1 != index2 && altMorePrior(alt1, alt2)) {
//                    nonOptAlts.add(alt2);
//                }
//            }
//        }
//        alts.removeAll(nonOptAlts);
//        return alts;
//    }

//    private boolean equalAlts(AlternativeEntity alt1, AlternativeEntity alt2) {
//        var criterias = dataContext.getCriterias();
//        var altsDelta = AlternativeUtils.calculateDelta(alt1, alt2, criterias);
//        for (var i : dataContext.getI()) {
//            var iDelta = AlternativeUtils.calculateDelta(i, criterias);
//            if (altsDelta == iDelta) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private List<AlternativeEntity> getEqualAlts(AlternativeEntity alt) {
//        var result = new ArrayList<AlternativeEntity>();
//        for (var a : dataContext.getAlts()) {
//            if (alt != a && equalAlts(alt, a)) {
//                result.add(a);
//            }
//        }
//        return result;
//    }

//    private AlternativePair calculateOptimalAlternatives(
//        ForecastFunctionEntity forecastFunction) throws LpSolveException {
//        var alts = getOptimalAlts();
//
//        var a = findMaxAlt(alts, forecastFunction);
//        alts.remove(a);
//        if (a == null) {
//            return null;
//        }
//
//        var equalAAlts = getEqualAlts(a);
//        alts.removeAll(equalAAlts);
//
//        if (alts.size() > 0) {
//            if (findMaxAlt(alts, forecastFunction) != null) {
//                return new AlternativePair(a, findMaxAlt(alts, forecastFunction));
//            }
//            return null;
//        } else {
//            System.out.println("Лучшая альтернатива " + a);
//            return null;
//        }
//    }

//    private AlternativePair pairWithoutSimilars(AlternativeEntity alt1, AlternativeEntity alt2) {
//        var criterias = dataContext.getCriterias();
//        var delta1 = AlternativeUtils.calculateDelta(alt1, criterias);
//        var delta2 = AlternativeUtils.calculateDelta(alt2, criterias);
//        for (var index = 0; index < dataContext.getM(); ++index) {
//            if (delta1.get(index) == 1 && delta2.get(index) == 1) {
//                delta1.set(index, 0.0);
//                delta2.set(index, 0.0);
//            }
//        }
//        return new AlternativePair(
//            alternativeService.createByDelta(delta1), alternativeService.createByDelta(delta2));
//    }
//
//    private List<AlternativePair> getDefaultPairs(
//        ForecastFunctionEntity forecastFunction) throws LpSolveException {
//        var alts = getOptimalAlts();
//        var a = findMaxAlt(alts, forecastFunction);
//        if (a != null) {
//            var equalAAlts = getEqualAlts(a);
//            alts.removeAll(equalAAlts);
//        }
//        var result = new ArrayList<AlternativePair>();
//        for (var index1 = 0; index1 < alts.size(); ++index1) {
//            for (var index2 = index1 + 1; index2 < alts.size(); ++index2) {
//                var alt1 = alts.get(index1);
//                var alt2 = alts.get(index2);
//                result.add(pairWithoutSimilars(alt1, alt2));
//            }
//        }
//        return result;
//    }

//    public List<AlternativePair> calculateCompareAlts(int k) throws LpSolveException {
//        var forecastFunction = algorithmAPI.calculateForecastFunction();
//        var altPair = calculateOptimalAlternatives(forecastFunction);
//        if (altPair == null) {
//            return Collections.emptyList();
//        }
//        var result = algorithmAPI.calculateCompareAlternatives(
//            k, forecastFunction.getV(), alternativeService.calculateDelta(altPair));
//        if (result.size() > 0) {
//            return result;
//        }
//        return getDefaultPairs(forecastFunction);
//    }

//    public AlternativeEntity getOptimal() throws LpSolveException {
//        var optV = algorithmAPI.calculateForecastFunction(dataContext);
//        var alts = getOptimalAlts();
//        var maxAlt = findMaxAlt(alts, optV);
//        if (maxAlt != null) {
//            var equalAAlts = getEqualAlts(maxAlt);
//            alts.removeAll(equalAAlts);
//        }
//        if (alts.size() == 1) {
//           return alts.get(0);
//        }
//        return null;
//    }
}
