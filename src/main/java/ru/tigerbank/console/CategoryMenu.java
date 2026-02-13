package ru.tigerbank.console;

import org.springframework.stereotype.Component;
import ru.tigerbank.console.utils.ConsoleReader;
import ru.tigerbank.console.utils.TablePrinter;
import ru.tigerbank.domain.Category;
import ru.tigerbank.domain.OperationType;
import ru.tigerbank.service.category.CategoryService;

import java.util.Optional;

@Component
public class CategoryMenu {

    private final CategoryService categoryService;
    private final ConsoleReader reader;
    private final TablePrinter tablePrinter;

    public CategoryMenu(
            CategoryService categoryService,
            ConsoleReader reader,
            TablePrinter tablePrinter) {
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
                case "1" -> createCategory();
                case "2" -> listAllCategories();
                case "3" -> listIncomeCategories();
                case "4" -> listExpenseCategories();
                case "5" -> renameCategory();
                case "6" -> deleteCategory();
                case "0" -> back = true;
                default -> System.out.println("❌ Неверный выбор.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- УПРАВЛЕНИЕ КАТЕГОРИЯМИ ---");
        System.out.println("1. Создать категорию");
        System.out.println("2. Список всех категорий");
        System.out.println("3. Список категорий доходов");
        System.out.println("4. Список категорий расходов");
        System.out.println("5. Переименовать категорию");
        System.out.println("6. Удалить категорию");
        System.out.println("0. Назад");
    }

    private void createCategory() {
        System.out.println("Выберите тип категории:");
        System.out.println("1. Доход");
        System.out.println("2. Расход");
        String typeChoice = reader.readString("Ваш выбор");

        OperationType type;
        if ("1".equals(typeChoice)) {
            type = OperationType.INCOME;
        } else if ("2".equals(typeChoice)) {
            type = OperationType.EXPENSE;
        } else {
            System.out.println("❌ Неверный выбор типа");
            return;
        }

        String name = reader.readString("Введите название категории");
        if (name.isEmpty()) {
            System.out.println("❌ Название не может быть пустым");
            return;
        }

        Category category = categoryService.createCategory(type, name);
        System.out.println("✅ Категория создана! ID: " + category.getId() + ", Название: " + category.getName());
    }

    private void listAllCategories() {
        tablePrinter.printCategories(categoryService.getAllCategories());
    }

    private void listIncomeCategories() {
        tablePrinter.printCategories(categoryService.getCategoriesByType(OperationType.INCOME));
    }

    private void listExpenseCategories() {
        tablePrinter.printCategories(categoryService.getCategoriesByType(OperationType.EXPENSE));
    }

    private void renameCategory() {
        Integer id = reader.readInt("Введите ID категории");
        if (id == null) return;

        Optional<Category> category = categoryService.getCategory(id);
        if (category.isEmpty()) {
            System.out.println("❌ Категория не найдена");
            return;
        }

        String newName = reader.readString("Введите новое название");
        if (newName.isEmpty()) {
            System.out.println("❌ Название не может быть пустым");
            return;
        }

        try {
            Category renamed = categoryService.renameCategory(id, newName);
            System.out.println("✅ Категория переименована: " + renamed.getName());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void deleteCategory() {
        Integer id = reader.readInt("Введите ID категории для удаления");
        if (id == null) return;

        Optional<Category> category = categoryService.getCategory(id);
        if (category.isEmpty()) {
            System.out.println("❌ Категория не найдена");
            return;
        }

        if (reader.confirm("Вы уверены?")) {
            try {
                categoryService.deleteCategory(id);
                System.out.println("✅ Категория удалена");
            } catch (IllegalStateException e) {
                System.out.println("❌ " + e.getMessage());
            }
        } else {
            System.out.println("Операция отменена");
        }
    }
}