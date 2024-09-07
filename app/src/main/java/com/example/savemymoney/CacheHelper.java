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
        cacheFile = cacheFl;
        boolean isSuccess = createFileIfNotExist();
        if (!isSuccess) {
            Log.e(TAG, "can`t create cache file");
        }
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

    void writeEntriesToJson(Map<String, WalletContainer> dateToEntriesMap) {
        JSONObject jsonObject = new JSONObject();

        try {
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

    Map<String, WalletContainer> readEntriesFromJson() {
        Map<String, WalletContainer> dateToEntriesMap = new HashMap<>();

        StringBuilder jsonString = new StringBuilder();

        try {
            FileInputStream fis = new FileInputStream(cacheFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            Log.d(TAG, "readEntriesFromJson: jsonString = " + jsonString);

            // Parse the JSON string
            JSONObject jsonObject = new JSONObject(jsonString.toString());

            // Iterate over the keys (dates)
            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String dateKey = keys.next();

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

    private boolean createCacheFile() {
        try {
            // Create a new file
            boolean created = cacheFile.createNewFile();
            if (!created) {
                Log.d(TAG, "can`t create file " + cacheFile);
                return false;
            }
            FileOutputStream fos = new FileOutputStream(cacheFile);
            fos.write(new String("{}").getBytes());
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error creating file: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean createFileIfNotExist() {
        if (cacheFile.exists()) {
            return true;
        }
        return createCacheFile();
    }

    public boolean recreateCache() {
        if (cacheFile.exists()) {
            cacheFile.delete();
        }
        return createCacheFile();
    }
}
