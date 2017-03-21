package com.sanath.moneytracker.adapters;

/**
 * Created by sanathnandasiri on 3/20/17.
 */

public class Summary {
    private String title;
    private double value;

    public Summary(String title, double value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
