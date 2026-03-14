package ru.tigerbank.factory;

import ru.tigerbank.domain.BankAccount;
import ru.tigerbank.domain.Category;
import ru.tigerbank.domain.Operation;
import ru.tigerbank.domain.OperationType;

import java.math.BigDecimal;

public abstract class EntityFactory {
    public abstract BankAccount createBankAccount(String name);
    public abstract Category createCategory(String name, OperationType type);
    public abstract Operation createOperation(Integer accountId, Integer categoryId, double amount,
                                              OperationType type, String description);
}
