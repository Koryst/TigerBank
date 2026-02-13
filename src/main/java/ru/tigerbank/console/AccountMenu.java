package ru.tigerbank.console;

import org.springframework.stereotype.Component;
import ru.tigerbank.console.utils.ConsoleReader;
import ru.tigerbank.console.utils.TablePrinter;
import ru.tigerbank.domain.BankAccount;
import ru.tigerbank.service.account.BankAccountService;

import java.util.Optional;

@Component
public class AccountMenu {

    private final BankAccountService accountService;
    private final ConsoleReader reader;
    private final TablePrinter tablePrinter;

    public AccountMenu(
            BankAccountService accountService,
            ConsoleReader reader,
            TablePrinter tablePrinter) {
        this.accountService = accountService;
        this.reader = reader;
        this.tablePrinter = tablePrinter;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            printMenu();
            String choice = reader.readString("Выберите пункт");

            switch (choice) {
                case "1" -> createAccount();
                case "2" -> listAccounts();
                case "3" -> renameAccount();
                case "4" -> adjustBalance();
                case "5" -> recalculateBalance();
                case "6" -> deleteAccount();
                case "0" -> back = true;
                default -> System.out.println("❌ Неверный выбор.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- УПРАВЛЕНИЕ СЧЕТАМИ ---");
        System.out.println("1. Создать счет");
        System.out.println("2. Список всех счетов");
        System.out.println("3. Переименовать счет");
        System.out.println("4. Скорректировать баланс");
        System.out.println("5. Пересчитать баланс по операциям");
        System.out.println("6. Удалить счет");
        System.out.println("0. Назад");
    }

    private void createAccount() {
        String name = reader.readString("Введите название счета");
        if (name.isEmpty()) {
            System.out.println("❌ Название не может быть пустым");
            return;
        }

        BankAccount account = accountService.createAccount(name);
        System.out.println("✅ Счет создан! ID: " + account.getId() + ", Название: " + account.getName());
    }

    private void listAccounts() {
        tablePrinter.printAccounts(accountService.getAllAccounts());
    }

    private void renameAccount() {
        Integer id = reader.readInt("Введите ID счета");
        if (id == null) return;

        Optional<BankAccount> account = accountService.getAccount(id);
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
            BankAccount renamed = accountService.renameAccount(id, newName);
            System.out.println("✅ Счет переименован: " + renamed.getName());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void adjustBalance() {
        Integer id = reader.readInt("Введите ID счета");
        if (id == null) return;

        Optional<BankAccount> account = accountService.getAccount(id);
        if (account.isEmpty()) {
            System.out.println("❌ Счет не найден");
            return;
        }

        Double amount = reader.readDouble("Введите новый баланс");
        if (amount == null) return;

        accountService.adjustBalance(id, amount);
        System.out.printf("✅ Баланс счета %d скорректирован до %.2f%n", id, amount);
    }

    private void recalculateBalance() {
        Integer id = reader.readInt("Введите ID счета");
        if (id == null) return;

        Optional<BankAccount> account = accountService.getAccount(id);
        if (account.isEmpty()) {
            System.out.println("❌ Счет не найден");
            return;
        }

        double newBalance = accountService.recalculateBalance(id);
        System.out.printf("✅ Баланс пересчитан. Новый баланс: %.2f%n", newBalance);
    }

    private void deleteAccount() {
        Integer id = reader.readInt("Введите ID счета для удаления");
        if (id == null) return;

        Optional<BankAccount> account = accountService.getAccount(id);
        if (account.isEmpty()) {
            System.out.println("❌ Счет не найден");
            return;
        }

        if (reader.confirm("Вы уверены?")) {
            try {
                accountService.deleteAccount(id);
                System.out.println("✅ Счет удален");
            } catch (IllegalStateException e) {
                System.out.println("❌ " + e.getMessage());
            }
        } else {
            System.out.println("Операция отменена");
        }
    }
}