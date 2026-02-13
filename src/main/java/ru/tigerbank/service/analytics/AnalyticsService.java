package ru.tigerbank.service.analytics;

import java.time.LocalDate;
import java.util.Map;

public interface AnalyticsService {

    // Разница доходов и расходов за период по всем счетам
    double getNetBalance(LocalDate from, LocalDate to);

    // Разница доходов и расходов за период по конкретному счету
    double getNetBalanceByAccount(Integer accountId, LocalDate from, LocalDate to);

    // Доходы по категориям за период
    Map<String, Double> getIncomeByCategory(LocalDate from, LocalDate to);

    // Расходы по категориям за период
    Map<String, Double> getExpenseByCategory(LocalDate from, LocalDate to);

    // Доходы/расходы по категориям для конкретного счета
    Map<String, Double> getIncomeByCategoryAndAccount(Integer accountId, LocalDate from, LocalDate to);
    Map<String, Double> getExpenseByCategoryAndAccount(Integer accountId, LocalDate from, LocalDate to);

    // Общая сумма доходов/расходов за период
    double getTotalIncome(LocalDate from, LocalDate to);
    double getTotalExpense(LocalDate from, LocalDate to);
}