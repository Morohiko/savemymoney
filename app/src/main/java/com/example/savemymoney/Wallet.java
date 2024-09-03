package com.example.savemymoney;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
    private static final String TAG = "SaveMyMoney:Wallet";

    private File cacheFile;

    private static final String cacheFileName = "savemymoneycache.json";

    private Map<String/*date*/, WalletContainer> walletContainers;

    public Wallet(File cDir) {
        cacheFile = new File(cDir, cacheFileName);
        Log.d(TAG, "cacheFile = " + cacheFile);

        walletContainers = new HashMap<String, WalletContainer>();
    }

    void withdrawMoney(Date date, float amount) {
        String dateString = dateToString(date);
        Log.d(TAG, "dateString = " + dateString + ", amount = " + amount);
    }

    void depositMoney(Date date, float amount) {
        Log.d(TAG, "withdrawMoney: date = " + date + ", amount = " + amount);

    }

    void removeOperation(Date date, float amount) {
        Log.d(TAG, "withdrawMoney: date = " + date + ", amount = " + amount);

    }

    void updateBudgetForMonth(float amout) throws JSONException {
        Log.d(TAG, "withdrawMoney: amount = " + amout);
        JSONObject budgetData = new JSONObject();
        budgetData.put("budget", 13000);
    }

    private String dateToString(Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }
}
