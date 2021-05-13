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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {
    private ExpenseSheet expenses;
    private Activity currentActivity;
    private ExpensesAdapter adapter;
    SharedPreferences mPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.currentActivity = this;
        mPrefs = getPreferences(MODE_PRIVATE);

        GsonBuilder builder = new GsonBuilder();
        builder.enableComplexMapKeySerialization();

        /*

        SharedPreferences.Editor edit = mPrefs.edit();

        edit.clear();
        edit.commit();


         */

        Gson gson = builder.create();
        if (mPrefs.contains("storedSheet")) {
            String json = mPrefs.getString("storedSheet", "");
            Log.d("TEST", json);
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


        //expenses = new ExpenseSheet();

        RecyclerView expensesRecycler = (RecyclerView) findViewById(R.id.expensesView);
        adapter = new ExpensesAdapter(getApplicationContext(), expenses.sortedExpenses);
        expensesRecycler.setAdapter(adapter);
        // Set layout manager to position the items
        expensesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));




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
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                    GsonBuilder builder = new GsonBuilder();
                    builder.enableComplexMapKeySerialization();
                    Gson gson = builder.create();
                    String json = gson.toJson(expenses, ExpenseSheet.class);
                    prefsEditor.putString("storedSheet", json);
                    prefsEditor.commit();
                    popupWindow.dismiss();
                    adapter.notifyDataSetChanged();
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
