package ru.tigerbank.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.tigerbank.domain.BankAccount;
import ru.tigerbank.repository.BankAccountRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryBankAccountRepository implements BankAccountRepository {

    private final Map<Integer, BankAccount> storage = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public BankAccount save(BankAccount account) {
        if (account.getId() == null) {
            // Новый счет — генерируем ID
            BankAccount newAccount = new BankAccount(
                    idGenerator.getAndIncrement(),
                    account.getName()
            );
            newAccount.setBalance(account.getBalance());
            storage.put(newAccount.getId(), newAccount);
            return newAccount;
        } else {
            // Существующий или с заданным ID
            storage.put(account.getId(), account);
            // Обновляем генератор, если нужно
            if (account.getId() >= idGenerator.get()) {
                idGenerator.set(account.getId() + 1);
            }
            return account;
        }
    }

    @Override
    public Optional<BankAccount> findById(Integer id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<BankAccount> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(Integer id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return storage.containsKey(id);
    }
}
