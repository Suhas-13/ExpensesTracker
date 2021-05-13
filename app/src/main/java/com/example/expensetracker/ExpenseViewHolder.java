package com.example.expensetracker;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ExpenseViewHolder extends RecyclerView.ViewHolder {
    public TextView nameView;
    public TextView priceView;
    public TextView currencyView;
    public TextView locationView;
    public TextView categoryView;
    public TextView dateView;
    public TextView notesView;


    public ExpenseViewHolder(View itemView) {
        super(itemView);
        nameView = (TextView)itemView.findViewById(R.id.expenseName);
        priceView = (TextView)itemView.findViewById(R.id.expensePrice);
        categoryView = (TextView)itemView.findViewById(R.id.expenseCategory);
        dateView = (TextView)itemView.findViewById(R.id.expenseDate);
        currencyView = (TextView)itemView.findViewById(R.id.expenseCurrency);
        locationView = (TextView)itemView.findViewById(R.id.expenseLocation);
        notesView = (TextView)itemView.findViewById(R.id.expenseNotes);
    }
}