package com.anvay.cctvcustomer.models;

public class Date {
    private String date, day;
    private boolean isSelected;

    public Date(String date, String day, boolean isSelected) {
        this.date = date;
        this.day = day;
        this.isSelected = isSelected;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
