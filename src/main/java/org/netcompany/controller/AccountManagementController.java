package org.netcompany.controller;

import lombok.RequiredArgsConstructor;
import org.netcompany.model.Account;
import org.netcompany.model.Beneficiary;
import org.netcompany.model.Transaction;
import org.netcompany.service.AccountService;
import org.netcompany.service.BeneficiaryService;
import org.netcompany.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class AccountManagementController {
    private final AccountService accountService;
    private final BeneficiaryService beneficiaryService;
    private final TransactionService transactionService;


    /**
     * 1. Retrieve the details of a beneficiary.
     *
     * @param beneficiaryId the ID of the beneficiary
     * @return the beneficiary details
     */
    @GetMapping("/beneficiaries/{beneficiaryId}")
    public Beneficiary getBeneficiary(@PathVariable Long beneficiaryId) {
        return beneficiaryService.getBeneficiaryById(beneficiaryId);
    }

    /**
     * 2. Retrieve the accounts of a beneficiary.
     *
     * @param beneficiaryId the ID of the beneficiary
     * @return a list of accounts associated with the beneficiary
     */
    @GetMapping("/beneficiaries/accounts/{beneficiaryId}")
    public List<Account> getBeneficiaryAccount(@PathVariable Long beneficiaryId) {
        return accountService.getAccountsByBeneficiaryId(beneficiaryId);
    }

    /**
     * 3. Retrieve the transactions of a beneficiary.
     *
     * @param beneficiaryId the ID of the beneficiary
     * @return a list of transactions associated with the beneficiary
     */
    @GetMapping("/beneficiaries/transactions/{beneficiaryId}")
    public List<Transaction> getTransactions(@PathVariable Long beneficiaryId) {
        return transactionService.getTransactionsByBeneficiaryId(beneficiaryId);
    }

    /**
     * 4. Retrieve the balance of a beneficiary's accounts.
     *
     * @param beneficiaryId the ID of the beneficiary
     * @return the total balance of the beneficiary's accounts
     */
    @GetMapping("/beneficiaries/balance/{beneficiaryId}")
    public double getBalance(@PathVariable Long beneficiaryId) {
        return transactionService.getBalanceByBeneficiary(beneficiaryId);
    }



    /**
     * 5. Retrieve the largest withdrawal of a beneficiary in the last month.
     *
     * @param beneficiaryId the ID of the beneficiary
     * @return the transaction representing the largest withdrawal
     */
    @GetMapping("/beneficiaries/maxWithdrawal/{beneficiaryId}")
    public ResponseEntity<?> getLargestWithdrawal(@PathVariable Long beneficiaryId) {
        try {
            Transaction transaction = transactionService.getLargestWithdrawalLastMonth(beneficiaryId);
            return ResponseEntity.ok(transaction);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

}
