package org.example.currencyExchanger.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Currency {
    private int id;
    private String code;
    private String name;
    private String sign;
}
