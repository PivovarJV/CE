package org.example.currencyExchanger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.currencyExchanger.model.Currency;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
public class CurrencyExchangeDTO {
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;
}
