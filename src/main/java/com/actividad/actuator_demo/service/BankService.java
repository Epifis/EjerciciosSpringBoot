package com.actividad.actuator_demo.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.actividad.actuator_demo.config.BankMetrics;
import com.actividad.actuator_demo.model.Account;
import com.actividad.actuator_demo.model.Transaction;
import com.actividad.actuator_demo.repository.AccountRepository;
import com.actividad.actuator_demo.repository.TransactionRepository;

import io.micrometer.core.instrument.Timer;

@Service
public class BankService {
    
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BankMetrics bankMetrics;
    
    public BankService(AccountRepository accountRepository, 
                      TransactionRepository transactionRepository,
                      BankMetrics bankMetrics) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.bankMetrics = bankMetrics;
    }
    
    @Transactional
    public Transaction transferMoney(String fromAccountNumber, 
                                   String toAccountNumber, 
                                   BigDecimal amount) {
        
        // Usar el timer para medir la duración
        Timer.Sample sample = Timer.start();
        
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El monto debe ser positivo");
            }
            
            Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                    .orElseThrow(() -> new RuntimeException("Cuenta origen no encontrada"));
            
            Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                    .orElseThrow(() -> new RuntimeException("Cuenta destino no encontrada"));
            
            // Verificar fondos suficientes
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                Transaction failedTx = new Transaction(fromAccount, toAccount, amount, "TRANSFER");
                failedTx.setStatus("FAILED");
                bankMetrics.recordFailedTransaction();
                return transactionRepository.save(failedTx);
            }
            
            // Realizar transferencia
            fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
            toAccount.setBalance(toAccount.getBalance().add(amount));
            
            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);
            
            Transaction transaction = new Transaction(fromAccount, toAccount, amount, "TRANSFER");
            transaction.setStatus("COMPLETED");
            
            bankMetrics.recordSuccessfulTransaction();
            return transactionRepository.save(transaction);
            
        } finally {
            sample.stop(bankMetrics.getTransactionTimer());
        }
    }
    
    @Transactional
    public Account deposit(String accountNumber, BigDecimal amount) {
        Timer.Sample sample = Timer.start();
        
        try {
            Account account = accountRepository.findByAccountNumber(accountNumber)
                    .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
            
            account.setBalance(account.getBalance().add(amount));
            
            Transaction transaction = new Transaction(null, account, amount, "DEPOSIT");
            transaction.setStatus("COMPLETED");
            transactionRepository.save(transaction);
            
            bankMetrics.recordSuccessfulTransaction();
            return accountRepository.save(account);
            
        } finally {
            sample.stop(bankMetrics.getTransactionTimer());
        }
    }
    
    public BigDecimal getTotalBankBalance() {
        return accountRepository.getTotalBankBalance().orElse(BigDecimal.ZERO);
    }
    
    public Long getTotalAccounts() {
        return accountRepository.count();
    }
    
    // Método para actualizar métricas de cuentas activas
    public void updateAccountsMetrics() {
        Long accountsWithBalance = accountRepository.countAccountsWithBalance();
        bankMetrics.updateActiveAccounts(accountsWithBalance.intValue());
    }
}