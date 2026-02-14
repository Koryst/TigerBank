package ru.tigerbank.domain;

import java.time.LocalDate;

public class Operation {
    private Integer id;
    private OperationType type;
    private Integer bankAccountId;
    private Integer categoryId;
    private double amount;
    private LocalDate date;
    private String description;

    public Operation() { }

    public Operation(Integer id, OperationType type, Integer bankAccountId,
                     Integer categoryId, double amount, LocalDate date,
                     String description) {

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        this.id = id;
        this.type = type;
        this.bankAccountId = bankAccountId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    public Integer getId() { return id; }
    public OperationType getType() { return type; }
    public Integer getBankAccountId() { return bankAccountId; }
    public Integer getCategoryId() { return categoryId; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
}
