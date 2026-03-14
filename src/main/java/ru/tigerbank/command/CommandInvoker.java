package ru.tigerbank.command;

import java.util.ArrayList;
import java.util.List;

public class CommandInvoker {

    private final List<Command> commands = new ArrayList<>();

    public void addCommand(Command cmd) {
        commands.add(cmd);
    }

    public void showMenu() {
        System.out.println("\nДоступные команды:");
        for (int i = 0; i < commands.size(); i++) {
            System.out.printf("%2d. %s\n", i + 1, commands.get(i).getDescription());
        }
        System.out.println(" 0. Выход");
    }

    public Command getCommand(int number) {
        if (number < 1 || number > commands.size()) return null;
        return commands.get(number - 1);
    }
}
