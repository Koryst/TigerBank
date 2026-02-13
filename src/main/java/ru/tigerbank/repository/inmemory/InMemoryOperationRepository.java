package ru.tigerbank.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.tigerbank.domain.Operation;
import ru.tigerbank.domain.OperationType;
import ru.tigerbank.repository.OperationRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryOperationRepository implements OperationRepository {

    private final Map<Integer, Operation> storage = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public Operation save(Operation operation) {
        if (operation.getId() == null) {
            Operation newOperation = new Operation(
                    idGenerator.getAndIncrement(),
                    operation.getType(),
                    operation.getBankAccountId(),
                    operation.getCategoryId(),
                    operation.getAmount(),
                    operation.getDate(),
                    operation.getDescription()
            );
            storage.put(newOperation.getId(), newOperation);
            return newOperation;
        } else {
            storage.put(operation.getId(), operation);
            if (operation.getId() >= idGenerator.get()) {
                idGenerator.set(operation.getId() + 1);
            }
            return operation;
        }
    }

    @Override
    public Optional<Operation> findById(Integer id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Operation> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Operation> findByBankAccountId(Integer bankAccountId) {
        return storage.values().stream()
                .filter(op -> op.getBankAccountId().equals(bankAccountId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Operation> findByCategoryId(Integer categoryId) {
        return storage.values().stream()
                .filter(op -> op.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Operation> findByDateBetween(LocalDate from, LocalDate to) {
        return storage.values().stream()
                .filter(op -> !op.getDate().isBefore(from) && !op.getDate().isAfter(to))
                .collect(Collectors.toList());
    }

    @Override
    public List<Operation> findByType(OperationType type) {
        return storage.values().stream()
                .filter(op -> op.getType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByBankAccountId(Integer bankAccountId) {
        return storage.values().stream()
                .anyMatch(op -> op.getBankAccountId().equals(bankAccountId));
    }

    @Override
    public boolean existsByCategoryId(Integer categoryId) {
        return storage.values().stream()
                .anyMatch(op -> op.getCategoryId().equals(categoryId));
    }

    @Override
    public void deleteById(Integer id) {
        storage.remove(id);
    }

    @Override
    public void deleteByBankAccountId(Integer bankAccountId) {
        storage.entrySet().removeIf(entry ->
                entry.getValue().getBankAccountId().equals(bankAccountId)
        );
    }
}
