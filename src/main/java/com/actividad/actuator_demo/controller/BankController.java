package com.actividad.actuator_demo.controller;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.actividad.actuator_demo.model.Account;
import com.actividad.actuator_demo.model.Transaction;
import com.actividad.actuator_demo.service.BankService;

@RestController
@RequestMapping("/api/bank")
public class BankController {
    
    private final BankService bankService;
    
    public BankController(BankService bankService) {
        this.bankService = bankService;
    }
    
    @PostMapping("/transfer")
    public Transaction transfer(@RequestParam String fromAccount,
                              @RequestParam String toAccount,
                              @RequestParam BigDecimal amount) {
        return bankService.transferMoney(fromAccount, toAccount, amount);
    }
    
    @PostMapping("/deposit")
    public Account deposit(@RequestParam String accountNumber,
                          @RequestParam BigDecimal amount) {
        return bankService.deposit(accountNumber, amount);
    }
    
    @GetMapping("/total-balance")
    public BigDecimal getTotalBalance() {
        return bankService.getTotalBankBalance();
    }
}