package ru.tigerbank.service.importexport;

import org.springframework.stereotype.Service;
import ru.tigerbank.domain.BankAccount;
import ru.tigerbank.domain.Category;
import ru.tigerbank.domain.Operation;
import ru.tigerbank.repository.BankAccountRepository;
import ru.tigerbank.repository.CategoryRepository;
import ru.tigerbank.repository.OperationRepository;
import ru.tigerbank.service.importexport.exporter.DataExporter;
import ru.tigerbank.service.importexport.importer.DataImporter;
import ru.tigerbank.service.importexport.model.DataSnapshot;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ImportExportServiceImpl implements ImportExportService {

    private final BankAccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final OperationRepository operationRepository;

    private final Map<String, DataExporter> exporters;
    private final Map<String, DataImporter> importers;

    public ImportExportServiceImpl(
            BankAccountRepository accountRepository,
            CategoryRepository categoryRepository,
            OperationRepository operationRepository,
            List<DataExporter> exporters,
            List<DataImporter> importers) {

        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.operationRepository = operationRepository;

        this.exporters = exporters.stream()
                .collect(Collectors.toMap(DataExporter::getFormat, Function.identity()));

        this.importers = importers.stream()
                .collect(Collectors.toMap(DataImporter::getFormat, Function.identity()));
    }

    @Override
    public void exportToJson(OutputStream outputStream) {
        export("json", outputStream);
    }

    @Override
    public void exportToCsv(OutputStream outputStream) {
        export("csv", outputStream);
    }

    @Override
    public void exportToYaml(OutputStream outputStream) {
        export("yaml", outputStream);
    }

    @Override
    public void export(String format, OutputStream outputStream) {
        DataExporter exporter = exporters.get(format.toLowerCase());
        if (exporter == null) {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }

        DataSnapshot snapshot = createSnapshot();
        exporter.export(snapshot, outputStream);
    }

    @Override
    public DataSnapshot importFromJson(InputStream inputStream) {
        return importData("json", inputStream);
    }

    @Override
    public DataSnapshot importFromCsv(InputStream inputStream) {
        return importData("csv", inputStream);
    }

    @Override
    public DataSnapshot importFromYaml(InputStream inputStream) {
        return importData("yaml", inputStream);
    }

    @Override
    public DataSnapshot importData(String format, InputStream inputStream) {
        DataImporter importer = importers.get(format.toLowerCase());
        if (importer == null) {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }

        return importer.importData(inputStream);
    }

    @Override
    public void applySnapshot(DataSnapshot snapshot, boolean overwrite) {
        if (overwrite) {
            // Очищаем все данные
            operationRepository.findAll().forEach(op ->
                    operationRepository.deleteById(op.getId()));
            categoryRepository.findAll().forEach(cat ->
                    categoryRepository.deleteById(cat.getId()));
            accountRepository.findAll().forEach(acc ->
                    accountRepository.deleteById(acc.getId()));
        }

        // Сохраняем счета
        for (BankAccount account : snapshot.getAccounts()) {
            accountRepository.save(account);
        }

        // Сохраняем категории
        for (Category category : snapshot.getCategories()) {
            categoryRepository.save(category);
        }

        // Сохраняем операции
        for (Operation operation : snapshot.getOperations()) {
            operationRepository.save(operation);
        }
    }

    private DataSnapshot createSnapshot() {
        return new DataSnapshot(
                accountRepository.findAll(),
                categoryRepository.findAll(),
                operationRepository.findAll()
        );
    }
}
