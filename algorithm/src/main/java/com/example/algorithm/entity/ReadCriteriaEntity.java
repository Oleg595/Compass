package com.example.algorithm.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReadCriteriaEntity {
    private final String name;
    private final List<Map<String, Object>> values;
}
