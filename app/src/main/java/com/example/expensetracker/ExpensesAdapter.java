package com.example.expensetracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TreeSet;

public class ExpensesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private TreeSet<Expense> mExpenses;
    private LayoutInflater mInflater;


    public ExpensesAdapter(Context context, TreeSet<Expense> expenseList) {
        this.mInflater = LayoutInflater.from(context);
        this.mExpenses = expenseList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.expenses_row, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ExpenseViewHolder expenseViewHolder = (ExpenseViewHolder) holder;
        int i=0;
        Expense currentExpense = null;
        for (Expense expense: mExpenses) {
            if (i==position) {
                currentExpense = expense;
            }
            i++;
        }
        DateFormat df = new SimpleDateFormat("dd/MM/yy");

        expenseViewHolder.nameView.setText(currentExpense.getName());
        expenseViewHolder.currencyView.setText(currentExpense.getCurrency());
        expenseViewHolder.priceView.setText(currentExpense.getPrice().toString());
        expenseViewHolder.dateView.setText(currentExpense.getDate().toString());
        expenseViewHolder.locationView.setText(currentExpense.getLocation());
        expenseViewHolder.notesView.setText(currentExpense.getNotes());
        expenseViewHolder.categoryView.setText(currentExpense.getCategory());

    }



    @Override
    public int getItemCount() {
        return mExpenses.size();
    }


}