package com.example.l_assistant;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.example.l_assistant.ui.NewsFragment;
import com.example.l_assistant.ui.PassFragment;
import com.example.l_assistant.ui.WeatherFragment;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_DRAWER_NAV_VIEW_SELECTED_ID = "KEY_DRAWEE_NAV_VIEW_SELECTED_ID";


    private NewsFragment mNewsFragment;

    private WeatherFragment mWeatherFragment;

    private PassFragment mPassFragment;

    private DrawerLayout mDrawerLayout;

    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        initFragments(savedInstanceState);

        if (savedInstanceState != null) {
            int id = savedInstanceState.getInt(KEY_DRAWER_NAV_VIEW_SELECTED_ID, navView.getId());
            switch (id) {
                case R.id.nav_news:
                    showFragment(mNewsFragment);
                    break;
                case R.id.nav_weather:
                    showFragment(mWeatherFragment);
                    break;
                case R.id.nav_password:
                    showFragment(mPassFragment);
                    break;
            }
        } else {
            showFragment(mNewsFragment);
        }


        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        break;
                    case R.id.nav_news:
                        showFragment(mNewsFragment);
                        break;
                    case R.id.nav_weather:
                        break;
                    case R.id.nav_password:
                        break;
                    default:
                        break;
                }
                ft.commit();
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
//        navButton.setOnClickListener(v -> {
//                mDrawerLayout.openDrawer(GravityCompat.START);
//        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_DRAWER_NAV_VIEW_SELECTED_ID, navView.getId());
        FragmentManager fm = getSupportFragmentManager();
        if(mNewsFragment.isAdded()){
            fm.putFragment(outState, NewsFragment.class.getSimpleName(), mNewsFragment);
        }
    }

    private void initViews(){
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_news);
    }

    private void initFragments(Bundle saveInstanceState){
        FragmentManager fm = getSupportFragmentManager();
        if(saveInstanceState == null){
            mNewsFragment = NewsFragment.newInstance();
            mWeatherFragment = WeatherFragment.newInstance();
            mPassFragment = PassFragment.newInstance();
        }else{
            mNewsFragment = (NewsFragment) fm.getFragment(saveInstanceState, NewsFragment.class.getSimpleName());
            mWeatherFragment = (WeatherFragment) fm.getFragment(saveInstanceState, WeatherFragment.class.getSimpleName());
            mPassFragment = (PassFragment) fm.getFragment(saveInstanceState, PassFragment.class.getSimpleName());
        }

        if(!mNewsFragment.isAdded()){
            fm.beginTransaction()
                    .add(R.id.container_main, mNewsFragment, NewsFragment.class.getSimpleName())
                    .commit();
        }

        if(!mWeatherFragment.isAdded()){
            fm.beginTransaction()
                    .add(R.id.container_main, mWeatherFragment, WeatherFragment.class.getSimpleName())
                    .commit();
        }

        if(!mPassFragment.isAdded()){
            fm.beginTransaction()
                    .add(R.id.container_main, mPassFragment, PassFragment.class.getSimpleName())
                    .commit();
        }
    }

    private void showFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        if(fragment instanceof NewsFragment){
            fm.beginTransaction()
                    .show(mNewsFragment)
                    .hide(mWeatherFragment)
                    .hide(mPassFragment)
                    .commit();
        }else if(fragment instanceof WeatherFragment){
            fm.beginTransaction()
                    .show(mWeatherFragment)
                    .hide(mNewsFragment)
                    .hide(mPassFragment)
                    .commit();
        }else if(fragment instanceof PassFragment){
            fm.beginTransaction()
                    .show(mPassFragment)
                    .hide(mWeatherFragment)
                    .hide(mNewsFragment)
                    .commit();
        }
    }
}
