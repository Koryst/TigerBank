package ru.tigerbank.command.account;

import ru.tigerbank.command.Command;
import ru.tigerbank.console.utils.ConsoleReader;
import ru.tigerbank.domain.BankAccount;
import ru.tigerbank.service.account.BankAccountService;

import java.util.Optional;

public class AdjustBalanceCommand implements Command {
    private final BankAccountService service;
    private final ConsoleReader reader;

    public AdjustBalanceCommand(BankAccountService service,  ConsoleReader reader) {
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

        Double amount = reader.readDouble("Введите новый баланс");
        if (amount == null) return;

        service.adjustBalance(id, amount);
        System.out.printf("✅ Баланс счета %d скорректирован до %.2f%n", id, amount);
    }

    @Override
    public String getDescription() {
        return "Скорректировать баланс";
    }
}
