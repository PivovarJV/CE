package org.example.currencyexchanger.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.currencyexchanger.dto.CurrencyExchangeDTO;
import org.example.currencyexchanger.exception.ErrorResponse;
import org.example.currencyexchanger.model.Currency;
import org.example.currencyexchanger.model.ExchangeRates;

import java.math.BigDecimal;
import java.util.List;

public class JsonConverter {
    public static <T> String transformation(List<T> currencies) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(currencies);
    }

    public static String transformation(BigDecimal exchangeRates) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(exchangeRates);
    }

    public static String transformation(Currency currency) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(currency);
    }

    public static String transformation(ExchangeRates exchangeRates) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(exchangeRates);
    }

    public static String transformation(CurrencyExchangeDTO currencyExchangeDTO) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(currencyExchangeDTO);
    }

    public static String transformation(ErrorResponse errorResponse) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(errorResponse);
    }
}
