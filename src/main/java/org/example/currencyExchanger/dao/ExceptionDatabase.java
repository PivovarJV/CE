package org.example.currencyExchanger.dao;

public class ExceptionDatabase extends RuntimeException{
    public ExceptionDatabase(String message){
        super(message);
    }
    public ExceptionDatabase(String message, Throwable cause){
        super(message, cause);
    }
}
