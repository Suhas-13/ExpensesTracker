package com.example.expensetracker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity {
    public static ExpenseSheet expenses;
    public static Activity currentActivity;
    private ListView expensesView;
    private int selectedPosition = -1;
    public static Context mainContext;
    private ExpensesAdapter adapter;
    private SharedPreferences mPrefs;
    private OperationsStack currentOperationStack;
    private SharedPreferences.Editor mEditor;

    public void refreshAdapter() {
        this.adapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadExpenseSheet(ExpenseSheet newExpenses) {
        this.expenses = newExpenses;
        adapter.setExpenses(this.expenses.sortedExpenses);
        adapter.setSearchResults(null);
        adapter.setShowSearchResults(false);
        refreshAdapter();
        saveData();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 112: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "The app was allowed to write to your storage!", Toast.LENGTH_LONG).show();
                    try {
                        exportSheetClick(null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
            case 142: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "The app was allowed to read from your storage!", Toast.LENGTH_LONG).show();
                    importSheetClick(null);
                } else {
                    Toast.makeText(this, "The app was not allowed to read from your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.currentActivity = this;
        this.mainContext = getApplicationContext();
        mPrefs = getPreferences(MODE_PRIVATE);

        GsonBuilder builder = new GsonBuilder().registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
            @Override
            public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return LocalDate.parse(json.getAsString());
            }
        });
        builder.enableComplexMapKeySerialization();
        currentOperationStack = new OperationsStack();


        mEditor = mPrefs.edit();

        Gson gson = builder.create();

        if (mPrefs.contains("storedSheet")) {
            String json = mPrefs.getString("storedSheet", "");
            Log.d("TEST",json);
            try {
                expenses = gson.fromJson(json, ExpenseSheet.class);
            }
            catch(Exception e) {
                Log.d("TEST",e.toString());
                expenses = new ExpenseSheet();
            }

        }
        else {
            expenses = new ExpenseSheet();
        }
        adapter = new ExpensesAdapter(getApplicationContext(), expenses.sortedExpenses);
        expensesView = (ListView) findViewById(R.id.records_list);
        expensesView.setAdapter(adapter);
        expensesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedPosition = i;
                if (expensesView.getSelector().getAlpha() == 0) {
                    expensesView.getSelector().setAlpha(255);
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void newSheetClick(View view) {
        loadExpenseSheet(new ExpenseSheet());
        saveData();
    }
    public void undoButtonClick(View view) {
        currentOperationStack.performUndo();
        refreshAdapter();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    try {
                        this.expenses = CsvHandler.importCsv(data.getData().getLastPathSegment());
                        this.adapter.setSearchResults(this.expenses.sortedExpenses);
                        this.adapter.setShowSearchResults(false);
                        this.adapter.setSearchResults(null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            case 123: {
                if (resultCode  == RESULT_OK){
                    try {
                        ExpenseSheet expenseSheet = CsvHandler.importCsv(data.getData().getLastPathSegment());
                        if (expenseSheet != null) {
                            loadExpenseSheet(expenseSheet);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Error reading file in, is it the correct file type(csv)?", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void importSheetClick(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Choose CSV File"), 123);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void exportSheetClick(View view) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime currentDate = LocalDateTime.now();
        CsvHandler.exportCsv(expenses, "expenses" + dtf.format(currentDate).toString() +"-" + (int) (Math.random() * 1000));
    }
    public void redoButtonClick(View view) {
        currentOperationStack.performRedo();
        refreshAdapter();
    }
    public void hideListViewSelector() {
        expensesView.getSelector().setAlpha(0);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteExpenseButtonClick(View view) {
        if (selectedPosition == -1) {
            Toast.makeText(getApplicationContext(), "Please select an expense to delete first.", Toast.LENGTH_LONG).show();
        }
        else {
            Expense expense = (Expense) adapter.getItem(selectedPosition);
            currentOperationStack.removeExpense(expense);
            if (adapter.isShowSearchResults()) {
                adapter.deleteFromSearchTree(expense);
            }
            adapter.notifyDataSetChanged();
            saveData();
            hideListViewSelector();
            selectedPosition = -1;

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveData() {

        GsonBuilder builder = new GsonBuilder().registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {

            @Override
            public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                return context.serialize(src.toString());
            }

        });

        builder.enableComplexMapKeySerialization();
        Gson gson = builder.create();
        String json = gson.toJson(expenses, ExpenseSheet.class);
        Log.d("TEST",json);
        mEditor.putString("storedSheet", json);
        mEditor.commit();
    }
    public void addExpenseButtonClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.add_expense, null);

        // create the popup window
        int width = currentActivity.getWindow().getDecorView().getWidth();
        int height = currentActivity.getWindow().getDecorView().getHeight();
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        final ImageView closePopup =  popupView.findViewById(R.id.closePopup);
        final EditText locationText =  popupView.findViewById(R.id.locationField);
        final EditText nameText =  popupView.findViewById(R.id.nameField);
        final EditText priceText =  popupView.findViewById(R.id.priceField);
        final EditText currencyText =  popupView.findViewById(R.id.currencyField);
        final EditText categoryText =  popupView.findViewById(R.id.categoryField);
        final EditText dateText =  popupView.findViewById(R.id.dateField);
        final EditText notesText =  popupView.findViewById(R.id.notesField);
        final Button submit =  popupView.findViewById(R.id.addExpenseButton);
        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {
                    String name = nameText.getText().toString();
                    String location = locationText.getText().toString();
                    double price = Double.parseDouble(priceText.getText().toString());
                    String currency = currencyText.getText().toString();
                    String category = categoryText.getText().toString();
                    String date = dateText.getText().toString();
                    String notes = notesText.getText().toString();
                    Expense newExpense = new Expense(name, date, price, location, currency, category, notes);
                    currentOperationStack.addExpense(newExpense);
                    adapter.notifyDataSetChanged();
                    saveData();
                    popupWindow.dismiss();

                }
                catch (Exception e) {
                    Log.d("ERROR", String.valueOf(e));
                }

            }
        });

        // show the popup window
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setupExpenseAnalysisData(LinearLayout dataList, LayoutInflater inflater, String mainText, boolean averagesMode) {
        dataList.removeAllViews();
        View currencyView = inflater.inflate(R.layout.analysis_row, null);
        TextView currencyTextView = (TextView) currencyView.findViewById(R.id.expense_currency);
        TextView currencyTotalView = (TextView) currencyView.findViewById(R.id.expense_value);
        currencyTextView.setText("Currency");
        currencyTotalView.setText(mainText);
        currencyTotalView.setAllCaps(true);
        currencyTotalView.setTextSize(18);

        currencyTextView.setAllCaps(true);
        currencyTextView.setTextSize(18);
        currencyTextView.setTypeface(null, Typeface.BOLD);
        currencyTotalView.setTypeface(null, Typeface.BOLD);

        dataList.addView(currencyView);
        HashMap<String, Double> currencyValues;
        if (averagesMode) {
             currencyValues = DataAnalysis.calculateAverages(expenses);
        }
        else {
             currencyValues = DataAnalysis.calculateTotal(expenses);
        }

        for (String currency: currencyValues.keySet()) {
            currencyView = inflater.inflate(R.layout.analysis_row, null);
            currencyTextView = (TextView) currencyView.findViewById(R.id.expense_currency);
            currencyTotalView = (TextView) currencyView.findViewById(R.id.expense_value);
            currencyTextView.setText(currency);
            currencyTotalView.setText(String.valueOf(currencyValues.get(currency)));
            currencyTextView.setTextSize(17);
            currencyTotalView.setTextSize(17);
            dataList.addView(currencyView);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void openDataAnalysis(View view) {
        // inflate the layout of the popup window
        final LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.data_analysis, null);

        // create the popup window
        int width = currentActivity.getWindow().getDecorView().getWidth();
        int height = currentActivity.getWindow().getDecorView().getHeight();
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        final Button outlierButton = popupView.findViewById(R.id.findOutliers);
        final ImageView closePopup =  popupView.findViewById(R.id.closePopup);
        final Button searchButton = (Button) (findViewById(R.id.searchButton));
        final Button dataButton = (Button) (findViewById(R.id.openDataAnalysis));
        final Button averageButton = (Button) (popupView.findViewById(R.id.averageExpenditure));
        final Button totalButton = (Button) (popupView.findViewById(R.id.totalExpenditure));
        final LinearLayout dataList = (LinearLayout) (popupView.findViewById(R.id.dataListView));
        if (adapter.isShowSearchResults()) {
            dataButton.setText("Data Analysis");
            searchButton.setText("Search Records");
            adapter.setShowSearchResults(false);
            adapter.setSearchResults(null);
            refreshAdapter();
            popupWindow.dismiss();
            return;
        }
        averageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                setupExpenseAnalysisData(dataList, inflater, "Average Expenditure", true);
            }
        });
        totalButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Log.d("TEST","CLICKED");
                setupExpenseAnalysisData(dataList, inflater, "Total Expenditure", false);
            }
        });
        outlierButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                TreeSet<Expense> outliers = DataAnalysis.findOutliers(expenses);
                adapter.setSearchResults(outliers);
                adapter.setShowSearchResults(true);
                dataButton.setText("Hide Outliers");
                searchButton.setText("Search Records");
                popupWindow.dismiss();
                refreshAdapter();
            }
        });

        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });


        // show the popup window
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


    }

    public void searchButtonClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.search_expenses, null);
        final Button searchButton = findViewById(R.id.searchButton);
        final Button dataButton = (Button) (findViewById(R.id.openDataAnalysis));
        if (adapter.isShowSearchResults()) {
            adapter.setShowSearchResults(false);
            adapter.setSearchResults(null);
            searchButton.setText("Search Records");
            dataButton.setText("Data Analysis");
            refreshAdapter();
            return;
        }
        searchButton.setText("Exit Search");
        dataButton.setText("Data Analysis");
        // create the popup window
        int width = currentActivity.getWindow().getDecorView().getWidth();
        int height = currentActivity.getWindow().getDecorView().getHeight();
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        final ImageView closePopup =  popupView.findViewById(R.id.closePopup);
        final EditText locationText =  popupView.findViewById(R.id.locationField);
        final EditText nameText =  popupView.findViewById(R.id.nameField);
        final EditText priceMin =  popupView.findViewById(R.id.priceMinField);
        final EditText priceMax =  popupView.findViewById(R.id.priceMaxField);
        final EditText currencyText =  popupView.findViewById(R.id.currencyField);
        final EditText categoryText =  popupView.findViewById(R.id.categoryField);
        final EditText dateText =  popupView.findViewById(R.id.dateField);
        final Button submit =  popupView.findViewById(R.id.searchExpenseButton);

        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setShowSearchResults(false);
                adapter.setSearchResults(null);
                adapter.notifyDataSetChanged();
                popupWindow.dismiss();
                searchButton.setText("Search Records");
                dataButton.setText("Data Analysis");
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {
                        String name = nameText.getText().toString();
                        String location = locationText.getText().toString();
                        Range rangeObject = null;
                        if (!priceMin.getText().toString().isEmpty() && !priceMax.getText().toString().isEmpty()) {
                            double minPrice = Double.parseDouble(priceMin.getText().toString());
                            double maxPrice = Double.parseDouble(priceMax.getText().toString());
                            rangeObject = new Range(minPrice, maxPrice);
                        }
                        String currency = currencyText.getText().toString();
                        String category = categoryText.getText().toString();
                        String date = dateText.getText().toString();
                        Expense newExpense = new Expense();
                        newExpense.setName(name);
                        newExpense.setLocation(location);
                        newExpense.setCurrency(currency);
                        newExpense.setCategory(category);
                        newExpense.setDate(date);
                        newExpense.setPriceRange(rangeObject);
                        TreeSet<Expense> expenseList = expenses.multiSearch(newExpense);
                        adapter.setSearchResults(expenseList);
                        adapter.setShowSearchResults(true);
                        refreshAdapter();
                        popupWindow.dismiss();
                }
                catch (Exception e) {
                }

            }
        });

        // show the popup window
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


    }
}
