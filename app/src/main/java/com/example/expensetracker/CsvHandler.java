package com.example.expensetracker;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;

public class CsvHandler {


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void exportCsv(ExpenseSheet expenseSheet, String fileName) throws IOException {
        try {
            if (!(fileName.substring(Math.max(0, fileName.length() - 4)).equals(".csv"))) {
                fileName = fileName + ".csv";
            }
            String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            String filePath = baseDir + File.separator + "expenseSheets/" + fileName;
            File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "expenseSheets");
            directory.mkdirs();
            String[] headerRecord = {"Name", "Category", "Price", "Currency", "Date", "Location", "Notes"};
            boolean hasPermission = (ContextCompat.checkSelfPermission(MainActivity.mainContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission) {
                ActivityCompat.requestPermissions(MainActivity.currentActivity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        112);
            } else {
                FileWriter mFileWriter = new FileWriter(filePath);
                CSVWriter csvWriter = new CSVWriter(mFileWriter,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);
                csvWriter.writeNext(headerRecord);
                for (Expense expense : expenseSheet.sortedExpenses) {
                    String[] newLine = {expense.getName(), expense.getCategory(), String.valueOf(expense.getPrice()), expense.getCurrency(), expense.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), expense.getLocation(), expense.getNotes()};
                    csvWriter.writeNext(newLine);
                }
                csvWriter.close();
                Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                File fileWithinMyDir = new File(filePath);

                if (fileWithinMyDir.exists()) {
                    intentShareFile.setType("application/csv");
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse(filePath));

                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                            "Sharing File...");
                    intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

                    MainActivity.currentActivity.startActivity(Intent.createChooser(intentShareFile, "Share File"));
                }
            }
        }
        catch (Exception e) {
            Toast.makeText(MainActivity.mainContext, "Error importing csv, please check format.",Toast.LENGTH_LONG).show();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ExpenseSheet importCsv(String fileName) throws IOException, ParseException {
        try {
            ExpenseSheet expenseSheet = new ExpenseSheet();
            boolean hasPermission = (ContextCompat.checkSelfPermission(MainActivity.mainContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission) {
                ActivityCompat.requestPermissions(MainActivity.currentActivity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        142);
            } else {
                FileReader fr = new FileReader(new File(Environment.getExternalStorageDirectory() + File.separator + fileName.substring(8)));
                CSVReader reader = new CSVReader(fr, ',', '\'', 1);
                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                    Expense currentExpense = new Expense(nextLine[0], nextLine[4], Double.parseDouble(nextLine[2]), nextLine[5], nextLine[3], nextLine[1], nextLine[6]);
                    expenseSheet.addExpense(currentExpense);
                }
            }
            Toast.makeText(MainActivity.mainContext, "Imported csv.", Toast.LENGTH_LONG).show();
            return expenseSheet;
        } catch (Exception exception) {
            Toast.makeText(MainActivity.mainContext, "Error importing csv. Please check the format.", Toast.LENGTH_LONG).show();
            return new ExpenseSheet();
        }
    }

}
