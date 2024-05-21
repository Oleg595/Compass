package com.example.algorithm.common;

import com.example.algorithm.entity.ForecastFunctionEntity;
import org.example.AlternativePair;
import org.example.DataContext;

public interface ChooseAlternativePairService {
    // Возвращает пару несравнимых альтернатив, если такой найти не удалось,
    // в качестве ответа возвращается пара с 2-мя одинаковыми альтернативами
    AlternativePair findPair(ForecastFunctionEntity fFunction, DataContext dataContext);
}
