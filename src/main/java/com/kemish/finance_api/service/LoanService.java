package com.kemish.finance_api.service;

import com.kemish.finance_api.entity.Customer;
import com.kemish.finance_api.entity.Loan;
import com.kemish.finance_api.entity.LoanPayment;
import com.kemish.finance_api.repository.LoanPaymentRepository;
import com.kemish.finance_api.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final LoanPaymentRepository loanPaymentRepository;
    private final CustomerService customerService;

    public LoanService(LoanRepository loanRepository,
                       LoanPaymentRepository loanPaymentRepository,
                       CustomerService customerService) {
        this.loanRepository = loanRepository;
        this.loanPaymentRepository = loanPaymentRepository;
        this.customerService = customerService;
    }

    @Transactional
    public Loan applyForLoan(Long customerId, BigDecimal amount,
                             BigDecimal interestRate, Integer termMonths) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Loan amount must be positive");
        }

        Customer customer = customerService.getCustomerById(customerId);

        // Check if customer already has an active loan
        List<Loan> activeLoans = loanRepository
                .findByCustomerIdAndStatus(customerId, "ACTIVE");
        if (!activeLoans.isEmpty()) {
            throw new RuntimeException("Customer already has an active loan");
        }

        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setAmount(amount);
        loan.setInterestRate(interestRate);
        loan.setTermMonths(termMonths);

        return loanRepository.save(loan);
    }

    public Loan getLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + id));
    }

    public List<Loan> getLoansByCustomer(Long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    @Transactional
    public LoanPayment makePayment(Long loanId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Payment amount must be positive");
        }

        Loan loan = getLoanById(loanId);

        if (loan.getStatus().equals("PAID_OFF")) {
            throw new RuntimeException("Loan is already paid off");
        }

        if (amount.compareTo(loan.getRemainingBalance()) > 0) {
            amount = loan.getRemainingBalance(); // cap at remaining balance
        }

        // Update remaining balance
        BigDecimal newBalance = loan.getRemainingBalance().subtract(amount);
        loan.setRemainingBalance(newBalance);

        // Mark as paid off if balance reaches zero
        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus("PAID_OFF");
        }

        loanRepository.save(loan);

        // Record the payment
        LoanPayment payment = new LoanPayment();
        payment.setLoan(loan);
        payment.setAmount(amount);
        payment.setBalanceAfter(newBalance);
        loanPaymentRepository.save(payment);

        return payment;
    }

    public List<LoanPayment> getPaymentHistory(Long loanId) {
        getLoanById(loanId); // verify loan exists
        return loanPaymentRepository.findByLoanIdOrderByCreatedAtDesc(loanId);
    }
}