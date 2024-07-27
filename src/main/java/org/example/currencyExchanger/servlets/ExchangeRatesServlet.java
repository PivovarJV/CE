package org.example.currencyExchanger.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyExchanger.exception.DataAccessException;
import org.example.currencyExchanger.exception.ErrorResponse;
import org.example.currencyExchanger.model.ExchangeRates;
import org.example.currencyExchanger.service.AnswersErrors;
import org.example.currencyExchanger.service.CurrencyService;
import org.example.currencyExchanger.service.JsonConverter;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private CurrencyService currencyService = new CurrencyService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String baseCurrency = req.getParameter("baseCurrencyCode");
            String targetCurrency = req.getParameter("targetCurrencyCode");
            String rate = req.getParameter("rate");
            BigDecimal rateDecimal = new BigDecimal(rate);

            if (baseCurrency.trim().isEmpty() || targetCurrency.trim().isEmpty() || rate.trim().isEmpty()) {
                AnswersErrors.errorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Отсутствует нужное поле формы");
                return;
            }

            int idBaseCurrency = currencyService.getIdCurrency(baseCurrency);
            int idTargetCurrency = currencyService.getIdCurrency(targetCurrency);

            if (idBaseCurrency == 0 || idTargetCurrency == 0) {
                AnswersErrors.errorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Одна (или обе) валюта из валютной пары не существует в БД");
                return;
            }
            if (currencyService.getExchangeRates(baseCurrency, targetCurrency) != null) {
                AnswersErrors.errorResponse(resp, HttpServletResponse.SC_CONFLICT, "Валютная пара с таким кодом уже существует");
                return;
            }
            currencyService.addExchangeRates(idBaseCurrency, idTargetCurrency, rateDecimal);
            ExchangeRates exchangeRates = currencyService.getExchangeRates(baseCurrency, targetCurrency);
            resp.getWriter().print(JsonConverter.transformation(exchangeRates));
        } catch (DataAccessException e) {
            AnswersErrors.errorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при добавлении валютного курса");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<ExchangeRates> exchangeRatesList = currencyService.getAllExchangeRates();
            response.getWriter().print(JsonConverter.transformation(exchangeRatesList));
        } catch (DataAccessException e) {
            AnswersErrors.errorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка :(");
        }
    }
}