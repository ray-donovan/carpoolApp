package com.edu.carpool;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class localfrag extends Fragment {

    EditText nameEditText, phoneEditText;
    Button saveButton,clearButton;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_localfrag, container, false);

        nameEditText = view.findViewById(R.id.name);
        phoneEditText = view.findViewById(R.id.phone);
        saveButton = view.findViewById(R.id.button);
        clearButton = view.findViewById(R.id.btnclear);
        phoneEditText.setFilters(new InputFilter[]{new NumericInputFilter()});

        loadPreviousData();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameEditText.getText().toString();
                String phone = phoneEditText.getText().toString();

                boolean fileSave = saveData(name, phone);
                if (fileSave) {
                    Toast.makeText(requireContext(), "File saved successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to save the file. Please try again.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear SharedPreferences data
                clearSharedPreferencesData();
                // Clear EditText fields
                nameEditText.setText("");
                phoneEditText.setText("");
            }
        });

        return view;
    }

    private void clearSharedPreferencesData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void loadPreviousData() {

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String savedName = sharedPreferences.getString("Name", "");
        String savedPhone = sharedPreferences.getString("Phone", "");


        nameEditText.setText(savedName);
        phoneEditText.setText(savedPhone);
    }
    private boolean saveData(String name, String phone) {
        try {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Name", name);
            editor.putString("Phone", phone);
            editor.apply();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class NumericInputFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (!Character.isDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }
    }
}