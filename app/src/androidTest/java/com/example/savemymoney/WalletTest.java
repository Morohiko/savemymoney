package com.example.savemymoney;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class WalletTest {
    private Context context;

    private Wallet wallet;

    @Before
    public void setUp() {
        // Get the application context for the tests
        context = ApplicationProvider.getApplicationContext();

        wallet = new Wallet(context.getCacheDir());
    }

    @After
    public void tearDown() {
        // Reset the singleton after each test
        Settings.destroyInstance();
        // Clean up any test files created
        wallet.removeCache();
    }

    @Test
    public void testWithdrawMoney() {
        Date date = new Date(2024, 9, 7, 10, 32, 59);
        wallet.withdrawMoney(date, 123, "data");
        int total = wallet.getSumOfTransactionsByDate(date);
        assertEquals(total, -123);

        date = new Date(2024, 9, 7, 11, 10, 13);
        wallet.withdrawMoney(date, 991, "data1");
        total = wallet.getSumOfTransactionsByDate(date);
        assertEquals(total, -1114);

        date = new Date(2024, 9, 6, 2, 23, 15);
        wallet.withdrawMoney(date, 301, "data2");
        total = wallet.getSumOfTransactionsByDate(date);
        assertEquals(total, -301);
        date = new Date(2024, 9, 7, 5, 54, 45);
        total = wallet.getSumOfTransactionsByDate(date);
        assertEquals(total, -1114);
    }

    @Test
    public void testDepositMoney() {
        Date date = new Date(2024, 9, 7, 10, 32, 59);
        wallet.depositMoney(date, 123, "data");
        int total = wallet.getSumOfTransactionsByDate(date);
        assertEquals(total, 123);

        date = new Date(2024, 9, 7, 11, 10, 13);
        wallet.depositMoney(date, 991, "data1");
        total = wallet.getSumOfTransactionsByDate(date);
        assertEquals(total, 1114);

        date = new Date(2024, 9, 6, 2, 23, 15);
        wallet.depositMoney(date, 301, "data2");
        total = wallet.getSumOfTransactionsByDate(date);
        assertEquals(total, 301);
        date = new Date(2024, 9, 7, 5, 54, 45);
        total = wallet.getSumOfTransactionsByDate(date);
        assertEquals(total, 1114);
    }

    /** @noinspection deprecation*/
    @Test
    public void testGetSumOfTransactionsByPeriod() {
        Date date = new Date(2024, 9, 5, 10, 32, 59);
        wallet.withdrawMoney(date, 35, "data");
        date = new Date(2024, 9, 6, 10, 32, 59);
        wallet.withdrawMoney(date, 25, "data");
        date = new Date(2024, 9, 7, 10, 12, 23);
        wallet.withdrawMoney(date, 45, "data");
        date = new Date(2024, 9, 7, 12, 45, 12);
        wallet.withdrawMoney(date, 50, "data");
        date = new Date(2024, 9, 7, 15, 23, 24);
        wallet.withdrawMoney(date, 15, "data");
        date = new Date(2024, 9, 7, 18, 30, 45);
        wallet.depositMoney(date, 15, "data");
        date = new Date(2024, 9, 8, 10, 22, 12);
        wallet.depositMoney(date, 45, "data");
        int total = wallet.getSumOfTransactionsByPeriod(new Date(2024, 9, 6), new Date(2024, 9, 7));
        assertEquals(total, -120);
    }
}
