package com.example.l_assistant.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.l_assistant.R;
import com.example.l_assistant.favorites.FavoritesFragment;
import com.example.l_assistant.timeline.TimelineFragment;

/**
 * Created by zkd on 2017/10/15.
 */

public class NewsFragment extends Fragment{

    private static final String KEY_BOTTOM_NAV_VIEW_SELECTED_ID = "KEY_BOTTOM_NAV_VIEW_SELECTED_ID";

    private TimelineFragment mTimelineFragment;
    private InfoFragment mInfoFragment;
    private FavoritesFragment mFavoritesFragment;

    private BottomNavigationView mBottomNavigationView;

    public static NewsFragment newInstance(){
        return new NewsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            int id = savedInstanceState.getInt(KEY_BOTTOM_NAV_VIEW_SELECTED_ID, R.id.nav_timeline);
            switch (id) {
                case R.id.nav_timeline:
                    showFragment(mTimelineFragment);
                    break;
                case R.id.nav_favorites:
                    showFragment(mFavoritesFragment);
                    break;
                case R.id.nav_info:
                    showFragment(mInfoFragment);
                    break;
            }
        } else {
            showFragment(mTimelineFragment);
        }



        // Start the caching service.
//        startService(new Intent(MainActivity.this, CacheService.class));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_main, container, false);

        initViews(view);

        initFragments(savedInstanceState);

        mBottomNavigationView.setOnNavigationItemSelectedListener((menuItem -> {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            switch (menuItem.getItemId()) {
                case R.id.nav_timeline:
                    showFragment(mTimelineFragment);
                    break;

                case R.id.nav_favorites:
                    showFragment(mFavoritesFragment);
                    break;

                case R.id.nav_info:
                    showFragment(mInfoFragment);
                    break;

                default:
                    break;

            }
            ft.commit();
            return true;
        }));

        return view;
    }

    private void initViews(View view) {
        mBottomNavigationView = view.findViewById(R.id.bottom_nav);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_BOTTOM_NAV_VIEW_SELECTED_ID, mBottomNavigationView.getSelectedItemId());
        FragmentManager fm = getChildFragmentManager();
        if (mTimelineFragment.isAdded()) {
            fm.putFragment(outState, TimelineFragment.class.getSimpleName(), mTimelineFragment);
        }
        if (mFavoritesFragment.isAdded()) {
            fm.putFragment(outState, FavoritesFragment.class.getSimpleName(), mFavoritesFragment);
        }
        if (mInfoFragment.isAdded()) {
            fm.putFragment(outState, InfoFragment.class.getSimpleName(), mInfoFragment);
        }

    }

    private void initFragments(Bundle savedInstanceState) {
        FragmentManager fm = getChildFragmentManager();
        if (savedInstanceState == null) {
            mTimelineFragment = TimelineFragment.newInstance();
            mInfoFragment = InfoFragment.newInstance();
            mFavoritesFragment = FavoritesFragment.newInstance();
        } else {
            mTimelineFragment = (TimelineFragment) fm.getFragment(savedInstanceState, TimelineFragment.class.getSimpleName());
            mFavoritesFragment = (FavoritesFragment) fm.getFragment(savedInstanceState, FavoritesFragment.class.getSimpleName());
            mInfoFragment = (InfoFragment) fm.getFragment(savedInstanceState, InfoFragment.class.getSimpleName());
        }

        if (!mTimelineFragment.isAdded()) {
            fm.beginTransaction()
                    .add(R.id.container, mTimelineFragment, TimelineFragment.class.getSimpleName())
                    .commit();
        }

        if (!mFavoritesFragment.isAdded()) {
            fm.beginTransaction()
                    .add(R.id.container, mFavoritesFragment, FavoritesFragment.class.getSimpleName())
                    .commit();

        }

        if (!mInfoFragment.isAdded()) {
            fm.beginTransaction()
                    .add(R.id.container, mInfoFragment, InfoFragment.class.getSimpleName())
                    .commit();
        }
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fm = getChildFragmentManager();
        if (fragment instanceof TimelineFragment) {
            fm.beginTransaction()
                    .show(mTimelineFragment)
                    .hide(mInfoFragment)
                    .hide(mFavoritesFragment)
                    .commit();

        } else if (fragment instanceof InfoFragment) {
            fm.beginTransaction()
                    .show(mInfoFragment)
                    .hide(mTimelineFragment)
                    .hide(mFavoritesFragment)
                    .commit();
        } else if (fragment instanceof FavoritesFragment) {
            fm.beginTransaction()
                    .show(mFavoritesFragment)
                    .hide(mTimelineFragment)
                    .hide(mInfoFragment)
                    .commit();
        }
    }

}
