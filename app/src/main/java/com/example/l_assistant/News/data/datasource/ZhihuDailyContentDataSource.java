package com.example.l_assistant.News.data.datasource;

import android.support.annotation.NonNull;

import com.example.l_assistant.News.data.ZhihuDailyContent;

/**
 * Created by zkd on 2017/10/17.
 *
 *   Main entry point for accessing the {@link ZhihuDailyContent} data.
 */

public interface ZhihuDailyContentDataSource {

    interface LoadZhihuDailyContentCallback {

        void onContentLoaded(@NonNull ZhihuDailyContent content);

        void onDataNotAvailable();

    }

    void getZhihuDailyContent(int id, @NonNull LoadZhihuDailyContentCallback callback);

    void saveContent(@NonNull ZhihuDailyContent content);

}
