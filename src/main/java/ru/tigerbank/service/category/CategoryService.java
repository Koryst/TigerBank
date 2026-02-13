package ru.tigerbank.service.category;

import ru.tigerbank.domain.Category;
import ru.tigerbank.domain.OperationType;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    // Создание новой категории (ID сгенерирует репозиторий)
    Category createCategory(OperationType type, String name);

    // Создание категории с заданным ID (для импорта)
    Category createCategory(Integer id, OperationType type, String name);

    // Чтение
    Optional<Category> getCategory(Integer id);
    List<Category> getAllCategories();
    List<Category> getCategoriesByType(OperationType type);

    // Обновление
    Category renameCategory(Integer id, String newName);

    // Удаление (с проверкой на наличие операций)
    void deleteCategory(Integer id) throws IllegalStateException;
}