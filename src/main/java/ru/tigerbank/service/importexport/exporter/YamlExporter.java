package ru.tigerbank.service.importexport.exporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;
import ru.tigerbank.service.importexport.model.DataSnapshot;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;

@Component
public class YamlExporter implements DataExporter {

    private final ObjectMapper objectMapper;

    public YamlExporter() {
        this.objectMapper = new ObjectMapper(new YAMLFactory())
                .registerModule(new JavaTimeModule());
    }

    @Override
    public void export(DataSnapshot snapshot, OutputStream outputStream) {
        try {
            objectMapper.writeValue(outputStream, snapshot);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to export to YAML", e);
        }
    }

    @Override
    public String getFormat() {
        return "yaml";
    }
}