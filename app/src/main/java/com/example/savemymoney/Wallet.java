package com.example.savemymoney;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Wallet {
    private static final String TAG = "SaveMyMoney:Wallet";
    private File cacheFile;
    private static final String cacheFileName = "savemymoneycache.json";
    private Map<String/*date*/, WalletContainer> walletContainers;
    private Budget budget;

    CacheHelper cacheHelper;
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

        walletContainers = cacheHelper.readEntriesFromJson(budget);
        addEntryToWallet(dateString, new WalletEntry(timeString, -amount, desc));
        cacheHelper.writeEntriesToJson(walletContainers, budget);
    }

    void depositMoney(Date date, int amount, String desc) {
        String dateString = dateToString(date);
        String timeString = timeToString(date);
        Log.d(TAG, "depositMoney: date = " + dateString + ", time = " + timeString + ", amount = " + amount);

        Map<String, WalletContainer> container = cacheHelper.readEntriesFromJson(budget);
        addEntryToWallet(dateString, new WalletEntry(timeString, amount, desc));
    }

    void removeOperation(Date date) {
        Log.d(TAG, "removeOperation: date = " + date);
        removeEntryByTime(dateToString(date), timeToString(date));
    }

    void updateBudgetForMonth(int amout) throws JSONException {
        Log.d(TAG, "withdrawMoney: amount = " + amout);
        JSONObject budgetData = new JSONObject();
        budgetData.put("budget", 13000);
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

class Budget {
    private int totalBudget;

    public Budget(int totalBudget) {
        this.totalBudget = totalBudget;
    }

    public int getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(int totalBudget) {
        this.totalBudget = totalBudget;
    }

    public int calculateRemainingBudget(Map<String, WalletContainer> walletMap) {
        int totalSpent = 0;

        // Iterate through all entries in the map to calculate total spent
        for (WalletContainer container : walletMap.values()) {
            for (WalletEntry entry : container.getEntries()) {
                totalSpent += entry.getAmount();
            }
        }

        return totalBudget - totalSpent;
    }

    @Override
    public String toString() {
        return "Budget{" +
                "totalBudget=" + totalBudget +
                '}';
    }
}

