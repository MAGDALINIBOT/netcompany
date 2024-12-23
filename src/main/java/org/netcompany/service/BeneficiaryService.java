package org.netcompany.service;


import com.opencsv.bean.CsvToBeanBuilder;
import org.netcompany.model.Beneficiary;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.util.List;

@Service
public class BeneficiaryService {

    private List<Beneficiary> beneficiaries;
    private static final String FILE_ROUTE = "src/main/resources/files/beneficiaries.csv";

    public BeneficiaryService() {
        loadBeneficiaries();
    }

    /**
     * Loads beneficiaries from a CSV file and parses them into a list of Beneficiary objects.
     */
    private void loadBeneficiaries() {
        try {
            beneficiaries = new CsvToBeanBuilder<Beneficiary>(new FileReader(FILE_ROUTE))
                    .withType(Beneficiary.class)
                    .build()
                    .parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a beneficiary by their unique ID.
     *
     * @param id The ID of the beneficiary.
     * @return The Beneficiary object with the matching ID, or null if no such beneficiary exists.
     */
    public Beneficiary getBeneficiaryById(Long id) {
        return beneficiaries.stream()
                .filter(b -> b.getBeneficiaryId().equals(id))
                .findFirst()
                .orElse(null);
    }

}

