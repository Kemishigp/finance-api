package com.kemish.finance_api.controller;

import com.kemish.finance_api.entity.Loan;
import com.kemish.finance_api.entity.LoanPayment;
import com.kemish.finance_api.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<Loan> apply(@RequestBody Map<String, String> body) {
        Long customerId     = Long.parseLong(body.get("customerId"));
        BigDecimal amount   = new BigDecimal(body.get("amount"));
        BigDecimal rate     = new BigDecimal(body.get("interestRate"));
        Integer termMonths  = Integer.parseInt(body.get("termMonths"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(loanService.applyForLoan(customerId, amount, rate, termMonths));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Loan> getById(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Loan>> getByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(loanService.getLoansByCustomer(customerId));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<LoanPayment> pay(@PathVariable Long id,
                                           @RequestBody Map<String, String> body) {
        BigDecimal amount = new BigDecimal(body.get("amount"));
        return ResponseEntity.ok(loanService.makePayment(id, amount));
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<List<LoanPayment>> getPayments(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getPaymentHistory(id));
    }
}