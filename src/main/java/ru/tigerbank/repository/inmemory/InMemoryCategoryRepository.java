package ru.tigerbank.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.tigerbank.domain.Category;
import ru.tigerbank.domain.OperationType;
import ru.tigerbank.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryCategoryRepository implements CategoryRepository {

    private final Map<Integer, Category> storage = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public Category save(Category category) {
        if (category.getId() == null) {
            Category newCategory = new Category(
                    idGenerator.getAndIncrement(),
                    category.getType(),
                    category.getName()
            );
            storage.put(newCategory.getId(), newCategory);
            return newCategory;
        } else {
            storage.put(category.getId(), category);
            if (category.getId() >= idGenerator.get()) {
                idGenerator.set(category.getId() + 1);
            }
            return category;
        }
    }

    @Override
    public Optional<Category> findById(Integer id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Category> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Category> findByType(OperationType type) {
        return storage.values().stream()
                .filter(c -> c.getType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Integer id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return storage.containsKey(id);
    }
}
