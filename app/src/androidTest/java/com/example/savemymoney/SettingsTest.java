package com.example.savemymoney;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class SettingsTest {

    private Settings settings;
    private Context context;
    private File mockCacheFile;

    @Before
    public void setUp() {
        // Get the application context for the tests
        context = ApplicationProvider.getApplicationContext();

        // Create the Settings instance (singleton)
        settings = Settings.getInstance();

        // Create a mock file for the settings cache
        mockCacheFile = new File(context.getCacheDir(), "savemymoneysettings.json");
    }

    @After
    public void tearDown() {
        // Reset the singleton after each test
        Settings.destroyInstance();
        // Clean up any test files created
        if (mockCacheFile.exists()) {
            mockCacheFile.delete();
        }
    }

    @Test
    public void testInstallSettingsCreatesFileIfNotExist() {
        settings.installSettings(context.getCacheDir());
        assertTrue(mockCacheFile.exists());
    }

    @Test
    public void testSaveSettingsUpdatesBudget() throws JSONException {
        // Create mock JSON with budget value
        JSONObject mockJsonSettings = new JSONObject();
        mockJsonSettings.put("budget", 1000);

        settings.installSettings(context.getCacheDir());
        settings.saveSettings(mockJsonSettings);

        assertEquals(1000, settings.getBudget());
    }

    @Test
    public void testGetBudgetNoBudgetInSettings() {
        settings.installSettings(context.getCacheDir());

        assertEquals(0, settings.getBudget());
    }

    @Test
    public void testSaveAndLoadSettings() throws JSONException {
        JSONObject mockJsonSettings = new JSONObject();
        mockJsonSettings.put("budget", 2000);

        settings.installSettings(context.getCacheDir());
        settings.saveSettings(mockJsonSettings);
        settings = Settings.getInstance();
        settings.installSettings(context.getCacheDir());

        assertEquals(2000, settings.getBudget());
    }
}
