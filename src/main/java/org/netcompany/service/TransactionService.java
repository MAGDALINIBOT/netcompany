package org.netcompany.service;

import com.opencsv.bean.CsvToBeanBuilder;
import org.netcompany.model.Account;
import org.netcompany.model.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

@Service
public class TransactionService {

    private List<Transaction> transactions;
    private final AccountService accountService;
    private static final String FILE_ROUTE = "src/main/resources/files/transactions.csv";

    public TransactionService(AccountService accountService) {
        this.accountService = accountService;
        loadTransactions();

    }

    /**
     * Loads transactions from a CSV file and parses them into a list of Transaction objects.
     */
    private void loadTransactions() {
        try {
            transactions = new CsvToBeanBuilder<Transaction>(new FileReader(FILE_ROUTE))
                    .withType(Transaction.class)
                    .build()
                    .parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Retrieves a list of account IDs associated with a given beneficiary ID.
     *
     * @param beneficiaryId The ID of the beneficiary.
     * @return A list of account IDs associated with the beneficiary.
     */
    private List<Long> getAccountIdsByBeneficiaryId(Long beneficiaryId) {
        List<Account> accountList = accountService.getAccountsByBeneficiaryId(beneficiaryId);
        return accountList.stream()
                .map(Account::getAccountId)
                .toList();
    }

    /**
     * Retrieves a list of transactions associated with a given beneficiary ID.
     *
     * @param beneficiaryId The ID of the beneficiary.
     * @return A list of transactions related to the beneficiary's accounts.
     */
    public List<Transaction> getTransactionsByBeneficiaryId(Long beneficiaryId) {
        List<Long> accountIds = getAccountIdsByBeneficiaryId(beneficiaryId);
        return transactions.stream()
                .filter(transaction -> accountIds.contains(transaction.getAccountId()))
                .toList();
    }

    /**
     * Calculates the total balance for a given beneficiary by summing deposits
     * and subtracting withdrawals across all associated accounts.
     *
     * @param beneficiaryId The ID of the beneficiary.
     * @return The total balance for the beneficiary.
     */
    public Double getBalanceByBeneficiary(Long beneficiaryId) {
        List<Long> accountIds = getAccountIdsByBeneficiaryId(beneficiaryId);
        return transactions.stream()
                .filter(transaction -> accountIds.contains(transaction.getAccountId()))
                .mapToDouble(transaction -> {
                    if ("withdrawal".equalsIgnoreCase(transaction.getType())) {
                        return -transaction.getAmount(); // Subtract withdrawals
                    } else if ("deposit".equalsIgnoreCase(transaction.getType())) {
                        return transaction.getAmount(); // Add deposits
                    }
                    return 0; // Ignore unknown types
                })
                .sum();
    }

    /**
     * Finds the largest withdrawal made by a beneficiary in the last month.
     *
     * @param beneficiaryId The ID of the beneficiary.
     * @return The largest withdrawal transaction in the last month, or null if none exist.
     */
    public Transaction getLargestWithdrawalLastMonth(Long beneficiaryId) {
        List<Long> accountIds = getAccountIdsByBeneficiaryId(beneficiaryId);
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        List<Transaction> lastMonthTransactions = transactions.stream()
                .filter(transaction -> accountIds.contains(transaction.getAccountId()) &&
                        "withdrawal".equalsIgnoreCase(transaction.getType()) &&
                        isTransactionInLastMonth(transaction.getDate(), lastMonth)).toList();
        if (lastMonthTransactions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"There are no transactions for the last month.");
        } else {
          return   lastMonthTransactions.stream().sorted(Comparator.comparingDouble(Transaction::getAmount)).toList().getLast();
        }
    }

    /**
     * Checks if a transaction date is within the previous month.
     *
     * @param transactionDate The date of the transaction as a string.
     * @param lastMonth       The LocalDate representing the previous month.
     * @return True if the transaction occurred in the last month, false otherwise.
     */
    private boolean isTransactionInLastMonth(String transactionDate, LocalDate lastMonth) {
        LocalDate transactionLocalDate = stringToLocalDate(transactionDate);
        return transactionLocalDate != null && transactionLocalDate.getMonth().equals(lastMonth.getMonth()) &&
                transactionLocalDate.getYear() == lastMonth.getYear();
    }

    /**
     * Converts a date string to a LocalDate object using the "MM/dd/yy" format.
     *
     * @param dateString The date as a string.
     * @return The corresponding LocalDate object, or null if parsing fails.
     */
    private LocalDate stringToLocalDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");
        try {
            return LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

}

