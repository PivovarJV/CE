package org.example.currencyExchanger.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyExchanger.exception.DataAccessException;
import org.example.currencyExchanger.exception.ErrorResponse;
import org.example.currencyExchanger.model.Currency;
import org.example.currencyExchanger.service.AnswersErrors;
import org.example.currencyExchanger.service.CurrencyService;
import org.example.currencyExchanger.service.JsonConverter;

import java.io.IOException;
import java.io.PrintWriter;

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
