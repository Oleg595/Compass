package com.example.algorithm.common;

import com.example.algorithm.context.DataContext;
import com.example.algorithm.entity.Rule;

import java.util.List;

public interface FindConflictService {
    List<Rule> findConflictChainOrNull(DataContext dataContext);
}
