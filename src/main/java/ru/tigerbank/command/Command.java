package ru.tigerbank.command;

public interface Command {
    void execute();
    String getDescription();
}
