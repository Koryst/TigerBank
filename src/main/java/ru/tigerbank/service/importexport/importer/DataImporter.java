package ru.tigerbank.service.importexport.importer;

import ru.tigerbank.service.importexport.model.DataSnapshot;

import java.io.InputStream;

public interface DataImporter {
    DataSnapshot importData(InputStream inputStream);
    String getFormat();
}