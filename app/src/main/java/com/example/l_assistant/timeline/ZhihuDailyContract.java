package com.example.l_assistant.timeline;

import android.support.annotation.NonNull;

import com.example.l_assistant.BasePresenter;
import com.example.l_assistant.BaseView;
import com.example.l_assistant.data.ZhihuDailyNewsQuestion;

import java.util.List;

/**
 * Created by zkd on 2017/10/15.
 */

public class ZhihuDailyContract {

    interface View extends BaseView<Presenter> {

        boolean isActive();

        void setLoadingIndicator(boolean active);

        void showResult(@NonNull List<ZhihuDailyNewsQuestion> list);

    }

    interface Presenter extends BasePresenter {

        void loadNews(boolean forceUpdate, boolean clearCache, long date);

    }

}
