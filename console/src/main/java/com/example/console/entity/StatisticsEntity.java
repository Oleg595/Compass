package com.example.console.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticsEntity {
    private int numQuests;
    private long time;
}
