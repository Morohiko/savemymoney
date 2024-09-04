package com.example.savemymoney.ui.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.savemymoney.R;
import com.example.savemymoney.databinding.FragmentSettingsBinding;

import android.widget.Button;
import android.widget.EditText;

public class SettingsFragment extends Fragment {
    private static final String TAG = "SaveMyMoney:SettingsFragment";

    private FragmentSettingsBinding binding;

    private OnSettingsChangeListener mListener;

    public interface OnSettingsChangeListener {
        void onSettingChanged(String newValue);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Find the EditText and Button in the layout
        EditText budgetEditText = root.findViewById(R.id.budget_input_field);
        Button saveSettingsBtn = root.findViewById(R.id.save_settings_btn);

        // Set an OnClickListener for the button
        saveSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the value from the EditText
                String inputValue = budgetEditText.getText().toString();
                Log.d(TAG, "Update budget: " + inputValue);
                if (mListener != null) {
                    mListener.onSettingChanged(inputValue);
                }
            }
        });
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsChangeListener) {
            mListener = (OnSettingsChangeListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnSettingsChangeListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}