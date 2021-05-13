package com.example.expensetracker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {
    private ExpenseSheet expenses;
    private Activity currentActivity;
    private ListView expensesView;
    private int selectedPosition = -1;
    private ExpensesAdapter adapter;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.currentActivity = this;
        mPrefs = getPreferences(MODE_PRIVATE);

        GsonBuilder builder = new GsonBuilder().registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
            @Override
            public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return LocalDate.parse(json.getAsString());
            }
        });
        builder.enableComplexMapKeySerialization();



        mEditor = mPrefs.edit();

        Gson gson = builder.create();

        if (mPrefs.contains("storedSheet")) {
            String json = mPrefs.getString("storedSheet", "");
            Log.d("TEST", mPrefs.getString("storedSheet",""));
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
        /*
        Expense expense = null;
        Expense expense2 = null;
        Expense expense3 = null;
        Expense expense4 = null;
        Expense expense5 = null;
        Expense expense6 = null;
        Expense expense7 = null;
        Expense expense8 = null;
        Expense expense9 = null;
        try {
            expense = new Expense("test","08/08/2012", 32.1, "test ocation", "sgd", "category", "testnotes");
            expense2 = new Expense("abc","08/08/2012", 32.1, "testl ocation", "sgd", "category", "testnotes");
            expense3 = new Expense("gef","08/08/2012", 32.1, "testl ocation", "sgd", "category", "testnotes");
            expense4 = new Expense("basdasd","08/08/2012", 32.1, "testl ocation", "sgd", "category", "testnotes");
            expense5 = new Expense("asdadsadasdasdasd","08/08/2012", 32.1, "testl ocation", "sgd", "category", "testnotes");
            expense6 = new Expense("azzx","08/08/2012", 32.1, "testl ocation", "sgd", "category", "testnotes");
            expense7 = new Expense("ccxcxz","08/08/2012", 32.1, "testl ocation", "sgd", "category", "testnotes");
            expense8 = new Expense("asdad","08/08/2012", 32.1, "testl ocation", "sgd", "category", "testnotes");
            expense9 = new Expense("ddddddddd","08/08/2012", 32.1, "testl ocation", "sgd", "category", "testnotes");


        } catch (ParseException e) {
            e.printStackTrace();
        }

        expenses.addExpense(expense);
        expenses.addExpense(expense2);
        expenses.addExpense(expense3);
        expenses.addExpense(expense4);
        expenses.addExpense(expense5);
        expenses.addExpense(expense7);
        expenses.addExpense(expense6);
        expenses.addExpense(expense8);
        expenses.addExpense(expense9);
        */

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
    public void hideListViewSelector() {
        expensesView.getSelector().setAlpha(0);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteExpenseButtonClick(View view) {
        if (selectedPosition == -1) {
            Toast.makeText(getApplicationContext(), "Please select an expense to delete first.", Toast.LENGTH_LONG).show();
        }
        else {
            Log.d("TEST", String.valueOf(selectedPosition));
            Expense expense = (Expense) adapter.getItem(selectedPosition);
            expenses.removeExpense(expense);
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
                    expenses.addExpense(newExpense);
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
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


    }
}
