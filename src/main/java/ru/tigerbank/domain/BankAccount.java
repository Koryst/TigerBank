package ru.tigerbank.domain;

public class BankAccount {
    private Integer id;
    private String name;
    private double balance;

    public BankAccount(Integer id, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Account name cannot be empty");
        }
        this.id = id;
        this.name = name;
        this.balance = 0.0;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public double getBalance() { return balance; }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
