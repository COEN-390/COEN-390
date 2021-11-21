package com.coen390.maskdetector.ui.logout;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coen390.maskdetector.R;
import com.coen390.maskdetector.controllers.AuthenticationController;
import com.coen390.maskdetector.databinding.LogoutFragmentBinding;

public class LogoutFragment extends Fragment {

    private LogoutViewModel mViewModel;

    public static LogoutFragment newInstance() {
        return new LogoutFragment();
    }

    private LogoutViewModel logoutViewModel;
    private LogoutFragmentBinding binding;

    private AuthenticationController authenticationController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        logoutViewModel =
                new ViewModelProvider(this).get(LogoutViewModel.class);

        binding = LogoutFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        authenticationController = new AuthenticationController(getContext());
        authenticationController.endSession();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}