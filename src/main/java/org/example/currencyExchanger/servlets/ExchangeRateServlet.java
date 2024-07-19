package org.example.currencyExchanger.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyExchanger.exception.ErrorResponse;
import org.example.currencyExchanger.model.ExchangeRates;
import org.example.currencyExchanger.service.AnswersErrors;
import org.example.currencyExchanger.service.CurrencyExchangeService;
import org.example.currencyExchanger.service.CurrencyService;
import org.example.currencyExchanger.service.JsonConverter;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends PatchServlet {
    CurrencyService currencyService = new CurrencyService();
    CurrencyExchangeService currencyExchangeService = new CurrencyExchangeService();

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            StringBuilder stringBuilder = currencyExchangeService.readingRequestBody(req);
            String rate = currencyExchangeService.parsingRequestBody(stringBuilder);

            if (rate == null || rate.trim().isEmpty()) {
                AnswersErrors.errorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Отсутствует нужное поле формы");
                return;
            }

            BigDecimal rateDecimal = new BigDecimal(rate);

            if (rateDecimal.signum() < 0) {
                AnswersErrors.errorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Курс должен быть положительным");
                return;
            }

            String pathInfo = req.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                AnswersErrors.errorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Коды валют пары отсутствуют в адресе");
                return;
            }

            String baseCode = pathInfo.substring(1, 4);
            String targetCode = pathInfo.substring(4);
            ExchangeRates exchange = currencyService.getExchangeRates(baseCode, targetCode);

            if (exchange == null) {
                AnswersErrors.errorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Обменнеый курс для пары не найден");
                return;
            }

            int idBase = currencyService.getIdCurrency(baseCode);
            int idTarget = currencyService.getIdCurrency(targetCode);
            currencyService.updatingExchangeRate(idBase, idTarget, rateDecimal);
            ExchangeRates exchangeRates = currencyService.getExchangeRates(baseCode, targetCode);
            resp.getWriter().print(JsonConverter.transformation(exchangeRates));

        } catch (Exception e) {
            AnswersErrors.errorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка :(");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                AnswersErrors.errorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Коды валют пары отсутствуют в адресе");
                return;
            }
            String baseCode = pathInfo.substring(1, 4);
            String targetCode = pathInfo.substring(4);
            ExchangeRates exchangeRates = currencyService.getExchangeRates(baseCode, targetCode);
            if (exchangeRates == null) {
                AnswersErrors.errorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Обменный курс для пары не найден");
                return;
            }

            PrintWriter pw = resp.getWriter();
            pw.print(JsonConverter.transformation(exchangeRates));
        } catch (Exception e) {
            AnswersErrors.errorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка :(");
        }
    }
}