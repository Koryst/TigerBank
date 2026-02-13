package ru.tigerbank.service.importexport.importer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;
import ru.tigerbank.service.importexport.model.DataSnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

@Component
public class JsonImporter implements DataImporter {

    private final ObjectMapper objectMapper;

    public JsonImporter() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @Override
    public DataSnapshot importData(InputStream inputStream) {
        try {
            return objectMapper.readValue(inputStream, DataSnapshot.class);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to import from JSON", e);
        }
    }

    @Override
    public String getFormat() {
        return "json";
    }
}
