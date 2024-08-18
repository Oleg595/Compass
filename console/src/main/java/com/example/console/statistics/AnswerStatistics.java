package com.example.console.statistics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AnswerStatistics {
    private int numQuestions = 0;
    private int numErrors = 0;

    public void addQuest() {
        numQuestions++;
    }

    public void addError() {
        numErrors++;
    }
}
