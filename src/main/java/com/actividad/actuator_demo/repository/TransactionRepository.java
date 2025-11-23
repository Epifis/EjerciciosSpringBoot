package com.actividad.actuator_demo.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.actividad.actuator_demo.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByStatus(String status);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.status = 'COMPLETED' AND t.type = 'TRANSFER'")
    Optional<BigDecimal> getTotalTransferredAmount();
    
    // ✅ CORRECCIÓN: Cambiar el tipo de retorno a Long
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.status = 'FAILED'")
    Long countFailedTransactions();
    
    // Método adicional útil para métricas
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.status = 'COMPLETED'")
    Long countSuccessfulTransactions();
}