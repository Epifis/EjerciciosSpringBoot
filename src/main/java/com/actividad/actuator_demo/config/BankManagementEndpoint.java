package com.actividad.actuator_demo.config;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import com.actividad.actuator_demo.repository.AccountRepository;
import com.actividad.actuator_demo.repository.TransactionRepository;

@Component
@Endpoint(id = "bank-stats")
public class BankManagementEndpoint {
    
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    
    public BankManagementEndpoint(AccountRepository accountRepository,
                                TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }
    
    @ReadOperation
    public Map<String, Object> getBankStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        BigDecimal totalBalance = accountRepository.getTotalBankBalance()
                .orElse(BigDecimal.ZERO);
        Long totalAccounts = accountRepository.count();
        Long failedTransactions = transactionRepository.countFailedTransactions();
        BigDecimal totalTransferred = transactionRepository.getTotalTransferredAmount()
                .orElse(BigDecimal.ZERO);
        
        stats.put("totalBalance", totalBalance);
        stats.put("totalAccounts", totalAccounts);
        stats.put("failedTransactions", failedTransactions);
        stats.put("totalTransferredAmount", totalTransferred);
        stats.put("timestamp", System.currentTimeMillis());
        stats.put("status", totalAccounts > 0 ? "OPERATIONAL" : "NO_ACCOUNTS");
        
        return stats;
    }
    
    @WriteOperation
    public Map<String, String> resetDemoData() {
        // En un caso real, esto sería más complejo
        Map<String, String> result = new HashMap<>();
        result.put("message", "Demo reset functionality would go here");
        result.put("status", "INFO");
        return result;
    }
}