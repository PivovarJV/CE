package org.example.currencyExchanger.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyExchanger.dto.CurrencyExchangeDTO;
import org.example.currencyExchanger.exception.CurrencyExchangeException;
import org.example.currencyExchanger.exception.CurrencyNotFoundException;
import org.example.currencyExchanger.exception.ErrorResponse;
import org.example.currencyExchanger.service.AnswersErrors;
import org.example.currencyExchanger.service.CurrencyExchangeService;
import org.example.currencyExchanger.service.CurrencyService;
import org.example.currencyExchanger.service.JsonConverter;

import java.io.IOException;
import java.io.PrintWriter;
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
        } catch (Exception e) {
            AnswersErrors.errorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка :(");
        }
    }
}