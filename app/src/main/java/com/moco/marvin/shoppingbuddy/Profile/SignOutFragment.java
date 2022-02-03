package com.moco.marvin.shoppingbuddy.Profile;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moco.marvin.shoppingbuddy.R;

/**
 * Created by Marvin.H on 13.06.18.
 */

public class SignOutFragment extends Fragment {

    private static final String TAG = "SignOutFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_out_new, container, false);

        return view;
    }
}
