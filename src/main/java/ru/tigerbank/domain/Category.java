package ru.tigerbank.domain;

public class Category {
    private Integer id;
    private OperationType type;  // переименовать с operationType на type
    private String name;

    public Category() { }

    public Category(Integer id, OperationType type, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Category type cannot be null");
        }
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public Integer getId() { return id; }
    public OperationType getType() { return type; }
    public String getName() { return name; }
}
