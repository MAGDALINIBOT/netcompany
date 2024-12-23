package org.netcompany.service;

import com.opencsv.bean.CsvToBeanBuilder;
import org.netcompany.model.Account;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.util.List;

@Service
public class AccountService {

    private List<Account> accounts;
    private static final String FILE_ROUTE = "src/main/resources/files/accounts.csv";

    public AccountService() {
        loadAccounts();
    }

    /**
     * Loads accounts from a CSV file and parses them into a list of Account objects.
     */
    private void loadAccounts() {
        try {
            accounts = new CsvToBeanBuilder<Account>(new FileReader(FILE_ROUTE))
                    .withType(Account.class)
                    .build()
                    .parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a list of accounts associated with a given beneficiary ID.
     *
     * @param beneficiaryId The ID of the beneficiary.
     * @return A list of accounts related to the specified beneficiary.
     */
    public List<Account> getAccountsByBeneficiaryId(Long beneficiaryId) {
        return accounts.stream()
                .filter(account -> account.getBeneficiaryId().equals(beneficiaryId))
                .toList();
    }
}
