package com.example.savemymoney;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CacheHelper {

    private static final String TAG = "SaveMyMoney:CacheHelper";

    private File cacheFile;

    CacheHelper(File cacheFl) {
        boolean isSuccess = createFileIfNotExist(cacheFl);
        if (!isSuccess) {
            Log.d(TAG, "can`t create file");
            // TODO: Exception?
            return;
        }
        cacheFile = cacheFl;
    }

    WalletEntry jsonToWalletEntry(@NonNull JSONObject obj) {
        WalletEntry entry;
        try {
            entry = new WalletEntry(obj.getString("time"), obj.getInt("amount"), obj.getString("desc"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return entry;
    }

    JSONObject walletEntryToJson(@NonNull WalletEntry entry) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("time", entry.getTime());
        obj.put("amount", entry.getAmount());
        obj.put("desc", entry.getDescription());
        return obj;
    }

    void writeEntriesToJson(Map<String, WalletContainer> dateToEntriesMap, Budget budget) {
        JSONObject jsonObject = new JSONObject();

        try {
            // Save the budget
            jsonObject.put("budget", budget.getTotalBudget());

            // Iterate over the map and convert each date's entries to JSON
            for (Map.Entry<String, WalletContainer> entry : dateToEntriesMap.entrySet()) {
                String dateKey = entry.getKey();
                WalletContainer container = entry.getValue();

                JSONArray jsonArray = new JSONArray();

                // Convert each Entry to a JSON object
                List<WalletEntry> entries = container.getEntries();
                for (WalletEntry e : entries) {
                    jsonArray.put(walletEntryToJson(e));
                }

                // Put the JSON array into the main JSON object with the date as the key
                jsonObject.put(dateKey, jsonArray);
            }

            // Write the JSON object to a file
            try (FileOutputStream fos = new FileOutputStream(cacheFile)) {
                fos.write(jsonObject.toString().getBytes());
                fos.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Map<String, WalletContainer> readEntriesFromJson(Budget budget) {
        Map<String, WalletContainer> dateToEntriesMap = new HashMap<>();

        StringBuilder jsonString = new StringBuilder();

        try {
            FileInputStream fis = new FileInputStream(cacheFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            // Parse the JSON string
            JSONObject jsonObject = new JSONObject(jsonString.toString());

            // Load the budget
            if (jsonObject.has("budget")) {
                budget.setTotalBudget(jsonObject.getInt("budget"));
            }

            // Iterate over the keys (dates)
            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String dateKey = keys.next();

                // Skip if it's not an actual date (e.g., budget field)
                if (!dateKey.matches("\\d{2}-\\d{2}-\\d{2}")) {
                    continue;
                }

                JSONArray entriesArray = jsonObject.getJSONArray(dateKey);
                WalletContainer entryContainer = new WalletContainer();

                // Iterate over the array of entries for each date
                for (int i = 0; i < entriesArray.length(); i++) {
                    entryContainer.addEntry(jsonToWalletEntry(entriesArray.getJSONObject(i)));
                }

                // Put the entry container into the map with the date as the key
                dateToEntriesMap.put(dateKey, entryContainer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateToEntriesMap;
    }

    public boolean createFileIfNotExist(@NonNull File file) {
        if (file.exists()) {
            return true;
        }
        try {
            // Create a new file
            boolean created = file.createNewFile();
            if (!created) {
                Log.d(TAG, "can`t create file " + file);
                return false;
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(new String("{}").getBytes());
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error creating file: " + e.getMessage());
            return false;
        }
        return true;
    }
}
