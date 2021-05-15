package com.example.expensetracker;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TreeSet;

public class ExpensesAdapter extends BaseAdapter {
    private TreeSet<Expense> mExpenses;
    private TreeSet<Expense> mSearchResults;
    private LayoutInflater mInflater;
    private Context expenseContext;
    private ExpenseViewHolder holder;
    private boolean showSearchResults;
    public ExpensesAdapter(Context context, TreeSet<Expense> expenseList) {
        this.mInflater = LayoutInflater.from(context);
        this.mExpenses = expenseList;
        this.expenseContext = context;
        this.showSearchResults = false;
    }
    public void deleteFromSearchTree(Expense deleteExpense) {
        mSearchResults.remove(deleteExpense);
    }
    public boolean isShowSearchResults() {
        return showSearchResults;
    }

    public void setSearchResults(TreeSet<Expense> expenses) {
        this.mSearchResults = expenses;
    }

    public void setExpenses(TreeSet<Expense> expenses) {
        this.mExpenses = expenses;
    }
    public void setShowSearchResults(boolean showSearchResults) {
        this.showSearchResults = showSearchResults;
    }

    @Override
    public int getCount() {
        if (showSearchResults) {
            return mSearchResults.size();
        }
        return mExpenses.size();
    }

    @Override
    public Object getItem(int i) {
        int j = 0;
        Expense currentExpense = null;
        if (showSearchResults) {
            if (mSearchResults == null) {
                return null;
            }
            for (Expense expense : mSearchResults) {
                if (i == j) {
                    currentExpense = expense;
                }
                j++;
            }
        } else {
            for (Expense expense : mExpenses) {
                if (i == j) {
                    currentExpense = expense;
                }
                j++;
            }
        }

        return currentExpense;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        if (view == null) {
            LayoutInflater recordInflater = (LayoutInflater)
                    expenseContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = recordInflater.inflate(R.layout.expenses_row, null);

            holder = new ExpenseViewHolder();
            holder.priceView = (TextView) view.findViewById(R.id.expense_price);
            holder.nameView = (TextView) view.findViewById(R.id.expense_name);
            holder.dateView = (TextView) view.findViewById(R.id.expense_date);
            holder.categoryView = (TextView) view.findViewById(R.id.expense_category);
            holder.currencyView = (TextView) view.findViewById(R.id.expense_currency);
            holder.locationView = (TextView) view.findViewById(R.id.expense_location);
            view.setTag(holder);

        } else {
            holder = (ExpenseViewHolder) view.getTag();
        }
        Expense expense = (Expense) getItem(i);
        holder.nameView.setText(expense.getName());
        holder.priceView.setText(expense.getPrice().toString());
        holder.dateView.setText(expense.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        holder.categoryView.setText(expense.getCategory());
        holder.currencyView.setText(expense.getCurrency());
        holder.locationView.setText(expense.getLocation());
        Log.d("TEST", "here ");
        return view;
    }

}