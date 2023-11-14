package com.example.compas.util;

import com.example.compas.entity.AlternativeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlternativePair {
    private AlternativeEntity first;
    private AlternativeEntity second;
}
