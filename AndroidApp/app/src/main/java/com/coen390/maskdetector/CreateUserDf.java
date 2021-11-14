package com.coen390.maskdetector;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.coen390.maskdetector.controllers.SharedPreferencesHelper;

public class CreateUserDf extends DialogFragment {

    private EditText editName, editEmail, editPassword;
    private Button saveButton, cancelButton;
    private SharedPreferencesHelper sharedPreferencesHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.df_create_user, container);

        editName = view.findViewById(R.id.editName);
        editEmail = view.findViewById(R.id.editEmail);
        editPassword = view.findViewById(R.id.editPassword);

        saveButton = view.findViewById(R.id.buttonSave);
        cancelButton = view.findViewById(R.id.buttonCancel);

        sharedPreferencesHelper = new SharedPreferencesHelper(getContext());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Temporary save text
                String name = editName.getText().toString();
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();

                if(name.equals("")){
                    Toast.makeText(getContext(), "Invalid name", Toast.LENGTH_LONG).show();
                    return;
                }
                else if(email.equals("")){
                    Toast.makeText(getContext(), "Invalid email", Toast.LENGTH_LONG).show();
                    return;
                }
                else if(password.length() < 6){
                    Toast.makeText(getContext(), "Invalid password", Toast.LENGTH_LONG).show();
                    return;
                }

//                if(databaseHelper.getProfile(studentId) != null){
//                    Toast.makeText(getContext(), "Student ID already exists", Toast.LENGTH_LONG).show();
//                    return;
//                } //TODO: check if the user exists by email (unless it's done automatically by Appwrite)

                // TODO: add a loading icon while waiting
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((AdminActivity) requireActivity()).setupRecyclerView();
                        dismiss();
                    }
                }, 5000);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }
}
