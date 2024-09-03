package com.example.savemymoney;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WalletEntry {
    private String time;
    private int amount;
    private String description;

    private WalletEntry() {}

    @SuppressLint("SimpleDateFormat")
    public WalletEntry(Date dt, int am, String desc) {
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
        time = fmt.format(dt);

        amount = am;
        description = desc;
    }

    public WalletEntry(String tm, int am, String desc) {
        time = tm;
        amount = am;
        description = desc;
    }


    public String getTime() {
        return time;
    }

    public int getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "time='" + time + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                '}';
    }
}
