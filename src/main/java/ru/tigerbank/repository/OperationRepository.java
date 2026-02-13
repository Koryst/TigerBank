package ru.tigerbank.repository;

import ru.tigerbank.domain.Operation;
import ru.tigerbank.domain.OperationType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OperationRepository {
    Operation save(Operation operation);
    Optional<Operation> findById(Integer id);
    List<Operation> findAll();
    List<Operation> findByBankAccountId(Integer bankAccountId);
    List<Operation> findByCategoryId(Integer categoryId);
    List<Operation> findByDateBetween(LocalDate from, LocalDate to);
    List<Operation> findByType(OperationType type);
    boolean existsByBankAccountId(Integer bankAccountId);
    boolean existsByCategoryId(Integer categoryId);
    void deleteById(Integer id);
    void deleteByBankAccountId(Integer bankAccountId);
}
