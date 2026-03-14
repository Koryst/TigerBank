package ru.tigerbank;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.tigerbank.config.AppConfig;
import ru.tigerbank.console.TigerBankConsole;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        System.out.println("Запуск ТигрБанк...");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {

            System.out.println("✅ Spring контекст загружен");

            TigerBankConsole console = context.getBean(TigerBankConsole.class);
            console.start();
        }

        System.out.println("👋 Программа завершена");
    }
}