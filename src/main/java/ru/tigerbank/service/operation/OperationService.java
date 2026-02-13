package ru.tigerbank.service.operation;

import ru.tigerbank.domain.Operation;
import ru.tigerbank.domain.OperationType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OperationService {

    // Создание операции
    Operation createOperation(
            OperationType type,
            Integer bankAccountId,
            Integer categoryId,
            double amount,
            LocalDate date,
            String description
    );

    // Создание операции с заданным ID (для импорта)
    Operation createOperation(
            Integer id,
            OperationType type,
            Integer bankAccountId,
            Integer categoryId,
            double amount,
            LocalDate date,
            String description
    );

    // Чтение
    Optional<Operation> getOperation(Integer id);
    List<Operation> getAllOperations();
    List<Operation> getOperationsByAccount(Integer bankAccountId);
    List<Operation> getOperationsByCategory(Integer categoryId);
    List<Operation> getOperationsByDateRange(LocalDate from, LocalDate to);
    List<Operation> getOperationsByType(OperationType type);

    // Обновление
    Operation updateDescription(Integer id, String newDescription);
    Operation changeCategory(Integer id, Integer newCategoryId);

    // Удаление
    void deleteOperation(Integer id);

    // Аналитика (базовая, остальное будет в AnalyticsService)
    double getTotalIncomeByAccount(Integer accountId, LocalDate from, LocalDate to);
    double getTotalExpenseByAccount(Integer accountId, LocalDate from, LocalDate to);
}