package com.example.savemymoney.ui.settings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private OnFragmentInteractionListener listener;

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

        Button removeCacheBtn = root.findViewById(R.id.remove_cache_btn);
        removeCacheBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog();
            }
        });
        return root;
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("removing cache");
        builder.setMessage("remove cache??");

        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                recreateCache
                if (listener != null) {
                    listener.onFragmentInteraction("removeCache");
                }
                Toast.makeText(getActivity(), "removed", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        newSettings = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String data);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}