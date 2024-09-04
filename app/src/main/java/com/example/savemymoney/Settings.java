package com.example.savemymoney;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Settings {
    private static final String TAG = "SaveMyMoney:Settings";
    private static final String SETTINGS_FILENAME = "savemymoneysettings.json";

    private JSONObject jsonSettings;

    private static Settings instance = null;

    private File cacheFile;

    private Settings() {}

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }

    void installSettings(File cacheDir) {
        cacheFile = new File(cacheDir, SETTINGS_FILENAME);
        if (!createSettingsFileIfNotExist()) {
            Log.e(TAG, "can`t create settings file: " + cacheFile);
        }

        StringBuilder jsonString = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(cacheFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            jsonSettings = new JSONObject(jsonString.toString());
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean createSettingsFileIfNotExist() {
        if (cacheFile.exists()) {
            return true;
        }
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

    private void writeSettingsToFile() {
        try {
            // Write the updated JSON string to a file
            FileOutputStream fos = new FileOutputStream(cacheFile);
            fos.write(jsonSettings.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void saveSettings(JSONObject newJsonSettings) {
        try {
            if (newJsonSettings.has("budget")) {
                jsonSettings.put("budget", newJsonSettings.getInt("budget"));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        writeSettingsToFile();
    }

    public int getBudget() {
        if (!jsonSettings.has("budget")) {
            Log.i(TAG, "there are no budget in config, return 0");
            return 0;
        }
        try {
            return jsonSettings.getInt("budget");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
