package com.example.expensetracker;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;

public class CsvHandler {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void exportCsv(ExpenseSheet expenseSheet, String filename) throws IOException {
        try (
                Writer writer = Files.newBufferedWriter(Paths.get(filename));

                CSVWriter csvWriter = new CSVWriter(writer,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);
        ) {
            String[] headerRecord = {"Name", "Category", "Price", "Currency", "Date", "Location", "Notes"};
            csvWriter.writeNext(headerRecord);
            for (Expense expense: expenseSheet.sortedExpenses) {
                String[] newLine = {expense.getName(), expense.getCategory(), String.valueOf(expense.getPrice()), expense.getCurrency(), String.valueOf(expense.getDate()), expense.getLocation(), expense.getNotes()};
                csvWriter.writeNext(newLine);
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ExpenseSheet importCsv(String fileName) throws IOException, ParseException {
        FileReader fr = new FileReader(fileName);
        CSVReader reader = new CSVReader(fr);
        String[] nextLine;
        ExpenseSheet expensesSheet = new ExpenseSheet();
        while ((nextLine = reader.readNext()) != null) {
            Expense currentExpense = new Expense(nextLine[0], nextLine[4], Double.parseDouble(nextLine[2]), nextLine[5], nextLine[3], nextLine[1], nextLine[6]);
            expensesSheet.addExpense(currentExpense);
        }
        return expensesSheet;
    }

}
