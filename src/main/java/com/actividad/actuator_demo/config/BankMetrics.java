package com.actividad.actuator_demo.config;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.actividad.actuator_demo.repository.AccountRepository;
import com.actividad.actuator_demo.repository.TransactionRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Component
public class BankMetrics {
    
    private final MeterRegistry meterRegistry;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    
    private final Counter successfulTransactions;
    private final Counter failedTransactions;
    private final AtomicInteger activeAccounts;
    private final Timer transactionTimer;
    
    public BankMetrics(MeterRegistry meterRegistry,
                      AccountRepository accountRepository,
                      TransactionRepository transactionRepository) {
        this.meterRegistry = meterRegistry;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        
        // Configurar contadores
        this.successfulTransactions = Counter.builder("bank.transactions.success")
            .description("Número de transacciones exitosas")
            .register(meterRegistry);
            
        this.failedTransactions = Counter.builder("bank.transactions.failed")
            .description("Número de transacciones fallidas")
            .register(meterRegistry);
            
        this.activeAccounts = new AtomicInteger(0);
        
        this.transactionTimer = Timer.builder("bank.transactions.duration")
            .description("Tiempo de procesamiento de transacciones")
            .register(meterRegistry);
    }
    
    @PostConstruct
    public void initMetrics() {
        // Gauge para cuentas activas
        Gauge.builder("bank.accounts.active", activeAccounts, AtomicInteger::get)
            .description("Número de cuentas activas")
            .register(meterRegistry);
            
        // ✅ CORRECCIÓN: Gauge para balance total con conversión correcta
        Gauge.builder("bank.balance.total", accountRepository, 
                     repo -> {
                         BigDecimal balance = repo.getTotalBankBalance().orElse(BigDecimal.ZERO);
                         return balance.doubleValue(); // Convertir BigDecimal a double
                     })
            .description("Balance total del banco")
            .register(meterRegistry);
            
        // Gauge adicional para transacciones fallidas
        Gauge.builder("bank.transactions.failed.count", transactionRepository,
                     repo -> repo.countFailedTransactions().doubleValue())
            .description("Contador de transacciones fallidas")
            .register(meterRegistry);
    }
    
    public void recordSuccessfulTransaction() {
        successfulTransactions.increment();
    }
    
    public void recordFailedTransaction() {
        failedTransactions.increment();
    }
    
    public void updateActiveAccounts(int count) {
        activeAccounts.set(count);
    }
    
    public Timer getTransactionTimer() {
        return transactionTimer;
    }
    
    // Método auxiliar para obtener métricas actuales
    public Map<String, Object> getCurrentMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("successfulTransactions", successfulTransactions.count());
        metrics.put("failedTransactions", failedTransactions.count());
        metrics.put("activeAccounts", activeAccounts.get());
        return metrics;
    }
}