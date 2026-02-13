package ru.tigerbank.console;

import org.springframework.stereotype.Component;
import ru.tigerbank.console.utils.ConsoleReader;
import ru.tigerbank.console.utils.TablePrinter;
import ru.tigerbank.service.account.BankAccountService;
import ru.tigerbank.service.analytics.AnalyticsService;

import java.time.LocalDate;
import java.util.Map;

@Component
public class AnalyticsMenu {

    private final AnalyticsService analyticsService;
    private final BankAccountService accountService;
    private final ConsoleReader reader;
    private final TablePrinter tablePrinter;

    public AnalyticsMenu(
            AnalyticsService analyticsService,
            BankAccountService accountService,
            ConsoleReader reader,
            TablePrinter tablePrinter) {
        this.analyticsService = analyticsService;
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
                case "1" -> netBalanceAll();
                case "2" -> netBalanceByAccount();
                case "3" -> incomeByCategory();
                case "4" -> expenseByCategory();
                case "5" -> totalIncomeExpense();
                case "0" -> back = true;
                default -> System.out.println("❌ Неверный выбор.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- АНАЛИТИКА ---");
        System.out.println("1. Разница доходов/расходов (все счета)");
        System.out.println("2. Разница доходов/расходов по счету");
        System.out.println("3. Доходы по категориям");
        System.out.println("4. Расходы по категориям");
        System.out.println("5. Общая сумма доходов и расходов");
        System.out.println("0. Назад");
    }

    private LocalDate[] readDatePeriod() {
        LocalDate from = reader.readDate("Введите начальную дату");
        if (from == null) return null;

        LocalDate to = reader.readDate("Введите конечную дату");
        if (to == null) return null;

        return new LocalDate[]{from, to};
    }

    private void netBalanceAll() {
        LocalDate[] period = readDatePeriod();
        if (period == null) return;

        double balance = analyticsService.getNetBalance(period[0], period[1]);
        System.out.printf("\n💰 Разница доходов и расходов за период: %.2f%n", balance);

        if (balance > 0) {
            System.out.println("📈 Положительный баланс (доходы > расходы)");
        } else if (balance < 0) {
            System.out.println("📉 Отрицательный баланс (расходы > доходы)");
        } else {
            System.out.println("⚖️ Нулевой баланс");
        }
    }

    private void netBalanceByAccount() {
        if (accountService.getAllAccounts().isEmpty()) {
            System.out.println("❌ Нет доступных счетов");
            return;
        }

        tablePrinter.printAccounts(accountService.getAllAccounts());

        Integer accountId = reader.readInt("Введите ID счета");
        if (accountId == null) return;

        LocalDate[] period = readDatePeriod();
        if (period == null) return;

        try {
            double balance = analyticsService.getNetBalanceByAccount(accountId, period[0], period[1]);
            System.out.printf("\n💰 Разница доходов и расходов по счету за период: %.2f%n", balance);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void incomeByCategory() {
        LocalDate[] period = readDatePeriod();
        if (period == null) return;

        Map<String, Double> incomeByCategory = analyticsService.getIncomeByCategory(period[0], period[1]);

        if (incomeByCategory.isEmpty()) {
            System.out.println("📭 Нет доходов за указанный период");
            return;
        }

        System.out.println("\n📊 ДОХОДЫ ПО КАТЕГОРИЯМ:");
        System.out.println("┌──────────────────────┬────────────┐");
        System.out.println("│ Категория            │ Сумма      │");
        System.out.println("├──────────────────────┼────────────┤");

        double total = 0;
        for (Map.Entry<String, Double> entry : incomeByCategory.entrySet()) {
            System.out.printf("│ %-20s │ %10.2f │%n",
                    truncate(entry.getKey(), 20),
                    entry.getValue());
            total += entry.getValue();
        }

        System.out.println("├──────────────────────┼────────────┤");
        System.out.printf("│ %-20s │ %10.2f │%n", "ИТОГО:", total);
        System.out.println("└──────────────────────┴────────────┘");
    }

    private void expenseByCategory() {
        LocalDate[] period = readDatePeriod();
        if (period == null) return;

        Map<String, Double> expenseByCategory = analyticsService.getExpenseByCategory(period[0], period[1]);

        if (expenseByCategory.isEmpty()) {
            System.out.println("📭 Нет расходов за указанный период");
            return;
        }

        System.out.println("\n📊 РАСХОДЫ ПО КАТЕГОРИЯМ:");
        System.out.println("┌──────────────────────┬────────────┐");
        System.out.println("│ Категория            │ Сумма      │");
        System.out.println("├──────────────────────┼────────────┤");

        double total = 0;
        for (Map.Entry<String, Double> entry : expenseByCategory.entrySet()) {
            System.out.printf("│ %-20s │ %10.2f │%n",
                    truncate(entry.getKey(), 20),
                    entry.getValue());
            total += entry.getValue();
        }

        System.out.println("├──────────────────────┼────────────┤");
        System.out.printf("│ %-20s │ %10.2f │%n", "ИТОГО:", total);
        System.out.println("└──────────────────────┴────────────┘");
    }

    private void totalIncomeExpense() {
        LocalDate[] period = readDatePeriod();
        if (period == null) return;

        double income = analyticsService.getTotalIncome(period[0], period[1]);
        double expense = analyticsService.getTotalExpense(period[0], period[1]);

        System.out.println("\n💰 ОБЩИЕ СУММЫ ЗА ПЕРИОД:");
        System.out.printf("Доходы:  %.2f%n", income);
        System.out.printf("Расходы: %.2f%n", expense);
        System.out.printf("Разница: %.2f%n", income - expense);
    }

    private String truncate(String str, int length) {
        if (str == null) return "";
        if (str.length() <= length) return str;
        return str.substring(0, length - 3) + "...";
    }
}