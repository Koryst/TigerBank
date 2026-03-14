package ru.tigerbank.command.account;

import ru.tigerbank.command.Command;
import ru.tigerbank.console.utils.ConsoleReader;
import ru.tigerbank.console.utils.TablePrinter;
import ru.tigerbank.factory.EntityFactory;
import ru.tigerbank.service.account.BankAccountService;

public class ShowAccountsCommand implements Command {
    private final BankAccountService service;
    private final TablePrinter tablePrinter;

    public ShowAccountsCommand(BankAccountService service, TablePrinter tablePrinter) {
        this.service = service;
        this.tablePrinter = tablePrinter;
    }

    @Override
    public void execute() {
        tablePrinter.printAccounts(service.getAllAccounts());
    }

    @Override
    public String getDescription() {
        return "Список всех счетов";
    }
}
