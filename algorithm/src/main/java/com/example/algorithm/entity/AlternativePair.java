package com.example.algorithm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlternativePair {
    private AlternativeEntity first;
    private AlternativeEntity second;
}
