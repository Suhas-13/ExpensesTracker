package com.example.expensetracker;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
/**
 *
 * @author suhas
 */
public class Expense implements Comparable<Expense> {
    private String name;
    private Double price;
    private LocalDate date;
    private String location;
    private String notes;
    private String category;
    private int randomId;
    private String currency;
    private Range priceRange;

    public Object getCharacteristic(String characteristic) {
        if (characteristic.equals("name")) {
            return this.name;
        }
        else if (characteristic.equals("price")) {
            return this.price;
        }
        else if (characteristic.equals("date")) {
            return this.date;
        }
        else if (characteristic.equals("location")) {
            return this.location;
        }
        else if (characteristic.equals("notes")) {
            return this.notes;
        }
        else if (characteristic.equals("category")) {
            return this.category;
        }
        return null;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Expense(String name, String date, double price, String location, String currency, String category, String notes) throws ParseException {
        this.name = name;
        this.price = price;
        this.date = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.location = location;
        this.category = category;
        this.notes = notes;
        this.currency = currency;
        this.randomId = (int) (Math.random() * 500000);
    }
    public Expense() {
        this.randomId = (int) (Math.random() * 500000);
    }

    /**
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the price range
     */
    public Range getPriceRange() {
        return priceRange;
    }

    /**
     * @param priceRange the priceRange to set
     */
    public void setPriceRange(Range priceRange) {
        this.priceRange = priceRange;
    }

    /**
     * @return the price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * @param notes the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int compareTo(Expense otherExpense) {
        if (otherExpense.name.equalsIgnoreCase(name)) {
            if (otherExpense.date.equals(date)) {
                if (otherExpense.location.equalsIgnoreCase(location)) {
                    if (otherExpense.price == price) {
                        return randomId - otherExpense.randomId;
                    }
                    return (int) (price-otherExpense.price);
                }
                return location.compareToIgnoreCase(otherExpense.location);
            }
            return date.compareTo(otherExpense.date);
        }
        return name.compareToIgnoreCase(otherExpense.name);
    }





}