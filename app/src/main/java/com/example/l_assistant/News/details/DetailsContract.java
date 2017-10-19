package com.example.l_assistant.News.details;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.example.l_assistant.BasePresenter;
import com.example.l_assistant.BaseView;
import com.example.l_assistant.News.data.ContentType;
import com.example.l_assistant.News.data.ZhihuDailyContent;

/**
 * Created by zkd on 2017/10/19.
 */

public interface DetailsContract {

    interface View extends BaseView<Presenter> {
        void showMessage(@StringRes int stringRes);

        boolean isActive();

        void showZhihuDailyContent(@NonNull ZhihuDailyContent content);

        void share(@Nullable String link);

        void copyLink(@Nullable String link);

        void openWithBrowser(@Nullable String link);
    }

    interface Presenter extends BasePresenter {
        void favorite(ContentType type, int id, boolean favorite);

        void loadZhihuDailyContent(int id);

        void getLink(ContentType type, int requestCode, int id);
    }
}
