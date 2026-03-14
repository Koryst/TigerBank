package ru.tigerbank.command.account;

import ru.tigerbank.command.Command;
import ru.tigerbank.console.utils.ConsoleReader;
import ru.tigerbank.domain.BankAccount;
import ru.tigerbank.service.account.BankAccountService;

import java.util.Optional;

public class RecalculateBalanceCommand implements Command {
    private final BankAccountService service;
    private final ConsoleReader reader;

    public RecalculateBalanceCommand(BankAccountService service, ConsoleReader reader) {
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

        double newBalance = service.recalculateBalance(id);
        System.out.printf("✅ Баланс пересчитан. Новый баланс: %.2f%n", newBalance);
    }

    @Override
    public String getDescription() {
        return "Пересчитать баланс по операциям";
    }
}
