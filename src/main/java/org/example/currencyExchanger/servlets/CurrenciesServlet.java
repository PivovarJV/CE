package org.example.currencyExchanger.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyExchanger.dao.ExceptionDatabase;
import org.example.currencyExchanger.exception.ErrorResponse;
import org.example.currencyExchanger.model.Currency;
import org.example.currencyExchanger.service.AnswersErrors;
import org.example.currencyExchanger.service.CurrencyService;
import org.example.currencyExchanger.service.JsonConverter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyService currencyService = new CurrencyService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String nameCurrency = req.getParameter("name");
            String codeCurrency = req.getParameter("code");
            String singCurrency = req.getParameter("sign");

            if (nameCurrency.trim().isEmpty() || codeCurrency.trim().isEmpty() || singCurrency.trim().isEmpty()) {
                AnswersErrors.errorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Отсутствует нужное поле формы");
                return;
            }
            Currency currency = currencyService.addCurrency(nameCurrency, codeCurrency, singCurrency);
            resp.getWriter().print(JsonConverter.transformation(currency));

        } catch (ExceptionDatabase e) {
            if (e.getMessage().contains("существует")) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
            }
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            resp.getWriter().print(JsonConverter.transformation(errorResponse));
        } catch (Exception e) {
            AnswersErrors.errorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при добавлении валюты");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Currency> currencies = currencyService.getAllCurrencies();
            response.getWriter().print(JsonConverter.transformation(currencies));
        } catch (Exception e) {
            AnswersErrors.errorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка :(");
        }
    }
}