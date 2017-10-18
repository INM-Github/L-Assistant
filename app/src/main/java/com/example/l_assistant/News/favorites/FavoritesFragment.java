package com.example.l_assistant.News.favorites;

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

public class FavoritesFragment extends Fragment {

    public FavoritesFragment() {
        // Empty constructor is needed as a fragment
    }

    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.framgent_favorites, container, false);

        return view;
    }
}
