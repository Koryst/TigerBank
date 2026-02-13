package ru.tigerbank.repository;

import ru.tigerbank.domain.BankAccount;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository {
    BankAccount save(BankAccount account);
    Optional<BankAccount> findById(Integer id);
    List<BankAccount> findAll();
    void deleteById(Integer id);
    boolean existsById(Integer id);
}
