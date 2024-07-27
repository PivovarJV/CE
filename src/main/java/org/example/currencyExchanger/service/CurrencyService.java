package org.example.currencyExchanger.service;

import org.example.currencyExchanger.dao.CurrencyDAO;
import org.example.currencyExchanger.dao.ExchangeRatesDAO;
import org.example.currencyExchanger.exception.DuplicateCurrencyCodeException;
import org.example.currencyExchanger.model.Currency;
import org.example.currencyExchanger.model.ExchangeRates;

import java.math.BigDecimal;
import java.util.List;

public class CurrencyService {
    private final CurrencyDAO currencyDAO = new CurrencyDAO();
    private final ExchangeRatesDAO exchangeRatesDAO = new ExchangeRatesDAO();

    public List<Currency> getAllCurrencies() {
        return currencyDAO.getAllCurrencies();
    }

    public List<ExchangeRates> getAllExchangeRates() {
        return exchangeRatesDAO.getAllExchangeRates();
    }

    public Currency getCurrency(String code) {
        return currencyDAO.getCurrency(code);
    }

    public ExchangeRates getExchangeRates(String baseCode, String targetCode) {
        return exchangeRatesDAO.getExchangeRate(baseCode, targetCode);
    }

    public Currency addCurrency(String nameCurrency, String codeCurrency, String singCurrency) throws DuplicateCurrencyCodeException {
        return currencyDAO.addCurrency(nameCurrency, codeCurrency, singCurrency);
    }

    public int getIdCurrency(String code) {
        return currencyDAO.getIdCurrency(code);
    }

    public void addExchangeRates(int baseId, int targetId, BigDecimal rate) {
        exchangeRatesDAO.addExchangeRates(baseId, targetId, rate);
    }

    public void updatingExchangeRate(int baseId, int targetId, BigDecimal rate) {
        exchangeRatesDAO.updatingExchangeRate(baseId, targetId, rate);
    }

    public BigDecimal getRate(int baseId, int targetId) {
        return exchangeRatesDAO.getRate(baseId, targetId);
    }
}
