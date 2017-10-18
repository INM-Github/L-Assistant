package com.example.l_assistant.News.timeline;

import android.support.annotation.NonNull;

import com.example.l_assistant.News.data.ZhihuDailyContent;
import com.example.l_assistant.News.data.ZhihuDailyNewsQuestion;
import com.example.l_assistant.News.data.datasource.ZhihuDailyNewsDataSource;
import com.example.l_assistant.News.data.repository.ZhihuDailyNewsRepository;

import java.util.List;

/**
 * Created by zkd on 2017/10/15.
 */

public class ZhihuDailyPresenter implements ZhihuDailyContract.Presenter {

    @NonNull
    private final ZhihuDailyContract.View mView;

    @NonNull
    private final ZhihuDailyNewsRepository mRepository;

    public ZhihuDailyPresenter(@NonNull ZhihuDailyContract.View view,
                               @NonNull ZhihuDailyNewsRepository repository) {
        this.mView = view;
        this.mRepository = repository;
        this.mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void loadNews(boolean forceUpdate, boolean clearCache, long date) {
        mRepository.getZhihuDailyNews(forceUpdate, clearCache, date, new ZhihuDailyNewsDataSource.LoadZhihuDailyNewsCallback() {
            @Override
            public void onNewsLoaded(@NonNull List<ZhihuDailyNewsQuestion> list) {
                if(mView.isActive()){
                    mView.showResult(list);
                    mView.setLoadingIndicator(false);
                }
            }

            @Override
            public void onDataNotAvailable() {
                if(mView.isActive()){
                    mView.setLoadingIndicator(false);
                }
            }
        });
    }

}