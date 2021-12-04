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

import com.coen390.maskdetector.controllers.AuthenticationController;
import com.coen390.maskdetector.controllers.SharedPreferencesHelper;

import org.json.JSONException;

import io.appwrite.exceptions.AppwriteException;
/**
 * Class to create User pop-up where you can type in all the attributes declared.
 * Once saved, it is is on the Appwrite database and can be viewed on the app under Users.
 */
public class CreateUserDf extends DialogFragment {

    private EditText editName, editEmail, editPassword;
    private Button saveButton, cancelButton;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private AuthenticationController authenticationController;

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
        authenticationController = new AuthenticationController(getContext());

        saveButton.setOnClickListener(new View.OnClickListener() {
            LoadingSpinnerView loadingSpinnerView = new LoadingSpinnerView(getActivity());
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
                } else {
                    try {
                        authenticationController.createUser(email, password, name);
                    } catch (AppwriteException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //loadingSpinnerView spinner
                    loadingSpinnerView.startLoading();
                }

                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((UsersActivity) requireActivity()).setupRecyclerView();
                        dismiss();
                        //dismiss loadingSpinnerView spinner
                        loadingSpinnerView.dismissLoading();
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
