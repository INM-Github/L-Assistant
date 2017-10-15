package com.example.l_assistant.timeline;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.l_assistant.R;

/**
 * Created by zkd on 2017/10/15.
 */

public class TimelineFragment extends Fragment{

    private FloatingActionButton mFab;
    private TabLayout mTabLayout;

    private ZhihuDailyFragment mZhihuFragment;

    public TimelineFragment(){
        // Requires the empty constructor
    }

    public static TimelineFragment newInstance(){
        return new TimelineFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            FragmentManager fm = getChildFragmentManager();
            mZhihuFragment = (ZhihuDailyFragment) fm.getFragment(savedInstanceState, ZhihuDailyFragment.class.getSimpleName());

        }else{
            mZhihuFragment = ZhihuDailyFragment.newInstance();

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timeline_fragment, container, false);

        initViews(view);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 2){
                    mFab.hide();
                }else{
                    mFab.show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mFab.setOnClickListener(v -> {
            if(mTabLayout.getSelectedTabPosition() == 0){
                mZhihuFragment.showDatePickerDialog();
            }else{

            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager fm = getChildFragmentManager();
        if(mZhihuFragment.isAdded()){
            fm.putFragment(outState, ZhihuDailyFragment.class.getSimpleName(), mZhihuFragment);
        }
    }

    private void initViews(View view){
//        ViewPager mViewPaper = view.findViewById(R.id.view_paper);
//        mViewPaper.setAdapter(new TimelineFragmentPaperAdapter(
//                getChildFragmentManager(),
//                getContext(),
//                mZhihuFragment
//        ));
//        mViewPaper.setOffscreenPageLimit(3);
        mTabLayout = view.findViewById(R.id.tab_layout);
        mFab = view.findViewById(R.id.fab);
    }
}
