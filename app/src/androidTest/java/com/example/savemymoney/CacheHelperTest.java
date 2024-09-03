package com.example.savemymoney;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class CacheHelperTest {

    private Context context;
    private static final String cacheFileName = "savemymoneycachetest.json";
    private File cacheFile;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        cacheFile = new File(context.getCacheDir() + cacheFileName);
    }

    @After
    public void tearDown() {
        if (cacheFile.exists()) {
            cacheFile.delete();
        }
    }

    @Test
    public void testJsonToWalletEntry() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("time", "09:05:22");
        obj.put("amount", 10);
        obj.put("desc", "tea");

        CacheHelper cacheHelper = new CacheHelper(cacheFile);
        WalletEntry entry = cacheHelper.jsonToWalletEntry(obj);

        assertEquals(entry.getTime(), "09:05:22");
        assertEquals(entry.getAmount(), 10);
        assertEquals(entry.getDescription(), "tea");
    }

    @Test
    public void testWalletEntryToJson() throws JSONException {
        CacheHelper cacheHelper = new CacheHelper(cacheFile);
        WalletEntry entry = new WalletEntry("10:10:13", 15, "fpv");

        JSONObject obj = cacheHelper.walletEntryToJson(entry);

        assertEquals(obj.getString("time"), "10:10:13");
        assertEquals(obj.getInt("amount"), 15);
        assertEquals(obj.getString("desc"), "fpv");
    }

    @Test
    public void testReadEntriesFromJson() throws Exception {
        // Create a JSON string similar to what you'd expect in the file
        String jsonContent = "{"
                + "\"03-09-24\": ["
                + "    {\"time\": \"09:05\", \"amount\": 10, \"desc\": \"tea\"},"
                + "    {\"time\": \"10:10\", \"amount\": 15, \"desc\": \"fpv\"}"
                + "],"
                + "\"04-09-24\": ["
                + "    {\"time\": \"13:24\", \"amount\": 35, \"desc\": \"cigarettes\"},"
                + "    {\"time\": \"15:10\", \"amount\": 20, \"desc\": \"fuel\"}"
                + "]"
                + "}";

        // Write the JSON content to a file in the cache directory
        try (FileOutputStream fos = new FileOutputStream(cacheFile)) {
            fos.write(jsonContent.getBytes());
        }
        CacheHelper cacheHelper = new CacheHelper(cacheFile);

        // Call the method to read the JSON file into the map
        Map<String, WalletContainer> dateToEntriesMap = cacheHelper.readEntriesFromJson();

        // Verify the size of the map
        assertNotNull(dateToEntriesMap);
        assertEquals(2, dateToEntriesMap.size());

        // Verify entries for "03-09-24"
        WalletContainer container1 = dateToEntriesMap.get("03-09-24");
        assertNotNull(container1);
        assertEquals(2, container1.getEntries().size());

        WalletEntry entry1 = container1.getEntries().get(0);
        assertEquals("09:05", entry1.getTime());
        assertEquals(10, entry1.getAmount());
        assertEquals("tea", entry1.getDescription());

        WalletEntry entry2 = container1.getEntries().get(1);
        assertEquals("10:10", entry2.getTime());
        assertEquals(15, entry2.getAmount());
        assertEquals("fpv", entry2.getDescription());

        // Verify entries for "04-09-24"
        WalletContainer container2 = dateToEntriesMap.get("04-09-24");
        assertNotNull(container2);
        assertEquals(2, container2.getEntries().size());

        WalletEntry entry3 = container2.getEntries().get(0);
        assertEquals("13:24", entry3.getTime());
        assertEquals(35, entry3.getAmount());
        assertEquals("cigarettes", entry3.getDescription());

        WalletEntry entry4 = container2.getEntries().get(1);
        assertEquals("15:10", entry4.getTime());
        assertEquals(20, entry4.getAmount());
        assertEquals("fuel", entry4.getDescription());
    }

    @Test
    public void testReadEntriesFromJson_EmptyFile() throws Exception {
        // Create an empty JSON string
        String jsonContent = "{}";

        // Write the empty JSON content to a file in the cache directory
        try (FileOutputStream fos = new FileOutputStream(cacheFile)) {
            fos.write(jsonContent.getBytes());
        }

        CacheHelper cacheHelper = new CacheHelper(cacheFile);
        // Call the method to read the JSON file into the map
        Map<String, WalletContainer> dateToEntriesMap = cacheHelper.readEntriesFromJson();

        // Verify that the map is empty
        assertNotNull(dateToEntriesMap);
        assertTrue(dateToEntriesMap.isEmpty());
    }

    @Test
    public void testWriteEntriesToJson() throws Exception {
        // Prepare the map with sample data
        Map<String, WalletContainer> dateToEntriesMap = new HashMap<>();

        // Sample data for "03-09-24"
        WalletContainer entriesForFirstDate = new WalletContainer();
        entriesForFirstDate.addEntry(new WalletEntry("09:05", 10, "tea"));
        entriesForFirstDate.addEntry(new WalletEntry("10:10", 15, "fpv"));
        dateToEntriesMap.put("03-09-24", entriesForFirstDate);

        // Sample data for "04-09-24"
        WalletContainer entriesForSecondDate = new WalletContainer();
        entriesForSecondDate.addEntry(new WalletEntry("13:24", 35, "cigarettes"));
        entriesForSecondDate.addEntry(new WalletEntry("15:10", 20, "fuel"));
        dateToEntriesMap.put("04-09-24", entriesForSecondDate);

        CacheHelper cacheHelper = new CacheHelper(cacheFile);
        // Write the map to a JSON file
        cacheHelper.writeEntriesToJson(dateToEntriesMap);

        // Read the file back and verify its contents
        StringBuilder jsonString = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(cacheFile);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
        }

        // Parse the JSON string and verify its structure and contents
        JSONObject jsonObject = new JSONObject(jsonString.toString());

        // Verify the JSON object for "03-09-24"
        assertTrue(jsonObject.has("03-09-24"));
        JSONArray array1 = jsonObject.getJSONArray("03-09-24");
        assertEquals(2, array1.length());

        JSONObject entry1 = array1.getJSONObject(0);
        assertEquals("09:05", entry1.getString("time"));
        assertEquals(10, entry1.getInt("amount"));
        assertEquals("tea", entry1.getString("desc"));

        JSONObject entry2 = array1.getJSONObject(1);
        assertEquals("10:10", entry2.getString("time"));
        assertEquals(15, entry2.getInt("amount"));
        assertEquals("fpv", entry2.getString("desc"));

        // Verify the JSON object for "04-09-24"
        assertTrue(jsonObject.has("04-09-24"));
        JSONArray array2 = jsonObject.getJSONArray("04-09-24");
        assertEquals(2, array2.length());

        JSONObject entry3 = array2.getJSONObject(0);
        assertEquals("13:24", entry3.getString("time"));
        assertEquals(35, entry3.getInt("amount"));
        assertEquals("cigarettes", entry3.getString("desc"));

        JSONObject entry4 = array2.getJSONObject(1);
        assertEquals("15:10", entry4.getString("time"));
        assertEquals(20, entry4.getInt("amount"));
        assertEquals("fuel", entry4.getString("desc"));
    }

    @Test
    public void testWriteEntriesToJson_EmptyMap() throws Exception {
        // Prepare an empty map
        Map<String, WalletContainer> dateToEntriesMap = new HashMap<>();

        CacheHelper cacheHelper = new CacheHelper(cacheFile);
        // Write the empty map to a JSON file
        cacheHelper.writeEntriesToJson(dateToEntriesMap);

        // Read the file back and verify that it contains an empty JSON object
        StringBuilder jsonString = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(cacheFile);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
        }

        // Parse the JSON string and verify that it's empty
        JSONObject jsonObject = new JSONObject(jsonString.toString());
        assertTrue(jsonObject.length() == 0);
    }
}
