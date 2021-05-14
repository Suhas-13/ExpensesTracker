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
    public void toggleOperationType() {
        if (this.operationType == 1) {
            this.operationType = 2;
        }
        else if (this.operationType == 2) {
            this.operationType = 1;
        }
    }
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
