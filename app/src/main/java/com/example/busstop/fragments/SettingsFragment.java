package com.example.busstop.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.busstop.LoginActivity;
import com.example.busstop.R;
import com.example.busstop.session.preference;

import java.util.Objects;


public class SettingsFragment extends Fragment {

    private View view;
    private Button logOut;

    public SettingsFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        logOut = view.findViewById(R.id.logout_btn);

        logOut.setOnClickListener(v ->{
            iniLoginActivity();
        });

        return view;
    }

    private void iniLoginActivity(){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        preference.clearData(getContext());
        startActivity(intent);
        requireActivity().finish();

    }
}