package com.example.savemymoney;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class WalletContainer {
    private static final String TAG = "SaveMyMoney:WalletContainer";

    private final List<WalletEntry> entries;

    public WalletContainer() {
        this.entries = new ArrayList<>();
    }

    public void addEntry(WalletEntry entry) {
        Log.d(TAG, "addEntry: entry = " + entry.toString());
        entries.add(entry);
    }

    public List<WalletEntry> getEntries() {
        return entries;
    }

    @Override
    public String toString() {
        return "WalletContainer{" +
                "entries=" + entries +
                '}';
    }
}
