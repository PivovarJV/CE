package org.example.currencyexchanger.dao;

public class ExceptionDatabase extends RuntimeException{
    public ExceptionDatabase(String message){
        super(message);
    }
    public ExceptionDatabase(String message, Throwable cause){
        super(message, cause);
    }
}
