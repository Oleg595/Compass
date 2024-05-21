package com.example.algorithm.common;

import org.example.DataContext;
import org.example.RuleEntity;

import java.util.List;

public interface FindConflictService {
    List<RuleEntity> findConflictChainOrNull(DataContext dataContext);
}
