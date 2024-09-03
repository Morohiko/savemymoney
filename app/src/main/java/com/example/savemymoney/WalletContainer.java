package com.example.savemymoney;

import java.util.ArrayList;
import java.util.List;

public class WalletContainer {
    private List<WalletEntry> entries;

    public WalletContainer() {
        this.entries = new ArrayList<>();
    }

    public void addEntry(WalletEntry entry) {
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
