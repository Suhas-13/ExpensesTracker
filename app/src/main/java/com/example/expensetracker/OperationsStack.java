package com.example.expensetracker;

import java.util.Stack;

/**
 *
 * @author suhas
 */
public class OperationsStack {
    private Stack<Operation> undoOperations;
    private Stack<Operation> redoOperations;
    private ExpenseSheet expenseSheet;
    public boolean addExpense(Expense expense) {
        return expenseSheet.addExpense(expense);
    }
    public boolean removeExpense(Expense expense) {
        return expenseSheet.removeExpense(expense);
    }
    public void performUndo() {
        if (!undoOperations.empty()) {
            Operation recentOperation = undoOperations.pop();
            redoOperations.push(recentOperation);
            if (recentOperation.getOperationType() == recentOperation.ADD_OPERATION) {
                addExpense(recentOperation.getOperationExpense());
            }
            else if (recentOperation.getOperationType() == recentOperation.REMOVE_OPERATION) {
                removeExpense(recentOperation.getOperationExpense());
            }
        }
    }
    public void performRedo() {
        if (!redoOperations.empty()) {
            Operation recentOperation = redoOperations.pop();
            undoOperations.push(recentOperation);
            if (recentOperation.getOperationType() == recentOperation.ADD_OPERATION) {
                addExpense(recentOperation.getOperationExpense());
            }
            else if (recentOperation.getOperationType() == recentOperation.REMOVE_OPERATION) {
                removeExpense(recentOperation.getOperationExpense());
            }
        }
    }
    public OperationsStack(ExpenseSheet expenseSheet) {
        this.expenseSheet = expenseSheet;
        this.undoOperations = new Stack<Operation>();
        this.redoOperations = new Stack<Operation>();
    }
}
