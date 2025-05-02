package com.example.sonsor.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.sonsor.R;

public class HomeFragment extends Fragment {

    private Button btnSensors, btnTemperature, btnGraphs;

    public HomeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnSensors = view.findViewById(R.id.btn_sensors);
        btnTemperature = view.findViewById(R.id.btn_temperature);
        btnGraphs = view.findViewById(R.id.btn_graphs);

        // Animation sur le logo
        ImageView logo = view.findViewById(R.id.home_logo);
        logo.animate()
                .alpha(1f)
                .translationYBy(50f)
                .setDuration(800)
                .start();

        btnSensors.setOnClickListener(v -> replaceFragment(new SensorsFragment()));
        btnTemperature.setOnClickListener(v -> replaceFragment(new TempFragment()));
        btnGraphs.setOnClickListener(v -> replaceFragment(new TempFragment())); // À remplacer par GraphsFragment plus tard

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        if (getActivity() != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            transaction.replace(R.id.nav_host_fragment_content_main, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
