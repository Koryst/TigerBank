package ru.tigerbank.service.importexport.exporter;

import ru.tigerbank.service.importexport.model.DataSnapshot;

import java.io.OutputStream;

public interface DataExporter {
    void export(DataSnapshot snapshot, OutputStream outputStream);
    String getFormat(); // "json", "csv", "yaml"
}
