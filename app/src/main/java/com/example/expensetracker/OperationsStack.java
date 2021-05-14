package com.example.expensetracker;

import android.util.Log;

import java.util.Stack;

/**
 *
 * @author suhas
 */
public class OperationsStack {
    private Stack<Operation> undoOperations;
    private Stack<Operation> redoOperations;
    public boolean addExpense(Expense expense) {
        undoOperations.add(new Operation(expense, Operation.ADD_OPERATION));
        return MainActivity.expenses.addExpense(expense);
    }
    public boolean removeExpense(Expense expense) {
        undoOperations.add(new Operation(expense, Operation.REMOVE_OPERATION));
        return MainActivity.expenses.removeExpense(expense);
    }
    public void performUndo() {
        if (!undoOperations.empty()) {
            Operation recentOperation = undoOperations.pop();
            if (recentOperation.getOperationType() == recentOperation.ADD_OPERATION) {
                MainActivity.expenses.removeExpense(recentOperation.getOperationExpense());
            }
            else if (recentOperation.getOperationType() == recentOperation.REMOVE_OPERATION) {
                MainActivity.expenses.addExpense(recentOperation.getOperationExpense());
            }
            recentOperation.toggleOperationType();
            redoOperations.push(recentOperation);
        }
    }
    public void performRedo() {
        if (!redoOperations.empty()) {
            Operation recentOperation = redoOperations.pop();

            if (recentOperation.getOperationType() == recentOperation.ADD_OPERATION) {
                removeExpense(recentOperation.getOperationExpense());
            }
            else if (recentOperation.getOperationType() == recentOperation.REMOVE_OPERATION) {
                addExpense(recentOperation.getOperationExpense());
            }
            recentOperation.toggleOperationType();
            undoOperations.push(recentOperation);
        }
    }
    public OperationsStack() {
        this.undoOperations = new Stack<Operation>();
        this.redoOperations = new Stack<Operation>();
    }
}
