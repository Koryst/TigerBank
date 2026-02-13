package ru.tigerbank.console.utils;

import org.springframework.stereotype.Component;
import ru.tigerbank.domain.BankAccount;

import java.util.List;

@Component
public class TablePrinter {

    public void printAccounts(List<BankAccount> accounts) {
        if (accounts.isEmpty()) {
            System.out.println("📭 Счетов пока нет");
            return;
        }

        System.out.println("\n📊 СПИСОК СЧЕТОВ:");
        System.out.println("┌──────┬──────────────────────┬────────────┐");
        System.out.println("│ ID   │ Название             │ Баланс    │");
        System.out.println("├──────┼──────────────────────┼────────────┤");

        for (var acc : accounts) {
            System.out.printf("│ %-4d │ %-20s │ %10.2f │%n",
                    acc.getId(),
                    truncate(acc.getName(), 20),
                    acc.getBalance());
        }

        System.out.println("└──────┴──────────────────────┴────────────┘");
    }

    public void printCategories(List<ru.tigerbank.domain.Category> categories) {
        if (categories.isEmpty()) {
            System.out.println("📭 Категорий пока нет");
            return;
        }

        System.out.println("\n📂 СПИСОК КАТЕГОРИЙ:");
        System.out.println("┌──────┬────────────┬──────────────────────┐");
        System.out.println("│ ID   │ Тип        │ Название             │");
        System.out.println("├──────┼────────────┼──────────────────────┤");

        for (var cat : categories) {
            System.out.printf("│ %-4d │ %-10s │ %-20s │%n",
                    cat.getId(),
                    cat.getType(),
                    truncate(cat.getName(), 20));
        }

        System.out.println("└──────┴────────────┴──────────────────────┘");
    }

    public void printOperations(List<ru.tigerbank.domain.Operation> operations) {
        if (operations.isEmpty()) {
            System.out.println("📭 Операций пока нет");
            return;
        }

        System.out.println("\n📝 СПИСОК ОПЕРАЦИЙ:");
        System.out.println("┌──────┬────────┬──────────┬──────────┬────────────┬──────────────────────┐");
        System.out.println("│ ID   │ Тип    │ Счет     │ Категория│ Сумма     │ Дата       │ Описание │");
        System.out.println("├──────┼────────┼──────────┼──────────┼────────────┼──────────────────────┤");

        for (var op : operations) {
            System.out.printf("│ %-4d │ %-6s │ %-8d │ %-8d │ %10.2f │ %-10s │ %-20s │%n",
                    op.getId(),
                    op.getType(),
                    op.getBankAccountId(),
                    op.getCategoryId(),
                    op.getAmount(),
                    op.getDate(),
                    truncate(op.getDescription() != null ? op.getDescription() : "", 20));
        }

        System.out.println("└──────┴────────┴──────────┴──────────┴────────────┴──────────────────────┘");
    }

    private String truncate(String str, int length) {
        if (str == null) return "";
        if (str.length() <= length) return str;
        return str.substring(0, length - 3) + "...";
    }
}