package ru.tigerbank.service.account;

import ru.tigerbank.domain.BankAccount;

import java.util.List;
import java.util.Optional;

public interface BankAccountService {

    // Создание нового счета (ID сгенерирует репозиторий)
    BankAccount createAccount(String name);

    // Создание счета с заданным ID (для импорта)
    BankAccount createAccount(Integer id, String name);

    // Чтение
    Optional<BankAccount> getAccount(Integer id);
    List<BankAccount> getAllAccounts();

    // Обновление
    BankAccount renameAccount(Integer id, String newName);

    // Ручная корректировка баланса
    void adjustBalance(Integer id, double newBalance);

    // Удаление (с проверкой на наличие операций)
    void deleteAccount(Integer id) throws IllegalStateException;

    // Автоматический пересчет баланса по операциям
    double recalculateBalance(Integer accountId);
}
