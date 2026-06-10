package com.kemish.finance_api.service;

import com.kemish.finance_api.entity.Account;
import com.kemish.finance_api.entity.Customer;
import com.kemish.finance_api.entity.Transaction;
import com.kemish.finance_api.repository.AccountRepository;
import com.kemish.finance_api.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerService customerService;

    public AccountService(AccountRepository accountRepository,
                          TransactionRepository transactionRepository,
                          CustomerService customerService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.customerService = customerService;
    }

    public Account createAccount(Long customerId, String type) {
        Customer customer = customerService.getCustomerById(customerId);
        Account account = new Account();
        account.setCustomer(customer);
        account.setType(type.toUpperCase());
        return accountRepository.save(account);
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found: " + id));
    }

    public List<Account> getAccountsByCustomer(Long customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    @Transactional
    public Account deposit(Long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deposit amount must be positive");
        }

        Account account = getAccountById(accountId);
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        // Record the transaction
        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setType("DEPOSIT");
        tx.setAmount(amount);
        tx.setBalanceAfter(account.getBalance());
        tx.setDescription("Deposit of $" + amount);
        transactionRepository.save(tx);

        return account;
    }

    @Transactional
    public Account withdraw(Long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Withdrawal amount must be positive");
        }

        Account account = getAccountById(accountId);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        // Record the transaction
        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setType("WITHDRAWAL");
        tx.setAmount(amount);
        tx.setBalanceAfter(account.getBalance());
        tx.setDescription("Withdrawal of $" + amount);
        transactionRepository.save(tx);

        return account;
    }

    public List<Transaction> getTransactionHistory(Long accountId) {
        getAccountById(accountId); // verify account exists
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }
}