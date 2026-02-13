package ru.tigerbank.service.importexport;

import ru.tigerbank.service.importexport.model.DataSnapshot;

import java.io.InputStream;
import java.io.OutputStream;

public interface ImportExportService {

    // Экспорт
    void exportToJson(OutputStream outputStream);
    void exportToCsv(OutputStream outputStream);
    void exportToYaml(OutputStream outputStream);
    void export(String format, OutputStream outputStream);

    // Импорт
    DataSnapshot importFromJson(InputStream inputStream);
    DataSnapshot importFromCsv(InputStream inputStream);
    DataSnapshot importFromYaml(InputStream inputStream);
    DataSnapshot importData(String format, InputStream inputStream);

    // Применение импортированных данных
    void applySnapshot(DataSnapshot snapshot, boolean overwrite);
}