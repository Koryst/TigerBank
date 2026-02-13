package ru.tigerbank.service.importexport.exporter;

import org.springframework.stereotype.Component;
import ru.tigerbank.domain.BankAccount;
import ru.tigerbank.domain.Category;
import ru.tigerbank.domain.Operation;
import ru.tigerbank.service.importexport.model.DataSnapshot;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Component
public class CsvExporter implements DataExporter {

    @Override
    public void export(DataSnapshot snapshot, OutputStream outputStream) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(
                    new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)
            );

            // Экспорт счетов
            writer.println("=== ACCOUNTS ===");
            writer.println("id,name,balance");
            for (BankAccount account : snapshot.getAccounts()) {
                writer.printf("%d,%s,%.2f%n",
                        account.getId(),
                        account.getName(),
                        account.getBalance()
                );
            }

            writer.println();

            // Экспорт категорий
            writer.println("=== CATEGORIES ===");
            writer.println("id,type,name");
            for (Category category : snapshot.getCategories()) {
                writer.printf("%d,%s,%s%n",
                        category.getId(),
                        category.getType(),
                        category.getName()
                );
            }

            writer.println();

            // Экспорт операций
            writer.println("=== OPERATIONS ===");
            writer.println("id,type,account_id,category_id,amount,date,description");
            for (Operation operation : snapshot.getOperations()) {
                String description = operation.getDescription();
                if (description == null) {
                    description = "";
                }
                writer.printf("%d,%s,%d,%d,%.2f,%s,%s%n",
                        operation.getId(),
                        operation.getType(),
                        operation.getBankAccountId(),
                        operation.getCategoryId(),
                        operation.getAmount(),
                        operation.getDate(),
                        description
                );
            }

            writer.flush();

        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    @Override
    public String getFormat() {
        return "csv";
    }
}