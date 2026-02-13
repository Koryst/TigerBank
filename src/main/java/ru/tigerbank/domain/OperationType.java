package ru.tigerbank.domain;

public enum OperationType {
    INCOME,
    EXPENSE;

    @Override
    public String toString() {
        return switch(this) {
            case INCOME -> "INCOME";
            case EXPENSE -> "EXPENSE";
        };
    }
}
