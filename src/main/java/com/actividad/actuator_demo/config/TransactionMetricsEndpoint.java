package com.actividad.actuator_demo.config;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint; // ✅ IMPORTACIÓN FALTANTE
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import com.actividad.actuator_demo.repository.AccountRepository;
import com.actividad.actuator_demo.repository.TransactionRepository;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Component
@Endpoint(id = "transaction-metrics")
public class TransactionMetricsEndpoint {

    private final MeterRegistry meterRegistry;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionMetricsEndpoint(MeterRegistry meterRegistry,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository) {
        this.meterRegistry = meterRegistry;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @ReadOperation
    public Map<String, Object> getTransactionMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        // Obtener métricas de Micrometer
        double successCount = meterRegistry.counter("bank.transactions.success").count();
        double failedCount = meterRegistry.counter("bank.transactions.failed").count();
        double totalTransactions = successCount + failedCount;
        double successRate = totalTransactions > 0 ? (successCount / totalTransactions) * 100 : 0;

        // Obtener datos directamente del repositorio para mayor precisión
        Long actualSuccessful = transactionRepository.countSuccessfulTransactions();
        Long actualFailed = transactionRepository.countFailedTransactions();
        BigDecimal totalBalance = accountRepository.getTotalBankBalance().orElse(BigDecimal.ZERO);

        metrics.put("successfulTransactions", actualSuccessful);
        metrics.put("failedTransactions", actualFailed);
        metrics.put("totalTransactions", actualSuccessful + actualFailed);
        metrics.put("successRate", Math.round(successRate * 100.0) / 100.0);
        metrics.put("totalBalance", totalBalance);
        metrics.put("activeAccounts", accountRepository.countAccountsWithBalance());

        Timer transactionTimer = meterRegistry.find("bank.transactions.duration").timer();
        if (transactionTimer != null) {
            metrics.put("avgTransactionTimeMs", Math.round(transactionTimer.mean(java.util.concurrent.TimeUnit.MILLISECONDS)));
            metrics.put("maxTransactionTimeMs", Math.round(transactionTimer.max(java.util.concurrent.TimeUnit.MILLISECONDS)));
            metrics.put("transactionCount", transactionTimer.count());
        } else {
            metrics.put("avgTransactionTimeMs", 0);
            metrics.put("maxTransactionTimeMs", 0);
            metrics.put("transactionCount", 0);
        }

        // Métricas adicionales del sistema
        metrics.put("timestamp", System.currentTimeMillis());
        metrics.put("totalAccounts", accountRepository.count());

        return metrics;
    }
}
