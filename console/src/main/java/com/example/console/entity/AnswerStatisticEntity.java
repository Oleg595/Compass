package com.example.console.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AnswerStatisticEntity {
    private int numQuestions = 0;
    private int numErrors = 0;

    public void addQuest() {
        numQuestions++;
    }

    public void addError() {
        numErrors++;
    }
}
