package org.example.currencyExchanger.dao;

import org.example.currencyExchanger.exception.DataAccessException;
import org.example.currencyExchanger.model.Currency;
import org.example.currencyExchanger.model.ExchangeRates;
import org.example.currencyExchanger.service.DataSource;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRatesDAO {
    public List<ExchangeRates> getAllExchangeRates() {
        List<ExchangeRates> exchangeRates = new ArrayList<>();

        try (Connection connect = DataSource.getConnection();
            Statement statement = connect.createStatement();
            ResultSet rs = statement.executeQuery("""                                
                                                    SELECT ExchangeRates.ID,
                                                    X.ID, X.FullName, X.Code, X.Sing,
                                                    Y.ID, Y.FullName, Y.Code, Y.Sing,
                                                    ExchangeRates.Rate
                                                    FROM ExchangeRates
                                                    JOIN Currencies X ON ExchangeRates.BaseCurrencyId = X.ID
                                                    JOIN Currencies Y ON ExchangeRates.TargetCurrencyId = Y.ID""")) {

            while (rs.next()) {
                int id = rs.getInt(1);
                BigDecimal rate = rs.getBigDecimal(10);

                Currency baseCurrency = new Currency(
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5)
                );

                Currency targetCurrency = new Currency(
                        rs.getInt(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getString(9)
                );

                ExchangeRates exchangeRate = new ExchangeRates(id,baseCurrency,targetCurrency,rate);
                exchangeRates.add(exchangeRate);
            }
        } catch (SQLException e) {
            throw new DataAccessException();
        }
        return exchangeRates;
    }

    public ExchangeRates getExchangeRate(String baseCode, String targetCode) {
        ExchangeRates exchangeRate = null;
        try (Connection connect = DataSource.getConnection();
             PreparedStatement preparedStatement = connect.prepareStatement("""                                
                                                    SELECT ExchangeRates.ID,
                                                    X.ID, X.FullName, X.Code, X.Sing,
                                                    Y.ID, Y.FullName, Y.Code, Y.Sing,
                                                    ExchangeRates.Rate
                                                    FROM ExchangeRates
                                                    JOIN Currencies X ON ExchangeRates.BaseCurrencyId = X.ID
                                                    JOIN Currencies Y ON ExchangeRates.TargetCurrencyId = Y.ID
                                                    WHERE X.CODE = ? AND Y.CODE = ?""")) {
            preparedStatement.setString(1, baseCode);
            preparedStatement.setString(2, targetCode);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                BigDecimal rate = rs.getBigDecimal(10);

                Currency baseCurrency = new Currency(
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5)
                );

                Currency targetCurrency = new Currency(
                        rs.getInt(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getString(9)
                );

                exchangeRate = new ExchangeRates(id,baseCurrency,targetCurrency,rate);
            }
            connect.close();
            preparedStatement.close();
            rs.close();
        } catch (SQLException e) {
            throw new DataAccessException();
        }
        return exchangeRate;
    }

    public void addExchangeRates(int baseId, int targetId, BigDecimal rate) {
        try {
            Connection connect = DataSource.getConnection();
            PreparedStatement preparedStatement = connect.prepareStatement("""
            INSERT INTO ExchangeRates(BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?,?,?)""");

            preparedStatement.setInt(1,baseId);
            preparedStatement.setInt(2,targetId);
            preparedStatement.setBigDecimal(3,rate);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Валюта не была добавлена");
            }
            connect.close();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new DataAccessException();
        }
    }

    public void updatingExchangeRate(int baseId, int targetId, BigDecimal rate) {
        try {
            Connection connect = DataSource.getConnection();
            PreparedStatement preparedStatement = connect.prepareStatement("""
                                                UPDATE ExchangeRates SET Rate = ?
                                                WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?""");

            preparedStatement.setBigDecimal(1, rate);
            preparedStatement.setInt(2, baseId);
            preparedStatement.setInt(3, targetId);
            preparedStatement.executeUpdate();

            connect.close();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new DataAccessException();
        }
    }

    public BigDecimal getRate(int baseId, int targetId) {
        BigDecimal rate = null;
        try {
            Connection connect = DataSource.getConnection();
            PreparedStatement preparedStatement = connect.prepareStatement("""
                                                         SELECT (Rate) FROM ExchangeRates
                                                         WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?""");

            preparedStatement.setInt(1, baseId);
            preparedStatement.setInt(2, targetId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                rate = rs.getBigDecimal(1);
            }
            connect.close();
            preparedStatement.close();
            rs.close();
        } catch (SQLException e) {
            throw new DataAccessException();
        }
        return rate;
    }
}
