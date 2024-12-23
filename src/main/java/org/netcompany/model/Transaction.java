package org.netcompany.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private Long transactionId;
    private Long accountId;
    private Double amount;
    private String type;
    private String date;
}
