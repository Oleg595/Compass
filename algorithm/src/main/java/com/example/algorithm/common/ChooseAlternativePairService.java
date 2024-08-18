package com.example.algorithm.common;

import com.example.algorithm.function.ForecastFunction;
import org.example.AlternativePair;
import org.example.DataContext;

public interface ChooseAlternativePairService {
    // Возвращает пару несравнимых альтернатив, если такой найти не удалось,
    // в качестве ответа возвращается пара с 2-мя одинаковыми альтернативами
    AlternativePair findPair(ForecastFunction fFunction, DataContext dataContext);
}
