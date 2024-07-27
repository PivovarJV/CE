package org.example.currencyExchanger.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.currencyExchanger.dto.CurrencyExchangeDTO;
import org.example.currencyExchanger.exception.CurrencyExchangeException;
import org.example.currencyExchanger.exception.CurrencyNotFoundException;
import org.example.currencyExchanger.model.Currency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;

public class CurrencyExchangeService {
    CurrencyService currencyService = new CurrencyService();

    public CurrencyExchangeDTO exchangeDirect(BigDecimal amountDecimal, String baseCurrency, String targetCurrency) throws CurrencyNotFoundException {
        int baseId = currencyService.getIdCurrency(baseCurrency);
        int targetId = currencyService.getIdCurrency(targetCurrency);

        if (baseId == 0 || targetId == 0) {
            throw new CurrencyNotFoundException();
        }

        BigDecimal rate = currencyService.getRate(baseId, targetId);
        BigDecimal convertedAmount = amountDecimal.multiply(rate).setScale(2, BigDecimal.ROUND_HALF_UP);
        Currency base = currencyService.getCurrency(baseCurrency);
        Currency target = currencyService.getCurrency(targetCurrency);

        CurrencyExchangeDTO currencyExchangeDTO = new CurrencyExchangeDTO(base, target, rate, amountDecimal, convertedAmount);
        return currencyExchangeDTO;
    }

    public CurrencyExchangeDTO exchangeReverse(String baseCurrency, String targetCurrency, BigDecimal amountDecimal) throws CurrencyNotFoundException {
        int baseId = currencyService.getIdCurrency(baseCurrency);
        int targetId = currencyService.getIdCurrency(targetCurrency);

        if (baseId == 0 || targetId == 0) {
            throw new CurrencyNotFoundException();
        }

        BigDecimal rate = currencyService.getRate(targetId, baseId);
        BigDecimal convertedAmount = amountDecimal.divide(rate,2, BigDecimal.ROUND_HALF_UP);
        Currency currencyBase = currencyService.getCurrency(baseCurrency);
        Currency currencyTarget = currencyService.getCurrency(targetCurrency);

        CurrencyExchangeDTO currencyExchangeDTO = new CurrencyExchangeDTO(currencyBase, currencyTarget, rate, amountDecimal, convertedAmount);
        return currencyExchangeDTO;
    }

    public CurrencyExchangeDTO exchangeCross(String baseCurrency, String targetCurrency, BigDecimal amountDecimal) throws CurrencyNotFoundException, CurrencyExchangeException {
        int idUSD = currencyService.getIdCurrency("USD");
        int baseId = currencyService.getIdCurrency(baseCurrency);
        int targetId = currencyService.getIdCurrency(targetCurrency);

        if (baseId == 0 || targetId == 0) {
            throw new CurrencyNotFoundException();
        }

        BigDecimal baseRate = currencyService.getRate(idUSD, baseId);
        BigDecimal targetRate = currencyService.getRate(idUSD, targetId);
        if (baseRate == null || targetRate == null) {
            throw new CurrencyExchangeException();
        }
        BigDecimal rate = targetRate.divide(baseRate, 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal convertedAmount = rate.multiply(amountDecimal).setScale(2, BigDecimal.ROUND_HALF_UP);
        Currency currencyBase = currencyService.getCurrency(baseCurrency);
        Currency currencyTarget = currencyService.getCurrency(targetCurrency);

        CurrencyExchangeDTO currencyExchangeDTO = new CurrencyExchangeDTO(currencyBase, currencyTarget, rate, amountDecimal, convertedAmount);
        return currencyExchangeDTO;
    }

    public StringBuilder readingRequestBody(HttpServletRequest req) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder;
    }

    public String parsingRequestBody(StringBuilder stringBuilder) throws UnsupportedEncodingException {
        String reqBody = stringBuilder.toString();
        String[] params = reqBody.split("&");
        String rate = null;
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && keyValue[0].equals("rate")) {
                rate = URLDecoder.decode(keyValue[1], "UTF-8");
                break;
            }
        }
        return rate;
    }
}
