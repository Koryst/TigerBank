package ru.tigerbank.console;

import org.springframework.stereotype.Component;
import ru.tigerbank.command.Command;
import ru.tigerbank.command.CommandInvoker;
import ru.tigerbank.command.account.*;
import ru.tigerbank.console.utils.ConsoleReader;
import ru.tigerbank.console.utils.TablePrinter;
import ru.tigerbank.domain.BankAccount;
import ru.tigerbank.factory.EntityFactory;
import ru.tigerbank.service.account.BankAccountService;

import java.util.Optional;

@Component
public class AccountMenu {
    private final ConsoleReader reader;
    private final CommandInvoker invoker;

    public AccountMenu(
            BankAccountService accountService,
            ConsoleReader reader,
            TablePrinter tablePrinter,
            EntityFactory factory) {
        this.reader = reader;
        this.invoker = new CommandInvoker();

        invoker.addCommand(new CreateAccountCommand(accountService, factory, reader));
        invoker.addCommand(new ShowAccountsCommand(accountService, tablePrinter));
        invoker.addCommand(new RenameAccountCommand(accountService, reader));
        invoker.addCommand(new AdjustBalanceCommand(accountService, reader));
        invoker.addCommand(new RecalculateBalanceCommand(accountService, reader));
        invoker.addCommand(new DeleteAccountCommand(accountService, reader));
    }

    public void show() {
        while (true) {
            System.out.println("\n--- УПРАВЛЕНИЕ СЧЕТАМИ ---");
            invoker.showMenu();
            String choice = reader.readString("Выберите пункт");

            if (choice.equals("0")) {
                break;
            }

            try {
                int num = Integer.parseInt(choice);
                Command cmd =  invoker.getCommand(num);
                if (cmd != null) {
                    cmd.execute();
                } else {
                    System.out.println("❌ Неверный выбор.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Введите число.");
            }
        }
    }
}