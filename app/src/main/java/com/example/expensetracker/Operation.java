package com.example.expensetracker;


/**
 *
 * @author suhas
 */
public class Operation {
    public static int ADD_OPERATION = 1;
    public static int REMOVE_OPERATION = 2;
    private int operationType;
    private Expense operationExpense;
    /*
    this method flips the current operation, if it is ADD it will become REMOVE and vice versa.
     */
    public void toggleOperationType() {
        if (this.operationType == ADD_OPERATION) {
            this.operationType = REMOVE_OPERATION;
        }
        else if (this.operationType == REMOVE_OPERATION) {
            this.operationType = ADD_OPERATION;
        }
    }
    /*
    this constructor is used to initialize a new opreation and sets the expense for an operation and the operationt type
     */
    public Operation(Expense operationExpense, int operationType) {
        this.operationExpense = operationExpense;
        this.operationType = operationType;
    }



    /**
     * @return the operationType
     */
    public int getOperationType() {
        return operationType;
    }

    /**
     * @param operationType the operationType to set
     */
    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    /**
     * @return the operationExpense
     */
    public Expense getOperationExpense() {
        return operationExpense;
    }

    /**
     * @param operationExpense the operationExpense to set
     */
    public void setOperationExpense(Expense operationExpense) {
        this.operationExpense = operationExpense;
    }

}
