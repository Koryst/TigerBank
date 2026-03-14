package ru.tigerbank.command.account;

import ru.tigerbank.command.Command;
import ru.tigerbank.console.utils.ConsoleReader;
import ru.tigerbank.domain.BankAccount;
import ru.tigerbank.service.account.BankAccountService;

import java.util.Optional;

public class DeleteAccountCommand  implements Command {
    private final BankAccountService service;
    private final ConsoleReader reader;

    public DeleteAccountCommand(BankAccountService service, ConsoleReader reader) {
        this.service = service;
        this.reader = reader;
    }

    @Override
    public void execute() {
        Integer id = reader.readInt("Введите ID счета для удаления");
        if (id == null) return;

        Optional<BankAccount> account = service.getAccount(id);
        if (account.isEmpty()) {
            System.out.println("❌ Счет не найден");
            return;
        }

        if (reader.confirm("Вы уверены?")) {
            try {
                service.deleteAccount(id);
                System.out.println("✅ Счет удален");
            } catch (IllegalStateException e) {
                System.out.println("❌ " + e.getMessage());
            }
        } else {
            System.out.println("Операция отменена");
        }
    }

    @Override
    public String getDescription() {
        return "Удалить счет";
    }
}
