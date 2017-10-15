package com.example.l_assistant.timeline;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.l_assistant.R;

/**
 * Created by zkd on 2017/10/15.
 */

public class TimelineFragmentPaperAdapter extends FragmentPagerAdapter{

    private final int pageCount = 3;
    private String[] titles;

    private ZhihuDailyFragment mZhihuFragment;

    public TimelineFragmentPaperAdapter(@NonNull FragmentManager fm,
                                        @NonNull Context context,
                                        @NonNull ZhihuDailyFragment zhihuDailyFragment){
        super(fm);
        titles = new String[]{
                context.getString(R.string.zhihu_daily),
                context.getString(R.string.douban_moment),
                context.getString(R.string.guokr_handpick)};
        this.mZhihuFragment = zhihuDailyFragment;

    }


    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return mZhihuFragment;
        }else if(position == 1){

        }
        return mZhihuFragment;
    }

    @Override
    public int getCount() {
        return pageCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
