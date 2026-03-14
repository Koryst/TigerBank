package ru.tigerbank.factory;

import org.springframework.stereotype.Component;
import ru.tigerbank.domain.BankAccount;
import ru.tigerbank.domain.Category;
import ru.tigerbank.domain.Operation;
import ru.tigerbank.domain.OperationType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class DefaultEntityFactory extends EntityFactory {
    private final AtomicInteger accountIdGenerator = new AtomicInteger(1);
    private final AtomicInteger categoryIdGenerator = new AtomicInteger(1);
    private final AtomicInteger operationIdGenerator = new AtomicInteger(1);

    @Override
    public BankAccount createBankAccount(String name) {
        return new BankAccount(accountIdGenerator.getAndIncrement(), name);
    }

    @Override
    public Category createCategory(String name, OperationType type) {
        return new Category(categoryIdGenerator.getAndIncrement(), type, name);
    }

    @Override
    public Operation createOperation(Integer accountId, Integer categoryId, double amount, OperationType type, String description) {
        return new Operation(operationIdGenerator.getAndIncrement(), type, accountId, categoryId, amount, LocalDate.now(), description);
    }
}
