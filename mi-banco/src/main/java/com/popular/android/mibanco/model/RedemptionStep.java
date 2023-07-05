package com.popular.android.mibanco.model;

public enum RedemptionStep {
    notStarted(-1), redemptionConfiguration(0), redemptionConfirmation(1), redemptionResult(2);

    private final int value;

    RedemptionStep(int value) {
        this.value = value;
    }

    int getValue() {
        return value;
    }
}
