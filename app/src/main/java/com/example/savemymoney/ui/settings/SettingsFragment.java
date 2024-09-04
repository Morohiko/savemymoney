package com.example.savemymoney.ui.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.savemymoney.R;
import com.example.savemymoney.Settings;
import com.example.savemymoney.databinding.FragmentSettingsBinding;

import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;

    private JSONObject newSettings = null;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Find the EditText and Button in the layout
        EditText budgetEditText = root.findViewById(R.id.budget_input_field);
        budgetEditText.setText(Integer.toString(Settings.getInstance().getBudget()));
        Button saveSettingsBtn = root.findViewById(R.id.save_settings_btn);

        // prepare to update settings
        newSettings = new JSONObject();

        // Set an OnClickListener for the button
        saveSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the value from the EditText
                String budgetValue = budgetEditText.getText().toString();
                if (budgetValue.length() > 1) {
                    try {
                        newSettings.put("budget", Integer.parseInt(budgetValue));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                Settings.getInstance().saveSettings(newSettings);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        newSettings = null;
    }
}