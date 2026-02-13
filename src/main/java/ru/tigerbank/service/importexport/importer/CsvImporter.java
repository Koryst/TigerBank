package ru.tigerbank.service.importexport.importer;

import org.springframework.stereotype.Component;
import ru.tigerbank.domain.BankAccount;
import ru.tigerbank.domain.Category;
import ru.tigerbank.domain.Operation;
import ru.tigerbank.domain.OperationType;
import ru.tigerbank.service.importexport.model.DataSnapshot;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvImporter implements DataImporter {

    @Override
    public DataSnapshot importData(InputStream inputStream) {
        List<BankAccount> accounts = new ArrayList<>();
        List<Category> categories = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, "UTF-8"))) {

            String line;
            String section = null;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Пропускаем пустые строки
                if (line.isEmpty()) {
                    continue;
                }

                // Удаляем BOM если есть (первые 3 байта файла)
                if (lineNumber == 1 && line.startsWith("\uFEFF")) {
                    line = line.substring(1);
                }

                // Определяем секцию
                if (line.startsWith("=== ") && line.endsWith(" ===")) {
                    section = line.substring(4, line.length() - 4);
                    continue;
                }

                // Пропускаем заголовки
                if (line.startsWith("id,") || line.startsWith("id,")) {
                    continue;
                }

                // Парсим данные в зависимости от секции
                try {
                    if ("ACCOUNTS".equals(section)) {
                        parseAccount(line, accounts);
                    } else if ("CATEGORIES".equals(section)) {
                        parseCategory(line, categories);
                    } else if ("OPERATIONS".equals(section)) {
                        parseOperation(line, operations);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing line " + lineNumber + ": " + line);
                    System.err.println("Error: " + e.getMessage());
                    // Пропускаем строку с ошибкой
                }
            }

        } catch (IOException e) {
            throw new UncheckedIOException("Failed to import from CSV", e);
        }

        return new DataSnapshot(accounts, categories, operations);
    }

    private void parseAccount(String line, List<BankAccount> accounts) {
        String[] parts = splitCsvLine(line);
        if (parts.length < 3) {
            return;
        }

        try {
            Integer id = parseInteger(parts[0]);
            String name = parts[1].trim();
            Double balance = parseDouble(parts[2]);

            if (id != null && name != null && !name.isEmpty() && balance != null) {
                BankAccount account = new BankAccount(id, name);
                account.setBalance(balance);
                accounts.add(account);
            }
        } catch (Exception e) {
            // Пропускаем
        }
    }

    private void parseCategory(String line, List<Category> categories) {
        String[] parts = splitCsvLine(line);
        if (parts.length < 3) {
            return;
        }

        try {
            Integer id = parseInteger(parts[0]);
            String typeStr = parts[1].trim();
            String name = parts[2].trim();

            if (id != null && typeStr != null && !typeStr.isEmpty() &&
                    name != null && !name.isEmpty()) {

                OperationType type = parseOperationType(typeStr);
                if (type != null) {
                    Category category = new Category(id, type, name);
                    categories.add(category);
                }
            }
        } catch (Exception e) {
            // Пропускаем
        }
    }

    private void parseOperation(String line, List<Operation> operations) {
        String[] parts = splitCsvLine(line);
        if (parts.length < 7) {
            return;
        }

        try {
            Integer id = parseInteger(parts[0]);
            String typeStr = parts[1].trim();
            Integer accountId = parseInteger(parts[2]);
            Integer categoryId = parseInteger(parts[3]);
            Double amount = parseDouble(parts[4]);
            String dateStr = parts[5].trim();
            String description = parts.length > 6 ? parts[6].trim() : "";

            if (id != null && accountId != null && categoryId != null &&
                    amount != null && dateStr != null && !dateStr.isEmpty()) {

                OperationType type = parseOperationType(typeStr);
                LocalDate date = parseDate(dateStr);

                if (type != null && date != null) {
                    // Пустое описание делаем null
                    if (description.isEmpty()) {
                        description = null;
                    }

                    Operation operation = new Operation(
                            id, type, accountId, categoryId, amount, date, description
                    );
                    operations.add(operation);
                }
            }
        } catch (Exception e) {
            // Пропускаем
        }
    }

    private String[] splitCsvLine(String line) {
        // Простое разбиение по запятой с учетом возможных кавычек
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }

        result.add(current.toString());

        return result.toArray(new String[0]);
    }

    private Integer parseInteger(String str) {
        if (str == null) return null;
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDouble(String str) {
        if (str == null) return null;
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private OperationType parseOperationType(String str) {
        if (str == null) return null;
        try {
            return OperationType.valueOf(str.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private LocalDate parseDate(String str) {
        if (str == null) return null;
        try {
            return LocalDate.parse(str.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    @Override
    public String getFormat() {
        return "csv";
    }
}