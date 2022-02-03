package com.moco.marvin.shoppingbuddy.Home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moco.marvin.shoppingbuddy.R;

/**
 * Created by Marvin.H on 10.06.18.
 */

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites_new, container, false);

        return view;
    }
}
