package ru.tigerbank.service.category;

import org.springframework.stereotype.Service;
import ru.tigerbank.domain.Category;
import ru.tigerbank.domain.OperationType;
import ru.tigerbank.repository.CategoryRepository;
import ru.tigerbank.repository.OperationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final OperationRepository operationRepository;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            OperationRepository operationRepository) {
        this.categoryRepository = categoryRepository;
        this.operationRepository = operationRepository;
    }

    @Override
    public Category createCategory(OperationType type, String name) {
        Category category = new Category(null, type, name);
        return categoryRepository.save(category);
    }

    @Override
    public Category createCategory(Integer id, OperationType type, String name) {
        Category category = new Category(id, type, name);
        return categoryRepository.save(category);
    }

    @Override
    public Optional<Category> getCategory(Integer id) {
        return categoryRepository.findById(id);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> getCategoriesByType(OperationType type) {
        return categoryRepository.findByType(type);
    }

    @Override
    public Category renameCategory(Integer id, String newName) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        Category renamed = new Category(category.getId(), category.getType(), newName);
        return categoryRepository.save(renamed);
    }

    @Override
    public void deleteCategory(Integer id) {
        boolean hasOperations = operationRepository.existsByCategoryId(id);
        if (hasOperations) {
            throw new IllegalStateException(
                    "Cannot delete category with id " + id + ": there are operations linked to this category"
            );
        }
        categoryRepository.deleteById(id);
    }
}