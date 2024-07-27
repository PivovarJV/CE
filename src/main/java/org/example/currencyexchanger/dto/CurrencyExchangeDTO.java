package org.example.currencyexchanger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.currencyexchanger.model.Currency;

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