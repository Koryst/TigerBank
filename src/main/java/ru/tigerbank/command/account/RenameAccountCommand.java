package ru.tigerbank.command.account;

import ru.tigerbank.command.Command;
import ru.tigerbank.console.utils.ConsoleReader;
import ru.tigerbank.console.utils.TablePrinter;
import ru.tigerbank.domain.BankAccount;
import ru.tigerbank.factory.EntityFactory;
import ru.tigerbank.service.account.BankAccountService;

import java.util.Optional;

public class RenameAccountCommand implements Command {
    private final BankAccountService service;
    private final ConsoleReader reader;

    public RenameAccountCommand(BankAccountService service, ConsoleReader reader) {
        this.service = service;
        this.reader = reader;
    }

    @Override
    public void execute() {
        Integer id = reader.readInt("Введите ID счета");
        if (id == null) return;

        Optional<BankAccount> account = service.getAccount(id);
        if (account.isEmpty()) {
            System.out.println("❌ Счет не найден");
            return;
        }

        String newName = reader.readString("Введите новое название");
        if (newName.isEmpty()) {
            System.out.println("❌ Название не может быть пустым");
            return;
        }

        try {
            BankAccount renamed = service.renameAccount(id, newName);
            System.out.println("✅ Счет переименован: " + renamed.getName());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "Переименовать счет";
    }
}
