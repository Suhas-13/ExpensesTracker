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
    /*
    the addexpense method will add to the undo stack and also call the proper addexpense method in expensessheet.
     */
    public boolean addExpense(Expense expense) {
        undoOperations.add(new Operation(expense, Operation.ADD_OPERATION));
        return MainActivity.expenses.addExpense(expense);
    }
    /*
    the removeexpense method will add to the undo stack and also call the proper removeexpense method in expensessheet.
     */
    public boolean removeExpense(Expense expense) {
        undoOperations.add(new Operation(expense, Operation.REMOVE_OPERATION));
        return MainActivity.expenses.removeExpense(expense);
    }

    /*
    this method performs the undo operation by popping from the undo stack if it is not empty. it then performs the inverse of the operationtype, for instance adding if the operation is a removal operation or vice versa. it then adds the inverse to the redostack.
     */
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
    /*
    this does the exact same thing as the undo method except that it appends the inverse to the undostack at the end.
     */
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
    /*
    this creates an undo and redo stack and initalizes the operationsstack.
     */
    public OperationsStack() {
        this.undoOperations = new Stack<Operation>();
        this.redoOperations = new Stack<Operation>();
    }
}
