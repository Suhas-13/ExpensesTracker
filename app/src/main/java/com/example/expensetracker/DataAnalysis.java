package com.example.expensetracker;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

public class DataAnalysis {
    private static Integer OUTLIER_VARIANCE = 50; // minimum variance to be classified as outlier in percentage.
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static TreeSet<Expense> findOutliers(ExpenseSheet expenses) {
        TreeSet<Expense> outliers = new TreeSet<Expense>();
        for (String category: expenses.categoryMap.keySet()) {
            HashMap<String, Double> currencyList = new HashMap<String, Double>();
            HashMap<String, Double> currencyNum = new HashMap<String, Double>();
            for (Expense expense: expenses.categoryMap.get(category)) {
                currencyList.put(expense.getCurrency(), currencyList.getOrDefault(expense.getCurrency(), 0.0) +
                        expense.getPrice());
                currencyNum.put(expense.getCurrency(), currencyNum.getOrDefault(expense.getCurrency(), 0.0) + 1);
            }
            for (Expense expense: expenses.categoryMap.get(category)) {
                if (currencyNum.getOrDefault(expense.getCurrency(), 0.0) > 2) {
                    double mean_category = currencyList.get(expense.getCurrency()) / currencyNum.get(expense.getCurrency());
                    double percent_difference = (expense.getPrice() - mean_category) / mean_category;
                    if (Math.abs(percent_difference) * 100 >= OUTLIER_VARIANCE) {
                        outliers.add(expense);
                    }
                }
            }
        }
        return outliers;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static HashMap<String, Double> calculateTotal(ExpenseSheet expenses) {
        HashMap<String, Double> currencyTotals = new HashMap<String, Double>();
        for (String currency: expenses.currencyMap.keySet()) {
            for (Expense expense: expenses.currencyMap.get(currency)) {
                currencyTotals.put(expense.getCurrency(), currencyTotals.getOrDefault(expense.getCurrency(), 0.0) +
                        expense.getPrice());
            }
        }
        return currencyTotals;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static HashMap<String, Double> calculateAverages(ExpenseSheet expenses) {
        HashMap<String, Double> currencyTotals = new HashMap<String, Double>();
        HashMap<String, Integer> currencyCounts = new HashMap<String, Integer>();
        HashMap<String, Double> currencyAverages = new HashMap<String, Double>();
        for (String currency: expenses.currencyMap.keySet()) {
            for (Expense expense: expenses.currencyMap.get(currency)) {
                currencyTotals.put(expense.getCurrency(), currencyTotals.getOrDefault(expense.getCurrency(), 0.0) +
                        expense.getPrice());
                currencyCounts.put(expense.getCurrency(), currencyCounts.getOrDefault(expense.getCurrency(), 0) + 1);
            }
        }
        for (String currency: currencyTotals.keySet()) {
            currencyAverages.put(currency, currencyTotals.get(currency) / currencyCounts.get(currency));
        }
        return currencyAverages;
    }

}
