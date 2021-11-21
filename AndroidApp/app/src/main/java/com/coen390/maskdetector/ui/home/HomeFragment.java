package com.coen390.maskdetector.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.coen390.maskdetector.R;
import com.coen390.maskdetector.controllers.AuthenticationController;
import com.coen390.maskdetector.controllers.SharedPreferencesHelper;
import com.coen390.maskdetector.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private SharedPreferencesHelper sharedPreferencesHelper;
    private AuthenticationController authenticationController;

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPreferencesHelper = new SharedPreferencesHelper(getContext());
        authenticationController = new AuthenticationController(getContext());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @NonNull
    private NavController getNavController() {
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        if (!(fragment instanceof NavHostFragment)) {
            throw new IllegalStateException("Activity " + this
                    + " does not have a NavHostFragment");
        }
        return ((NavHostFragment) fragment).getNavController();
    }
}