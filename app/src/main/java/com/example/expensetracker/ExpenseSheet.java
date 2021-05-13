package com.example.expensetracker;


import android.util.Log;

import androidx.recyclerview.widget.SortedList;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author suhas
 */
public class ExpenseSheet {

    public HashMap<String, ArrayList<Expense> > nameMap;
    public HashMap<String, ArrayList<Expense> > categoryMap;
    public HashMap<String, ArrayList<Expense> > locationMap;
    public HashMap<LocalDate, ArrayList<Expense> > dateMap;
    public HashMap<String, ArrayList<Expense> > currencyMap;
    public TreeMap<Double, ArrayList<Expense> > priceMap;
    public TreeSet<Expense> sortedExpenses;

    public ExpenseSheet() {
        this.nameMap = new HashMap<String, ArrayList<Expense> >();
        this.categoryMap = new HashMap<String, ArrayList<Expense> >();
        this.locationMap = new HashMap<String, ArrayList<Expense> >();
        this.currencyMap = new HashMap<String, ArrayList<Expense> >();
        this.dateMap = new HashMap<LocalDate, ArrayList<Expense> >();
        this.priceMap = new TreeMap<Double, ArrayList<Expense> >();
        this.sortedExpenses = new TreeSet<Expense>();
    }

    public boolean addExpense(Expense expense) {
        if (nameMap.containsKey(expense.getName())) {
            nameMap.get(expense.getName()).add(expense);
        }
        else {
            ArrayList<Expense> arr = new ArrayList<Expense>();
            arr.add(expense);
            nameMap.put(expense.getName(), arr);
        }

        if (categoryMap.containsKey(expense.getCategory())) {
            categoryMap.get(expense.getCategory()).add(expense);
        }
        else {
            ArrayList<Expense> arr = new ArrayList<Expense>();
            arr.add(expense);
            categoryMap.put(expense.getCategory(), arr);
        }

        if (locationMap.containsKey(expense.getLocation())) {
            locationMap.get(expense.getLocation()).add(expense);
        }
        else {
            ArrayList<Expense> arr = new ArrayList<Expense>();
            arr.add(expense);
            locationMap.put(expense.getLocation(), arr);
        }

        if (dateMap.containsKey(expense.getDate())) {
            dateMap.get(expense.getDate()).add(expense);
        }
        else {
            ArrayList<Expense> arr = new ArrayList<Expense>();
            arr.add(expense);
            dateMap.put(expense.getDate(), arr);
        }

        if (priceMap.containsKey(expense.getPrice())) {
            priceMap.get(expense.getPrice()).add(expense);
        }
        else {
            ArrayList<Expense> arr = new ArrayList<Expense>();
            arr.add(expense);
            priceMap.put(expense.getPrice(), arr);
        }
        sortedExpenses.add(expense);
        return true;
    }
    public void searchHelper(Expense partialExpense, HashMap<Expense, Integer> searchList, String characteristic) {
        HashMap<?, ArrayList<Expense>> queryList;
        ArrayList<Expense> expenseList;
        if (characteristic.equals("name")) {
            queryList = nameMap;
            expenseList = queryList.get(partialExpense.getCharacteristic(characteristic));
        }
        else if (characteristic.equals("date")) {
            queryList = dateMap;
            expenseList = queryList.get(partialExpense.getCharacteristic(characteristic));
        }
        else if (characteristic.equals("location")) {
            queryList = locationMap;
            expenseList = queryList.get(partialExpense.getCharacteristic(characteristic));
        }
        else if (characteristic.equals("category")) {
            queryList = categoryMap;
            expenseList = queryList.get(partialExpense.getCharacteristic(characteristic));
        }
        else if (characteristic.equals("priceRange")) {
            expenseList = searchByRange(partialExpense.getPriceRange());
        }
        else if (characteristic.equals("currency")) {
            queryList = currencyMap;
            expenseList = queryList.get(partialExpense.getCharacteristic(characteristic));
        }
        else {
            return;
        }

        if (expenseList!=null) {
            for (int i=0; i<expenseList.size(); i++) {
                if (searchList.get(expenseList.get(i)) != null) {
                    searchList.put(expenseList.get(i), searchList.get(expenseList.get(i))+1);
                }
                else {
                    searchList.put(expenseList.get(i), 1);
                }
            }
        }

    }
    public ArrayList<Expense> searchByRange(Range priceRange) {

        NavigableMap<Double, ArrayList<Expense>> withinRange = priceMap.subMap(priceRange.getPriceLow(), true, priceRange.getPriceHigh(), true);
        ArrayList<Expense> output = new ArrayList<Expense>();
        for (Entry<Double,ArrayList<Expense>> entry: withinRange.entrySet()) {
            output.addAll(entry.getValue());
        }
        return output;
    }
    public TreeSet<Expense> multiSearch(Expense partialExpense) {
        int numCriteria = 0;
        HashMap<Expense, Integer> searchList = new HashMap<Expense, Integer>();
        if (partialExpense.getName() != null && !partialExpense.getName().isEmpty()) {
            numCriteria++;
            searchHelper(partialExpense, searchList, "name");
        }
        if (partialExpense.getDate() != null) {
            numCriteria++;
            searchHelper(partialExpense, searchList, "date");
        }
        if (partialExpense.getLocation() != null && !partialExpense.getLocation().isEmpty()) {
            numCriteria++;
            searchHelper(partialExpense, searchList, "location");
        }
        if (partialExpense.getCurrency() != null && !partialExpense.getCurrency().isEmpty()) {
            numCriteria++;
            searchHelper(partialExpense, searchList, "currency");
        }
        if (partialExpense.getCategory() != null && !partialExpense.getCategory().isEmpty()) {
            numCriteria++;
            searchHelper(partialExpense, searchList, "category");
        }
        if (partialExpense.getPriceRange() != null) {
            numCriteria++;
            searchHelper(partialExpense, searchList, "priceRange");
        }
        Log.d("TEST","Num criteria" + numCriteria);
        TreeSet<Expense> output = new TreeSet<Expense>();
        for (Expense expense: searchList.keySet()) {
            if (searchList.get(expense) == numCriteria) {
                output.add(expense);
            }
        }
        return output;
    }

    public boolean removeExpense(Expense expense) {
        if (expense == null) {
            return false;
        }
        if (nameMap.containsKey(expense.getName())) {
            nameMap.get(expense.getName()).remove(expense);
        }

        if (categoryMap.containsKey(expense.getCategory())) {
            categoryMap.get(expense.getCategory()).remove(expense);
        }

        if (locationMap.containsKey(expense.getLocation())) {
            locationMap.get(expense.getLocation()).remove(expense);
        }

        if (dateMap.containsKey(expense.getDate())) {
            dateMap.get(expense.getDate()).remove(expense);
        }

        if (priceMap.containsKey(expense.getPrice())) {
            priceMap.get(expense.getPrice()).remove(expense);
        }
        sortedExpenses.remove(expense);
        return true;
    }
}
