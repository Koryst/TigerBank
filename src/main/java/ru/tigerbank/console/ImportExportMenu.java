package ru.tigerbank.console;

import org.springframework.stereotype.Component;
import ru.tigerbank.console.utils.ConsoleReader;
import ru.tigerbank.service.importexport.ImportExportService;
import ru.tigerbank.service.importexport.model.DataSnapshot;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ImportExportMenu {

    private final ImportExportService importExportService;
    private final ConsoleReader reader;

    public ImportExportMenu(
            ImportExportService importExportService,
            ConsoleReader reader) {
        this.importExportService = importExportService;
        this.reader = reader;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            printMenu();
            String choice = reader.readString("Выберите пункт");

            switch (choice) {
                case "1" -> exportJson();
                case "2" -> exportCsv();
                case "3" -> exportYaml();
                case "4" -> importJson();
                case "5" -> importCsv();
                case "6" -> importYaml();
                case "0" -> back = true;
                default -> System.out.println("❌ Неверный выбор.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- ИМПОРТ/ЭКСПОРТ ---");
        System.out.println("1. Экспорт в JSON");
        System.out.println("2. Экспорт в CSV");
        System.out.println("3. Экспорт в YAML");
        System.out.println("4. Импорт из JSON");
        System.out.println("5. Импорт из CSV");
        System.out.println("6. Импорт из YAML");
        System.out.println("0. Назад");
    }

    private void exportJson() {
        export("json");
    }

    private void exportCsv() {
        export("csv");
    }

    private void exportYaml() {
        export("yaml");
    }

    private void export(String format) {
        String filename = reader.readString("Введите имя файла для экспорта (например, data." + format + ")");
        if (filename.isEmpty()) {
            System.out.println("❌ Имя файла не может быть пустым");
            return;
        }

        Path path = Paths.get(filename);
        try (OutputStream os = new FileOutputStream(path.toFile())) {
            switch (format) {
                case "json" -> importExportService.exportToJson(os);
                case "csv" -> importExportService.exportToCsv(os);
                case "yaml" -> importExportService.exportToYaml(os);
            }
            System.out.println("✅ Данные экспортированы в файл: " + path.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("❌ Ошибка при экспорте: " + e.getMessage());
        }
    }

    private void importJson() {
        importData("json");
    }

    private void importCsv() {
        importData("csv");
    }

    private void importYaml() {
        importData("yaml");
    }

    private void importData(String format) {
        String filename = reader.readString("Введите имя файла для импорта (например, data." + format + ")");
        if (filename.isEmpty()) {
            System.out.println("❌ Имя файла не может быть пустым");
            return;
        }

        Path path = Paths.get(filename);
        if (!path.toFile().exists()) {
            System.out.println("❌ Файл не найден: " + path.toAbsolutePath());
            return;
        }

        try (InputStream is = new FileInputStream(path.toFile())) {
            // Сначала читаем данные
            DataSnapshot snapshot;
            switch (format) {
                case "json" -> snapshot = importExportService.importFromJson(is);
                case "csv" -> snapshot = importExportService.importFromCsv(is);
                case "yaml" -> snapshot = importExportService.importFromYaml(is);
                default -> throw new IllegalArgumentException("Unsupported format: " + format);
            }

            System.out.println("✅ Данные прочитаны из файла:");
            System.out.println("  - Счетов: " + snapshot.getAccounts().size());
            System.out.println("  - Категорий: " + snapshot.getCategories().size());
            System.out.println("  - Операций: " + snapshot.getOperations().size());

            // Спрашиваем, как применять
            boolean overwrite = reader.confirm("Заменить существующие данные? (нет - добавить к существующим)");

            // Применяем
            importExportService.applySnapshot(snapshot, overwrite);
            System.out.println("✅ Данные импортированы");

        } catch (IOException e) {
            System.out.println("❌ Ошибка при импорте: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Ошибка в формате данных: " + e.getMessage());
        }
    }
}