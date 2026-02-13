package ru.tigerbank.service.importexport.model;

import ru.tigerbank.domain.BankAccount;
import ru.tigerbank.domain.Category;
import ru.tigerbank.domain.Operation;

import java.util.List;

public class DataSnapshot {
    private final List<BankAccount> accounts;
    private final List<Category> categories;
    private final List<Operation> operations;

    public DataSnapshot(
            List<BankAccount> accounts,
            List<Category> categories,
            List<Operation> operations) {
        this.accounts = accounts;
        this.categories = categories;
        this.operations = operations;
    }

    public List<BankAccount> getAccounts() { return accounts; }
    public List<Category> getCategories() { return categories; }
    public List<Operation> getOperations() { return operations; }
}
