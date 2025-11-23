package com.actividad.actuator_demo.config;

import java.math.BigDecimal;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.actividad.actuator_demo.repository.AccountRepository;
import com.actividad.actuator_demo.repository.TransactionRepository;

@Component
public class BankServiceHealthIndicator implements HealthIndicator {
    
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    
    public BankServiceHealthIndicator(AccountRepository accountRepository,
                                    TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }
    
    @Override
    public Health health() {
        try {
            Long accountCount = accountRepository.count();
            Long failedTransactions = transactionRepository.countFailedTransactions();
            BigDecimal totalBalance = accountRepository.getTotalBankBalance()
                    .orElse(BigDecimal.ZERO);
            
            Health.Builder status = accountCount > 0 ? Health.up() : Health.down();
            
            return status
                .withDetail("total_accounts", accountCount)
                .withDetail("total_balance", totalBalance)
                .withDetail("failed_transactions", failedTransactions)
                .withDetail("accounts_with_balance", 
                           accountRepository.countAccountsWithBalance())
                .build();
                
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", "Service health check failed: " + e.getMessage())
                .build();
        }
    }
}