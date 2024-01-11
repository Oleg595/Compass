package com.example.compas.service;

import com.example.compas.context.DataContext;
import com.example.compas.entity.AlternativeEntity;
import com.example.compas.entity.CriteriaEntity;
import com.example.compas.entity.AlternativePair;
import lombok.AllArgsConstructor;
import org.ejml.data.DMatrixRMaj;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

@Service
@AllArgsConstructor
public class CompareValueService {
    private static final String CRITERIA_QUESTION = "Выберите лучшее значение критерия %s: 1 - %s; 2 - %s; 3 - " +
        "эквивалентны";
    private static final String ALTERNATIVE_QUESTION = "Выберите лучшее значение альтернативы (%s): 1 - %s; 2 - %s; 3" +
        " - " +
        "эквивалентны";

    private final DataContext dataContext;

    private void processAnswer(DMatrixRMaj matrix, int x, int y, int ans, CriteriaEntity criteria, String valueX,
                               String valueY) {
        switch (ans) {
            case 1:
                matrix.set(x, y, 1.);
                matrix.set(y, x, -1.);
                dataContext.addP(List.of(criteria), List.of(valueX), List.of(valueY));
                return;
            case 2:
                matrix.set(y, x, 1.);
                matrix.set(x, y, -1.);
                dataContext.addP(List.of(criteria), List.of(valueY), List.of(valueX));
                return;
            case 3:
                matrix.set(x, y, .5);
                matrix.set(y, x, .5);
                dataContext.addI(List.of(criteria), List.of(valueX), List.of(valueY));
                return;
            default:
                throw new IllegalArgumentException("Указан некорректный ответ");
        }
    }

    private void checkInputData(DMatrixRMaj matrix, Set<Integer> prev, int row) {
        for (var col = 0; col < matrix.numCols; ++col) {
            if (matrix.get(row, col) == 1.) {
                if (prev.contains(col)) {
                    throw new IllegalStateException("Выявлено противоречие");
                }
                prev.add(row);
                checkInputData(matrix, prev, col);
            } else if (matrix.get(row, col) == .5) {
                if (!prev.contains(col)) {
                    prev.add(row);
                    checkInputData(matrix, prev, col);
                }
            }
        }
        prev.remove(row);
    }

    private void processAnswers(CriteriaEntity criteria) {
        var reader = new Scanner(System.in);
        var values = criteria.getValues();
        var size = values.size();
        var matrix = new DMatrixRMaj(size, size);
        matrix.zero();
        for (var x = 0; x < size; x++) {
            for (var y = x + 1; y < size; y++) {
                var valueX = values.get(x);
                var valueY = values.get(y);
                System.out.printf((CRITERIA_QUESTION) + "%n", criteria.getName(), valueX, valueY);
                processAnswer(matrix, x, y, reader.nextInt(), criteria, valueX, valueY);
                checkInputData(matrix, new HashSet<>(Set.of(x)), x);
                checkInputData(matrix, new HashSet<>(Set.of(y)), y);
            }
        }
    }

    private void processAnswer(int answer, AlternativePair pair) {
        switch (answer) {
            case 1:
                dataContext.getP().add(new AlternativePair(pair.getFirst(), pair.getSecond()));
                break;
            case 2:
                dataContext.getP().add(new AlternativePair(pair.getSecond(), pair.getFirst()));
                break;
            case 3:
                dataContext.getI().add(pair);
                break;
            default:
                throw new IllegalArgumentException("Указан некорректный ответ");
        }
    }

    private String getAlternative(AlternativeEntity alt) {
        String result = null;
        for (var criteria: dataContext.getCriterias()) {
            var value = alt.getValueByCriteria(criteria);
            if (value != null) {
                if (result == null) {
                    result = value;
                } else {
                    result = result.concat(", " + value);
                }
            }
        }
        return result;
    }

    private String getCriterias(AlternativeEntity alt) {
        String result = null;
        for (var criteria: dataContext.getCriterias()) {
            var value = alt.getValueByCriteria(criteria);
            if (value != null) {
                if (result == null) {
                    result = criteria.getName();
                } else {
                    result = result.concat(", " + criteria.getName());
                }
            }
        }
        return result;
    }

    public void processAlternatives(List<AlternativePair> alts) {
        var reader = new Scanner(System.in);
        for (var pair: alts) {
            System.out.printf(
                (ALTERNATIVE_QUESTION) + "%n", getCriterias(pair.getFirst()),
                getAlternative(pair.getFirst()), getAlternative(pair.getSecond()));
            processAnswer(reader.nextInt(), pair);
        }
    }

    public void processCriterias() {
        for (var criteria : dataContext.getCriterias()) {
            processAnswers(criteria);
        }
    }
}
