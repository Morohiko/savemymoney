package com.example.savemymoney;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.Date;

public class Wallet {
    private static Wallet instance;

    private static final String TAG = "SaveMyMoney:Wallet";

    File cacheFile;

    private static final String cacheFileName = "savemymoneycache.json";

    public Wallet(Context context) {
        cacheFile = new File(context.getCacheDir(), cacheFileName);
    }

    void withdrawMoney(Date date, float amount) {
        Log.d(TAG, "withdrawMoney: date = " + date + ", amount = " + amount);
    }

    void depositMoney(Date date, float amount) {
        Log.d(TAG, "withdrawMoney: date = " + date + ", amount = " + amount);

    }

    void removeOperation(Date date, float amount) {
        Log.d(TAG, "withdrawMoney: date = " + date + ", amount = " + amount);

    }

    void updateBudgetForMonth(float amout) {
        Log.d(TAG, "withdrawMoney: amount = " + amout);
    }
}
