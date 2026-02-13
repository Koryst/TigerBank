package ru.tigerbank.service.analytics;

import org.springframework.stereotype.Service;
import ru.tigerbank.domain.Operation;
import ru.tigerbank.domain.OperationType;
import ru.tigerbank.repository.CategoryRepository;
import ru.tigerbank.repository.OperationRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final OperationRepository operationRepository;
    private final CategoryRepository categoryRepository;

    public AnalyticsServiceImpl(
            OperationRepository operationRepository,
            CategoryRepository categoryRepository) {
        this.operationRepository = operationRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public double getNetBalance(LocalDate from, LocalDate to) {
        return getTotalIncome(from, to) - getTotalExpense(from, to);
    }

    @Override
    public double getNetBalanceByAccount(Integer accountId, LocalDate from, LocalDate to) {
        List<Operation> operations = operationRepository.findByBankAccountId(accountId).stream()
                .filter(op -> !op.getDate().isBefore(from) && !op.getDate().isAfter(to))
                .collect(Collectors.toList());

        double income = operations.stream()
                .filter(op -> op.getType() == OperationType.INCOME)
                .mapToDouble(Operation::getAmount)
                .sum();

        double expense = operations.stream()
                .filter(op -> op.getType() == OperationType.EXPENSE)
                .mapToDouble(Operation::getAmount)
                .sum();

        return income - expense;
    }

    @Override
    public Map<String, Double> getIncomeByCategory(LocalDate from, LocalDate to) {
        return getSumByCategory(OperationType.INCOME, from, to, null);
    }

    @Override
    public Map<String, Double> getExpenseByCategory(LocalDate from, LocalDate to) {
        return getSumByCategory(OperationType.EXPENSE, from, to, null);
    }

    @Override
    public Map<String, Double> getIncomeByCategoryAndAccount(Integer accountId, LocalDate from, LocalDate to) {
        return getSumByCategory(OperationType.INCOME, from, to, accountId);
    }

    @Override
    public Map<String, Double> getExpenseByCategoryAndAccount(Integer accountId, LocalDate from, LocalDate to) {
        return getSumByCategory(OperationType.EXPENSE, from, to, accountId);
    }

    @Override
    public double getTotalIncome(LocalDate from, LocalDate to) {
        return operationRepository.findByDateBetween(from, to).stream()
                .filter(op -> op.getType() == OperationType.INCOME)
                .mapToDouble(Operation::getAmount)
                .sum();
    }

    @Override
    public double getTotalExpense(LocalDate from, LocalDate to) {
        return operationRepository.findByDateBetween(from, to).stream()
                .filter(op -> op.getType() == OperationType.EXPENSE)
                .mapToDouble(Operation::getAmount)
                .sum();
    }

    private Map<String, Double> getSumByCategory(
            OperationType type,
            LocalDate from,
            LocalDate to,
            Integer accountId) {

        // Получаем все операции за период
        List<Operation> operations = operationRepository.findByDateBetween(from, to).stream()
                .filter(op -> op.getType() == type)
                .filter(op -> accountId == null || op.getBankAccountId().equals(accountId))
                .collect(Collectors.toList());

        // Группируем по категориям
        Map<Integer, Double> sumByCategoryId = operations.stream()
                .collect(Collectors.groupingBy(
                        Operation::getCategoryId,
                        Collectors.summingDouble(Operation::getAmount)
                ));

        // Преобразуем ID категорий в названия
        Map<String, Double> result = new HashMap<>();
        for (Map.Entry<Integer, Double> entry : sumByCategoryId.entrySet()) {
            categoryRepository.findById(entry.getKey())
                    .ifPresent(category ->
                            result.put(category.getName(), entry.getValue())
                    );
        }

        return result;
    }
}