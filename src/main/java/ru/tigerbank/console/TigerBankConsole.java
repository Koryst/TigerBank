package ru.tigerbank.console;

import org.springframework.stereotype.Component;

@Component
public class TigerBankConsole {

    private final AccountMenu accountMenu;
    private final CategoryMenu categoryMenu;
    private final OperationMenu operationMenu;
    private final AnalyticsMenu analyticsMenu;
    private final ImportExportMenu importExportMenu;

    public TigerBankConsole(
            AccountMenu accountMenu,
            CategoryMenu categoryMenu,
            OperationMenu operationMenu,
            AnalyticsMenu analyticsMenu,
            ImportExportMenu importExportMenu) {
        this.accountMenu = accountMenu;
        this.categoryMenu = categoryMenu;
        this.operationMenu = operationMenu;
        this.analyticsMenu = analyticsMenu;
        this.importExportMenu = importExportMenu;
    }

    public void start() {
        System.out.println("=====================================");
        System.out.println("🐯 Добро пожаловать в ТигрБанк!");
        System.out.println("=====================================");

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = readChoice();

            switch (choice) {
                case "1" -> accountMenu.show();
                case "2" -> categoryMenu.show();
                case "3" -> operationMenu.show();
                case "4" -> analyticsMenu.show();
                case "5" -> importExportMenu.show();
                case "0" -> {
                    running = false;
                    System.out.println("До свидания! Возвращайтесь в ТигрБанк!");
                }
                default -> System.out.println("❌ Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n-------------------------------------");
        System.out.println("ГЛАВНОЕ МЕНЮ:");
        System.out.println("1. Управление счетами");
        System.out.println("2. Управление категориями");
        System.out.println("3. Управление операциями");
        System.out.println("4. Аналитика");
        System.out.println("5. Импорт/Экспорт");
        System.out.println("0. Выход");
        System.out.print("Выберите пункт: ");
    }

    private String readChoice() {
        return new java.util.Scanner(System.in).nextLine().trim();
    }
}