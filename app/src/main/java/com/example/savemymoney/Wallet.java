package com.example.savemymoney;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Wallet {
    private static final String TAG = "SaveMyMoney:Wallet";

    private static final String cacheFileName = "savemymoneycache.json";
    private File cacheFile;
    private CacheHelper cacheHelper;

    private Map<String/*date*/, WalletContainer> walletContainers;

    public Wallet(File cDir) {
        cacheFile = new File(cDir, cacheFileName);
        Log.d(TAG, "cacheFile = " + cacheFile);

        walletContainers = new HashMap<String, WalletContainer>();

        cacheHelper = new CacheHelper(cacheFile);
    }

    private void addEntryToWallet(String date, WalletEntry entry) {
        // Check if the date key exists in the map
        WalletContainer walletContainer;
        if (walletContainers.containsKey(date)) {
            // If it exists, get the existing WalletContainer
            walletContainer = walletContainers.get(date);
        } else {
            // If it doesn't exist, create a new WalletContainer and add it to the map
            walletContainer = new WalletContainer();
            walletContainers.put(date, walletContainer);
        }

        // Add the entry to the WalletContainer
        walletContainer.addEntry(entry);

        Log.d(TAG, "addEntryToWallet: Entry added for date: " + date);
    }

    private void removeEntryByTime(String date, String time) {
        if (walletContainers.containsKey(date)) {
            WalletContainer walletContainer = walletContainers.get(date);
            Iterator<WalletEntry> iterator = walletContainer.getEntries().iterator();

            while (iterator.hasNext()) {
                WalletEntry entry = iterator.next();
                if (entry.getTime().equals(time)) {
                    iterator.remove();
                    Log.d(TAG, "removeEntryByTime: Entry removed for date: " + date + " at time: " + time);
                    return;
                }
            }
            Log.d(TAG, "removeEntryByTime: No entry found for date: " + date + " at time: " + time);
        } else {
            Log.d(TAG, "removeEntryByTime: No entries found for date: " + date);
        }
    }

    void withdrawMoney(Date date, int amount, String desc) {
        String dateString = dateToString(date);
        String timeString = timeToString(date);
        Log.d(TAG, "withdrawMoney: date = " + dateString + ", time = " + timeString + ", amount = " + amount);

        walletContainers = cacheHelper.readEntriesFromJson();
        addEntryToWallet(dateString, new WalletEntry(timeString, -amount, desc));
        cacheHelper.writeEntriesToJson(walletContainers);
    }

    void depositMoney(Date date, int amount, String desc) {
        String dateString = dateToString(date);
        String timeString = timeToString(date);
        Log.d(TAG, "depositMoney: date = " + dateString + ", time = " + timeString + ", amount = " + amount);

        walletContainers = cacheHelper.readEntriesFromJson();
        addEntryToWallet(dateString, new WalletEntry(timeString, amount, desc));
        cacheHelper.writeEntriesToJson(walletContainers);
    }

    void removeOperation(Date date) {
        Log.d(TAG, "removeOperation: date = " + date);
        removeEntryByTime(dateToString(date), timeToString(date));
    }

    int getSumOfTransactionsByDate(Date date) {
        walletContainers = cacheHelper.readEntriesFromJson();
        String dateString = dateToString(date);
        int total = 0;
        for (Map.Entry<String, WalletContainer> entry : walletContainers.entrySet()) {
            if (!entry.getKey().equals(dateString)) {
                continue;
            }

            List<WalletEntry> entries = entry.getValue().getEntries();
            for (int i = 0; i < entries.size(); i++) {
                total += entries.get(i).getAmount();
            }
        }
        return total;
    }

    void recreateCache() {
        cacheHelper.recreateCache();
    }

    @NonNull
    private String dateToString(Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    @NonNull
    private String timeToString(Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(date);
    }
}
