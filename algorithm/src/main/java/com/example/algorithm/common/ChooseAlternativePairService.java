package com.example.algorithm.common;

import com.example.algorithm.context.DataContext;
import com.example.algorithm.entity.AlternativePair;
import com.example.algorithm.entity.ForecastFunctionEntity;

public interface ChooseAlternativePairService {
    // Возвращает пару несравнимых альтернатив, если такой найти не удалось,
    // в качестве ответа возвращается пара с 2-мя одинаковыми альтернативами
    AlternativePair findPair(ForecastFunctionEntity fFunction, DataContext dataContext);
}
