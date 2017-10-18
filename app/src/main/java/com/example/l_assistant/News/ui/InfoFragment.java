package com.example.l_assistant.News.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.l_assistant.R;

/**
 * Created by zkd on 2017/10/15.
 */

public class InfoFragment extends Fragment {

    public InfoFragment() {
        // Empty constructor is needed as a fragment
    }

    public static InfoFragment newInstance() {
        return new InfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);


        return view;
    }
}
