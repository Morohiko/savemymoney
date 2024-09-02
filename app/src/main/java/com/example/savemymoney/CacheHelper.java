package com.example.savemymoney;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class CacheHelper {

    private static final String TAG = "SaveMyMoney:CacheHelper";

    // Save JSON object to cache directory
    public static void saveJsonToCache(File cacheFile, JSONObject jsonObject) {
        try {
            FileOutputStream fos = new FileOutputStream(cacheFile);
            fos.write(jsonObject.toString().getBytes());
            fos.close();
            Log.d(TAG, "JSON saved to cache");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error saving JSON to cache", e);
        }
    }

    // Retrieve JSON object from cache directory
    public static JSONObject readJsonFromCache(File cacheFile) {
        StringBuilder jsonString = new StringBuilder();

        try {
            FileInputStream fis = new FileInputStream(cacheFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            reader.close();
            fis.close();

            Log.d(TAG, "JSON read from cache");
            return new JSONObject(jsonString.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error reading JSON from cache", e);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error parsing JSON from cache", e);
        }

        return null;
    }

    // Delete JSON file from cache directory
    public static boolean deleteJsonFromCache(File cacheFile) {
        boolean deleted = cacheFile.delete();
        if (deleted) {
            Log.d(TAG, "JSON deleted from cache");
        } else {
            Log.e(TAG, "Error deleting JSON from cache");
        }
        return deleted;
    }
}
