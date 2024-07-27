package org.example.currencyexchanger.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyexchanger.exception.DataAccessException;
import org.example.currencyexchanger.model.Currency;
import org.example.currencyexchanger.service.AnswersErrors;
import org.example.currencyexchanger.service.CurrencyService;
import org.example.currencyexchanger.service.JsonConverter;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyService currencyService = new CurrencyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                AnswersErrors.errorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Код валюты отсутствует в адресе");
                return;
            }
            String codeCurrency = pathInfo.substring(1);
            Currency currency = currencyService.getCurrency(codeCurrency);
            if (currency == null) {
                AnswersErrors.errorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Валюта не найдена");
                return;
            }
            resp.getWriter().print(JsonConverter.transformation(currency));
        } catch (DataAccessException e) {
            AnswersErrors.errorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка :(");
        }
    }
}
