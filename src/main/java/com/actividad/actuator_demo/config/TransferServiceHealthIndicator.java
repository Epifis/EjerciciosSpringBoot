package com.actividad.actuator_demo.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TransferServiceHealthIndicator implements HealthIndicator {
    
    private boolean transferServiceAvailable = true;
    
    @Override
    public Health health() {
        //Implementar la lógica del health check
        
        // INSTRUCCIONES:
        // 1. Si transferServiceAvailable es true, retornar Health.up()
        // 2. Si es false, retornar Health.down()
        // 3. Agregar un detail con key "service" y value "TransferService"
        // 4. Agregar otro detail con key "checkedAt" y el timestamp actual
        
        // EJEMPLO de retorno cuando está UP:
        // return Health.up()
        //     .withDetail("service", "TransferService")
        //     .withDetail("checkedAt", System.currentTimeMillis())
        //     .build();
        
        // EJEMPLO de retorno cuando está DOWN:
        // return Health.down()
        //     .withDetail("service", "TransferService")
        //     .withDetail("checkedAt", System.currentTimeMillis())
        //     .build();
        
        // IMPLEMENTA LA SOLUCIÓN AQUÍ:
    }
    
    // Método para simular un cambio en el estado (para pruebas)
    public void setTransferServiceAvailable(boolean available) {
        this.transferServiceAvailable = available;
    }
}