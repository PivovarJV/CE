package org.example.currencyexchanger.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyexchanger.exception.DataAccessException;
import org.example.currencyexchanger.exception.DuplicateCurrencyCodeException;
import org.example.currencyexchanger.model.Currency;
import org.example.currencyexchanger.service.AnswersErrors;
import org.example.currencyexchanger.service.CurrencyService;
import org.example.currencyexchanger.service.JsonConverter;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyService currencyService = new CurrencyService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nameCurrency = req.getParameter("name");
        String codeCurrency = req.getParameter("code");
        String singCurrency = req.getParameter("sign");

        if (nameCurrency.trim().isEmpty() || codeCurrency.trim().isEmpty() || singCurrency.trim().isEmpty()) {
            AnswersErrors.errorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Отсутствует нужное поле формы");
            return;
        }

        if (codeCurrency.length() != 3) {
            AnswersErrors.errorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "CODE валюты должен содержать 3 символа");
            return;
        }

        try {
            Currency currency = currencyService.addCurrency(nameCurrency, codeCurrency, singCurrency);
            resp.getWriter().print(JsonConverter.transformation(currency));

        } catch (DuplicateCurrencyCodeException e) {
            AnswersErrors.errorResponse(resp, HttpServletResponse.SC_CONFLICT, "Валютная пара с таким кодом уже существует");
        } catch (NullPointerException e) {
            AnswersErrors.errorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Не удалось добавить валюту в БД");
        } catch (DataAccessException e) {
            AnswersErrors.errorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при добавлении валюты");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Currency> currencies = currencyService.getAllCurrencies();
            response.getWriter().print(JsonConverter.transformation(currencies));
        } catch (DataAccessException e) {
            AnswersErrors.errorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка :(");
        }
    }
}