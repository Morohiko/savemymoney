package com.example.savemymoney.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.savemymoney.MainActivity;
import com.example.savemymoney.R;
import com.example.savemymoney.Settings;
import com.example.savemymoney.Wallet;
import com.example.savemymoney.databinding.FragmentHomeBinding;
import com.example.savemymoney.ui.settings.SettingsFragment;
import com.example.savemymoney.ui.settings.SettingsViewModel;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {
    private static final String TAG = "SaveMyMoney:HomeFragment";

    private FragmentHomeBinding binding;
    private FragmentCommandListener commandListener;

    private ProgressBar progressToday;
    private ProgressBar progressWeek;
    private ProgressBar progressMonth;
    private TextView tvTodayLeft;
    private TextView tvWeekLeft;
    private TextView tvMonthLeft;

    private Wallet wallet;

    private View root;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        // Find the Button by its ID
        progressToday = root.findViewById(R.id.progress_today);
        progressWeek = root.findViewById(R.id.progress_week);
        progressMonth = root.findViewById(R.id.progress_month);

        Button withdrawBtn = root.findViewById(R.id.btn_withdraw);
        Button depositBtn = root.findViewById(R.id.btn_deposit);

        tvTodayLeft = root.findViewById(R.id.tv_today_left);
        tvWeekLeft = root.findViewById(R.id.tv_week_left);
        tvMonthLeft = root.findViewById(R.id.tv_month_left);

        // Set a click listener
        withdrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "withdrawBtn: onClicklistener");
                newWithdraw();
            }
        });

        depositBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "depositBtn: onClickListener");
                newDeposit();
            }
        });

        File cacheDir = root.getContext().getCacheDir();
        Log.d(TAG, "onCreate: cacheDir = " + cacheDir);
        Settings.getInstance().installSettings(cacheDir);
        wallet = new Wallet(cacheDir);

        updateTotalOnScreen();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        updateTotalOnScreen();
    }

    private void newWithdraw() {
        final EditText priceInput = new EditText(root.getContext());
        priceInput.setHint("price");
        priceInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText descInput = new EditText(root.getContext());
        descInput.setHint("desc");

        LinearLayout layout = new LinearLayout(root.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(priceInput);
        layout.addView(descInput);

        AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
        builder.setTitle("withdraw:");

        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String priceValue = priceInput.getText().toString();
                String descValue = descInput.getText().toString();

                wallet.withdrawMoney(new Date()/*today*/, Integer.parseInt(priceValue), descValue);

                Toast.makeText(root.getContext(), "saved:\nprice: " + priceValue + "\ndesc: " + descValue, Toast.LENGTH_LONG).show();

                // update screen
                updateTotalOnScreen();
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void newDeposit() {
        final EditText priceInput = new EditText(root.getContext());
        priceInput.setHint("price");
        priceInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText descInput = new EditText(root.getContext());
        descInput.setHint("desc");

        LinearLayout layout = new LinearLayout(root.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(priceInput);
        layout.addView(descInput);

        AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
        builder.setTitle("deposit:");

        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String priceValue = priceInput.getText().toString();
                String descValue = descInput.getText().toString();

                wallet.depositMoney(new Date(), Integer.parseInt(priceValue), descValue);

                Toast.makeText(root.getContext(), "saved:\nprice: " + priceValue + "\ndesc: " + descValue, Toast.LENGTH_LONG).show();

                // update screen
                updateTotalOnScreen();
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @SuppressLint("SetTextI18n")
    private void updateBudgetForToday() {
        int total = wallet.getSumOfTransactionsByDate(new Date() /*today*/);
        int budgetForToday = Settings.getInstance().getBudget() / 30;
        int remain = budgetForToday + total;
        tvTodayLeft.setText(Integer.toString(remain));
        int percent = remain < 0 ? 100 : 100 - (int)(((float)remain / (float) budgetForToday) * 100F);
        progressToday.setProgress(percent, true);
    }

    @SuppressLint("SetTextI18n")
    private void updateBudgetForWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date dateWeekAgo = calendar.getTime();
        calendar.clear();
        int total = wallet.getSumOfTransactionsByPeriod(dateWeekAgo, new Date());
        int budgetForWeek = (int) (Settings.getInstance().getBudget() / 4.3);
        int remain = budgetForWeek + total;
        tvWeekLeft.setText(Integer.toString(remain));
        int percent = remain < 0 ? 100 : 100 - (int)(((float)remain / (float) budgetForWeek) * 100F);
        progressWeek.setProgress(percent, true);

    }

    @SuppressLint("SetTextI18n")
    private void updateBudgetForMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        Date dateMonthAgo = calendar.getTime();
        int total = wallet.getSumOfTransactionsByPeriod(dateMonthAgo, new Date());
        int budgetForMonth = Settings.getInstance().getBudget();
        int remain = budgetForMonth + total;
        tvMonthLeft.setText(Integer.toString(remain));
        int percent = remain < 0 ? 100 : 100 - (int)(((float)remain / (float) budgetForMonth) * 100F);
        progressMonth.setProgress(percent, true);

        Log.d(TAG, "budgetForMonth = " + budgetForMonth + ", total = " + total + " remain = " + remain + ", percent = " + percent);
    }

    public void updateTotalOnScreen() {
        updateBudgetForToday();
        updateBudgetForWeek();
        updateBudgetForMonth();
    }

    public interface FragmentCommandListener {
        void onFragmentCommand(String command);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentCommandListener) {
            commandListener = (FragmentCommandListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentCommandListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        commandListener = null;
    }

    public void removeCache() {
        Log.d(TAG, "remove cache !!!!!!!!!!!!!");

    }
    // Method to receive command from activity
    public void receiveCommand(String cmd) {
        Log.d(TAG, "receiveCommand");
        if (cmd.equals("removeCache")) {
            Log.d(TAG, "remove cache !!!!!!!!!!!!!");

            wallet.recreateCache();
            // update screen
            updateTotalOnScreen();
        } else {
            Log.w(TAG, "unrecognized cmd");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        // Observe the command LiveData
        homeViewModel.getCommand().observe(getViewLifecycleOwner(), cmd -> {
            Log.d(TAG, "onViewCreated observer");
            if (cmd != null) {
                Log.d(TAG, "newcommand = " + cmd);
                wallet.recreateCache();
                // update screen
                updateTotalOnScreen();
            } else {
                Log.d(TAG, "newcommand = null");
            }
        });

    }
}