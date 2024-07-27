package org.example.currencyexchanger.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyexchanger.dto.CurrencyExchangeDTO;
import org.example.currencyexchanger.exception.CurrencyExchangeException;
import org.example.currencyexchanger.exception.CurrencyNotFoundException;
import org.example.currencyexchanger.exception.DataAccessException;
import org.example.currencyexchanger.service.AnswersErrors;
import org.example.currencyexchanger.service.CurrencyExchangeService;
import org.example.currencyexchanger.service.CurrencyService;
import org.example.currencyexchanger.service.JsonConverter;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private CurrencyService currencyService = new CurrencyService();
    private CurrencyExchangeService currencyExchangeService = new CurrencyExchangeService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String baseCurrency = req.getParameter("from");
            String targetCurrency = req.getParameter("to");
            String amount = req.getParameter("amount");

            if (baseCurrency.trim().isEmpty() || targetCurrency.trim().isEmpty() || amount.trim().isEmpty()) {
                AnswersErrors.errorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Отсутствует нужное поле формы");
                return;
            }

            BigDecimal amountDecimal = new BigDecimal(amount);

            if (currencyService.getExchangeRates(baseCurrency, targetCurrency) != null) {
                CurrencyExchangeDTO currencyExchangeDTO = currencyExchangeService.exchangeDirect(amountDecimal, baseCurrency, targetCurrency);
                resp.getWriter().print(JsonConverter.transformation(currencyExchangeDTO));
            } else if (currencyService.getExchangeRates(targetCurrency, baseCurrency) != null) {
                CurrencyExchangeDTO currencyExchangeDTO = currencyExchangeService.exchangeReverse(baseCurrency, targetCurrency, amountDecimal);
                resp.getWriter().print(JsonConverter.transformation(currencyExchangeDTO));
            } else {
                CurrencyExchangeDTO currencyExchangeDTO = currencyExchangeService.exchangeCross(baseCurrency, targetCurrency, amountDecimal);
                resp.getWriter().print(JsonConverter.transformation(currencyExchangeDTO));
            }
        } catch (CurrencyNotFoundException e) {
            AnswersErrors.errorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Одна (или обе) валюты не существует в БД");
        } catch (CurrencyExchangeException e) {
            AnswersErrors.errorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Обмен не возможен. Нет прямого/обратного/кроcс курса");
        } catch (DataAccessException e) {
            AnswersErrors.errorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка :(");
        }
    }
}
