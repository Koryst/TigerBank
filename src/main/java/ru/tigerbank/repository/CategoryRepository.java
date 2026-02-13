package ru.tigerbank.repository;

import ru.tigerbank.domain.Category;
import ru.tigerbank.domain.OperationType;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(Integer id);
    List<Category> findAll();
    List<Category> findByType(OperationType type);
    void deleteById(Integer id);
    boolean existsById(Integer id);
}
