package ru.tigerbank.service.operation;

import org.springframework.stereotype.Service;
import ru.tigerbank.domain.Category;
import ru.tigerbank.domain.Operation;
import ru.tigerbank.domain.OperationType;
import ru.tigerbank.repository.BankAccountRepository;
import ru.tigerbank.repository.CategoryRepository;
import ru.tigerbank.repository.OperationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CategoryRepository categoryRepository;

    public OperationServiceImpl(
            OperationRepository operationRepository,
            BankAccountRepository bankAccountRepository,
            CategoryRepository categoryRepository) {
        this.operationRepository = operationRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Operation createOperation(
            OperationType type,
            Integer bankAccountId,
            Integer categoryId,
            double amount,
            LocalDate date,
            String description) {

        // Проверяем существование счета
        if (!bankAccountRepository.existsById(bankAccountId)) {
            throw new IllegalArgumentException("Bank account not found with id: " + bankAccountId);
        }

        // Проверяем существование категории
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("Category not found with id: " + categoryId);
        }

        Operation operation = new Operation(
                null, type, bankAccountId, categoryId, amount, date, description
        );

        return operationRepository.save(operation);
    }

    @Override
    public Operation createOperation(
            Integer id,
            OperationType type,
            Integer bankAccountId,
            Integer categoryId,
            double amount,
            LocalDate date,
            String description) {

        // Проверяем существование счета и категории
        if (!bankAccountRepository.existsById(bankAccountId)) {
            throw new IllegalArgumentException("Bank account not found with id: " + bankAccountId);
        }
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("Category not found with id: " + categoryId);
        }

        Operation operation = new Operation(
                id, type, bankAccountId, categoryId, amount, date, description
        );

        return operationRepository.save(operation);
    }

    @Override
    public Optional<Operation> getOperation(Integer id) {
        return operationRepository.findById(id);
    }

    @Override
    public List<Operation> getAllOperations() {
        return operationRepository.findAll();
    }

    @Override
    public List<Operation> getOperationsByAccount(Integer bankAccountId) {
        if (!bankAccountRepository.existsById(bankAccountId)) {
            throw new IllegalArgumentException("Bank account not found with id: " + bankAccountId);
        }
        return operationRepository.findByBankAccountId(bankAccountId);
    }

    @Override
    public List<Operation> getOperationsByCategory(Integer categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("Category not found with id: " + categoryId);
        }
        return operationRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Operation> getOperationsByDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        return operationRepository.findByDateBetween(from, to);
    }

    @Override
    public List<Operation> getOperationsByType(OperationType type) {
        return operationRepository.findByType(type);
    }

    @Override
    public Operation updateDescription(Integer id, String newDescription) {
        Operation operation = operationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Operation not found with id: " + id));

        Operation updated = new Operation(
                operation.getId(),
                operation.getType(),
                operation.getBankAccountId(),
                operation.getCategoryId(),
                operation.getAmount(),
                operation.getDate(),
                newDescription
        );

        return operationRepository.save(updated);
    }

    @Override
    public Operation changeCategory(Integer id, Integer newCategoryId) {
        Operation operation = operationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Operation not found with id: " + id));

        if (!categoryRepository.existsById(newCategoryId)) {
            throw new IllegalArgumentException("Category not found with id: " + newCategoryId);
        }

        Category newCategory = categoryRepository.findById(newCategoryId).get();

        // Проверяем, что тип категории совпадает с типом операции
        if (operation.getType() != newCategory.getType()) {
            throw new IllegalArgumentException(
                    "Cannot change category: operation type " + operation.getType() +
                            " does not match category type " + newCategory.getType()
            );
        }

        Operation updated = new Operation(
                operation.getId(),
                operation.getType(),
                operation.getBankAccountId(),
                newCategoryId,
                operation.getAmount(),
                operation.getDate(),
                operation.getDescription()
        );

        return operationRepository.save(updated);
    }

    @Override
    public void deleteOperation(Integer id) {
        Operation operation = operationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Operation not found with id: " + id));
        operationRepository.deleteById(operation.getId());
    }

    @Override
    public double getTotalIncomeByAccount(Integer accountId, LocalDate from, LocalDate to) {
        return operationRepository.findByBankAccountId(accountId).stream()
                .filter(op -> op.getType() == OperationType.INCOME)
                .filter(op -> !op.getDate().isBefore(from) && !op.getDate().isAfter(to))
                .mapToDouble(Operation::getAmount)
                .sum();
    }

    @Override
    public double getTotalExpenseByAccount(Integer accountId, LocalDate from, LocalDate to) {
        return operationRepository.findByBankAccountId(accountId).stream()
                .filter(op -> op.getType() == OperationType.EXPENSE)
                .filter(op -> !op.getDate().isBefore(from) && !op.getDate().isAfter(to))
                .mapToDouble(Operation::getAmount)
                .sum();
    }
}