package ru.tigerbank.service.importexport.importer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;
import ru.tigerbank.service.importexport.model.DataSnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

@Component
public class YamlImporter implements DataImporter {

    private final ObjectMapper objectMapper;

    public YamlImporter() {
        this.objectMapper = new ObjectMapper(new YAMLFactory())
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public DataSnapshot importData(InputStream inputStream) {
        try {
            return objectMapper.readValue(inputStream, DataSnapshot.class);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to import from YAML", e);
        }
    }

    @Override
    public String getFormat() {
        return "yaml";
    }
}