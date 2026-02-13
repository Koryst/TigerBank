package ru.tigerbank.console.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

@Component
public class ConsoleReader {
    private final Scanner scanner = new Scanner(System.in);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public String readString(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    public Integer readInt(String prompt) {
        System.out.print(prompt + ": ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("❌ Некорректный ввод. Ожидается целое число.");
            return null;
        }
    }

    public Double readDouble(String prompt) {
        System.out.print(prompt + ": ");
        try {
            return Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            System.out.println("❌ Некорректный ввод. Ожидается число.");
            return null;
        }
    }

    public LocalDate readDate(String prompt) {
        System.out.print(prompt + " (дд.мм.гггг): ");
        try {
            String input = scanner.nextLine().trim();
            return LocalDate.parse(input, dateFormatter);
        } catch (DateTimeParseException e) {
            System.out.println("❌ Некорректная дата. Используйте формат дд.мм.гггг");
            return null;
        }
    }

    public boolean confirm(String prompt) {
        System.out.print(prompt + " (да/нет): ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("да") || input.equals("yes") || input.equals("y");
    }

    public void pressEnterToContinue() {
        System.out.print("\nНажмите Enter для продолжения...");
        scanner.nextLine();
    }
}