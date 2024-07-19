package org.example.currencyExchanger.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyExchanger.exception.ErrorResponse;

import java.io.IOException;

public class AnswersErrors {
    public static void errorResponse(HttpServletResponse resp, int codeError, String message) throws IOException {
        resp.setStatus(codeError);
        ErrorResponse errorResponse = new ErrorResponse(message);
        resp.getWriter().print(JsonConverter.transformation(errorResponse));
    }
}
