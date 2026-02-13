package ru.tigerbank.service.account;

import org.springframework.stereotype.Service;
import ru.tigerbank.domain.BankAccount;
import ru.tigerbank.domain.Operation;
import ru.tigerbank.domain.OperationType;
import ru.tigerbank.repository.BankAccountRepository;
import ru.tigerbank.repository.OperationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository accountRepository;
    private final OperationRepository operationRepository;

    public BankAccountServiceImpl(
            BankAccountRepository accountRepository,
            OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    @Override
    public BankAccount createAccount(String name) {
        BankAccount account = new BankAccount(null, name);
        return accountRepository.save(account);
    }

    @Override
    public BankAccount createAccount(Integer id, String name) {
        BankAccount account = new BankAccount(id, name);
        return accountRepository.save(account);
    }

    @Override
    public Optional<BankAccount> getAccount(Integer id) {
        return accountRepository.findById(id);
    }

    @Override
    public List<BankAccount> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public BankAccount renameAccount(Integer id, String newName) {
        BankAccount account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + id));

        BankAccount renamed = new BankAccount(account.getId(), newName);
        renamed.setBalance(account.getBalance());

        return accountRepository.save(renamed);
    }

    @Override
    public void adjustBalance(Integer id, double newBalance) {
        BankAccount account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + id));

        account.setBalance(newBalance);
        accountRepository.save(account);
    }

    @Override
    public void deleteAccount(Integer id) {
        boolean hasOperations = operationRepository.existsByBankAccountId(id);
        if (hasOperations) {
            throw new IllegalStateException(
                    "Cannot delete account with id " + id + ": there are operations linked to this account"
            );
        }
        accountRepository.deleteById(id);
    }

    @Override
    public double recalculateBalance(Integer accountId) {
        List<Operation> operations = operationRepository.findByBankAccountId(accountId);

        double balance = operations.stream()
                .mapToDouble(op -> {
                    if (op.getType() == OperationType.INCOME) {
                        return op.getAmount();
                    } else {
                        return -op.getAmount();
                    }
                })
                .sum();

        adjustBalance(accountId, balance);
        return balance;
    }
}