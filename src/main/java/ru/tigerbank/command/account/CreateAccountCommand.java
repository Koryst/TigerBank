package ru.tigerbank.command.account;

import ru.tigerbank.command.Command;
import ru.tigerbank.console.utils.ConsoleReader;
import ru.tigerbank.domain.BankAccount;
import ru.tigerbank.factory.EntityFactory;
import ru.tigerbank.service.account.BankAccountService;

public class CreateAccountCommand implements Command {

    private final BankAccountService service;
    private final EntityFactory factory;
    private final ConsoleReader reader;

    public CreateAccountCommand(BankAccountService service, EntityFactory factory, ConsoleReader reader) {
        this.service = service;
        this.factory = factory;
        this.reader = reader;
    }

    @Override
    public void execute() {
        String name = reader.readString("Введите название счета");
        if (name.isEmpty()) {
            System.out.println("❌ Название не может быть пустым");
            return;
        }

        BankAccount account = factory.createBankAccount(name);
        service.addBankAccount(account);
        System.out.println("✅ Счет создан! ID: " + account.getId() + ", Название: " + account.getName());
    }

    @Override
    public String getDescription() {
        return "Создать новый банковский счёт";
    }
}