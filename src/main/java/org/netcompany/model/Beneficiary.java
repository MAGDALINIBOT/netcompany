package org.netcompany.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Beneficiary {
    private Long beneficiaryId;
    private String firstName;
    private String lastName;
}
