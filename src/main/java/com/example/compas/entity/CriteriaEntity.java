package com.example.compas.entity;

import lombok.Data;

import java.util.List;

@Data
public class CriteriaEntity {
    private final String name;
    private final List<String> values;
}
