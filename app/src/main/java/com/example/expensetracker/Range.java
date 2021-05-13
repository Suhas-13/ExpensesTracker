package com.example.expensetracker;

/**
 *
 * @author suhas
 */
public class Range {
    private double priceLow;
    private double priceHigh;
    public Range(double priceLow, double priceHigh) {
        this.priceLow = priceLow;
        this.priceHigh = priceHigh;
    }

    /**
     * @return the priceLow
     */
    public double getPriceLow() {
        return priceLow;
    }

    /**
     * @param priceLow the priceLow to set
     */
    public void setPriceLow(double priceLow) {
        this.priceLow = priceLow;
    }

    /**
     * @return the priceHigh
     */
    public double getPriceHigh() {
        return priceHigh;
    }

    /**
     * @param priceHigh the priceHigh to set
     */
    public void setPriceHigh(double priceHigh) {
        this.priceHigh = priceHigh;
    }

}
