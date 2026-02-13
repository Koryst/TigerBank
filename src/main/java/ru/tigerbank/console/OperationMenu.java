package ru.tigerbank.console;

import org.springframework.stereotype.Component;
import ru.tigerbank.console.utils.ConsoleReader;
import ru.tigerbank.console.utils.TablePrinter;
import ru.tigerbank.domain.BankAccount;
import ru.tigerbank.domain.Category;
import ru.tigerbank.domain.Operation;
import ru.tigerbank.domain.OperationType;
import ru.tigerbank.service.account.BankAccountService;
import ru.tigerbank.service.category.CategoryService;
import ru.tigerbank.service.operation.OperationService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class OperationMenu {

    private final OperationService operationService;
    private final BankAccountService accountService;
    private final CategoryService categoryService;
    private final ConsoleReader reader;
    private final TablePrinter tablePrinter;

    public OperationMenu(
            OperationService operationService,
            BankAccountService accountService,
            CategoryService categoryService,
            ConsoleReader reader,
            TablePrinter tablePrinter) {
        this.operationService = operationService;
        this.accountService = accountService;
        this.categoryService = categoryService;
        this.reader = reader;
        this.tablePrinter = tablePrinter;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            printMenu();
            String choice = reader.readString("Выберите пункт");

            switch (choice) {
                case "1" -> addIncome();
                case "2" -> addExpense();
                case "3" -> listAllOperations();
                case "4" -> listOperationsByAccount();
                case "5" -> listOperationsByCategory();
                case "6" -> listOperationsByDateRange();
                case "7" -> updateDescription();
                case "8" -> changeCategory();
                case "9" -> deleteOperation();
                case "0" -> back = true;
                default -> System.out.println("❌ Неверный выбор.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- УПРАВЛЕНИЕ ОПЕРАЦИЯМИ ---");
        System.out.println("1. Добавить доход");
        System.out.println("2. Добавить расход");
        System.out.println("3. Список всех операций");
        System.out.println("4. Операции по счету");
        System.out.println("5. Операции по категории");
        System.out.println("6. Операции за период");
        System.out.println("7. Изменить описание");
        System.out.println("8. Изменить категорию");
        System.out.println("9. Удалить операцию");
        System.out.println("0. Назад");
    }

    private void addIncome() {
        addOperation(OperationType.INCOME);
    }

    private void addExpense() {
        addOperation(OperationType.EXPENSE);
    }

    private void addOperation(OperationType type) {
        // Проверяем наличие счетов
        List<BankAccount> accounts = accountService.getAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("❌ Сначала создайте хотя бы один счет");
            return;
        }

        // Проверяем наличие категорий нужного типа
        List<Category> categories = categoryService.getCategoriesByType(type);
        if (categories.isEmpty()) {
            System.out.println("❌ Сначала создайте хотя бы одну категорию " +
                    (type == OperationType.INCOME ? "доходов" : "расходов"));
            return;
        }

        // Показываем доступные счета
        System.out.println("\nДоступные счета:");
        tablePrinter.printAccounts(accounts);

        Integer accountId = reader.readInt("Введите ID счета");
        if (accountId == null) return;

        // Проверяем существование счета
        Optional<BankAccount> account = accountService.getAccount(accountId);
        if (account.isEmpty()) {
            System.out.println("❌ Счет не найден");
            return;
        }

        // Показываем доступные категории
        System.out.println("\nДоступные категории " +
                (type == OperationType.INCOME ? "доходов:" : "расходов:"));
        tablePrinter.printCategories(categories);

        Integer categoryId = reader.readInt("Введите ID категории");
        if (categoryId == null) return;

        // Проверяем существование категории и ее тип
        Optional<Category> category = categoryService.getCategory(categoryId);
        if (category.isEmpty()) {
            System.out.println("❌ Категория не найдена");
            return;
        }
        if (category.get().getType() != type) {
            System.out.println("❌ Выбранная категория не подходит для " +
                    (type == OperationType.INCOME ? "дохода" : "расхода"));
            return;
        }

        Double amount = reader.readDouble("Введите сумму");
        if (amount == null || amount <= 0) {
            System.out.println("❌ Сумма должна быть положительным числом");
            return;
        }

        LocalDate date = reader.readDate("Введите дату");
        if (date == null) {
            date = LocalDate.now(); // Если дата не введена, используем сегодня
            System.out.println("Используется текущая дата: " + date);
        }

        String description = reader.readString("Введите описание (необязательно)");
        if (description.isEmpty()) {
            description = null;
        }

        try {
            Operation operation = operationService.createOperation(
                    type, accountId, categoryId, amount, date, description
            );
            System.out.println("✅ Операция добавлена! ID: " + operation.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void listAllOperations() {
        tablePrinter.printOperations(operationService.getAllOperations());
    }

    private void listOperationsByAccount() {
        Integer accountId = reader.readInt("Введите ID счета");
        if (accountId == null) return;

        try {
            List<Operation> operations = operationService.getOperationsByAccount(accountId);
            tablePrinter.printOperations(operations);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void listOperationsByCategory() {
        Integer categoryId = reader.readInt("Введите ID категории");
        if (categoryId == null) return;

        try {
            List<Operation> operations = operationService.getOperationsByCategory(categoryId);
            tablePrinter.printOperations(operations);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void listOperationsByDateRange() {
        LocalDate from = reader.readDate("Введите начальную дату");
        if (from == null) return;

        LocalDate to = reader.readDate("Введите конечную дату");
        if (to == null) return;

        try {
            List<Operation> operations = operationService.getOperationsByDateRange(from, to);
            tablePrinter.printOperations(operations);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void updateDescription() {
        Integer id = reader.readInt("Введите ID операции");
        if (id == null) return;

        Optional<Operation> operation = operationService.getOperation(id);
        if (operation.isEmpty()) {
            System.out.println("❌ Операция не найдена");
            return;
        }

        String newDescription = reader.readString("Введите новое описание");

        try {
            Operation updated = operationService.updateDescription(id, newDescription);
            System.out.println("✅ Описание обновлено");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void changeCategory() {
        Integer id = reader.readInt("Введите ID операции");
        if (id == null) return;

        Optional<Operation> operation = operationService.getOperation(id);
        if (operation.isEmpty()) {
            System.out.println("❌ Операция не найдена");
            return;
        }

        // Показываем доступные категории подходящего типа
        OperationType type = operation.get().getType();
        List<Category> categories = categoryService.getCategoriesByType(type);

        System.out.println("\nДоступные категории:");
        tablePrinter.printCategories(categories);

        Integer newCategoryId = reader.readInt("Введите ID новой категории");
        if (newCategoryId == null) return;

        try {
            Operation updated = operationService.changeCategory(id, newCategoryId);
            System.out.println("✅ Категория изменена");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void deleteOperation() {
        Integer id = reader.readInt("Введите ID операции для удаления");
        if (id == null) return;

        Optional<Operation> operation = operationService.getOperation(id);
        if (operation.isEmpty()) {
            System.out.println("❌ Операция не найдена");
            return;
        }

        if (reader.confirm("Вы уверены?")) {
            try {
                operationService.deleteOperation(id);
                System.out.println("✅ Операция удалена");
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            }
        } else {
            System.out.println("Операция отменена");
        }
    }
}