package org.example.currencyExchanger.dao;

import org.example.currencyExchanger.exception.DataAccessException;
import org.example.currencyExchanger.exception.DuplicateCurrencyCodeException;
import org.example.currencyExchanger.model.Currency;
import org.example.currencyExchanger.service.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAO {

    public List<Currency> getAllCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        try (Connection connect = DataSource.getConnection();
            Statement statement = connect.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Currencies")) {

            while (rs.next()) {
                int id = rs.getInt("ID");
                String code = rs.getString("Code");
                String name = rs.getString("FullName");
                String sing = rs.getString("Sing");
                currencies.add(new Currency(id, code, name, sing));
            }
        } catch (SQLException e) {
            throw new DataAccessException();
        }
        return currencies;
    }

    public Currency getCurrency(String codeCurrency) {
        Currency currency = null;
        try {
            Connection connect = DataSource.getConnection();
            PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM Currencies WHERE Code = ?");
            preparedStatement.setString(1, codeCurrency);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("ID");
                String code = rs.getString("Code");
                String name = rs.getString("FullName");
                String sing = rs.getString("Sing");
                currency = new Currency(id, code, name, sing);
            }
            connect.close();
            preparedStatement.close();
            rs.close();
        } catch (SQLException e) {
            throw new DataAccessException();
        }
        return currency;
    }

    public Currency addCurrency(String nameCurrency, String codeCurrency, String singCurrency) throws DuplicateCurrencyCodeException {
        Currency currency = null;
        try {
            Connection connect = DataSource.getConnection();
            PreparedStatement preparedStatement = connect.prepareStatement("""
                    INSERT INTO Currencies (Code, FullName, Sing) VALUES (?,?,?)""");

            preparedStatement.setString(1, codeCurrency);
            preparedStatement.setString(2, nameCurrency);
            preparedStatement.setString(3, singCurrency);
            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                currency = new Currency(id, codeCurrency, nameCurrency, singCurrency);
            }
            connect.close();
            preparedStatement.close();
            rs.close();
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                throw new DuplicateCurrencyCodeException();
            } else {
                throw new DataAccessException();
            }
        }
        return currency;
    }

    public int getIdCurrency(String code) {
        int id = 0;
        try {
            Connection connect = DataSource.getConnection();
            PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM Currencies WHERE Code = ?");
            
            preparedStatement.setString(1, code);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
            connect.close();
            preparedStatement.close();
            rs.close();
        } catch (SQLException e) {
            throw new DataAccessException();
        }
        return id;
    }
}
