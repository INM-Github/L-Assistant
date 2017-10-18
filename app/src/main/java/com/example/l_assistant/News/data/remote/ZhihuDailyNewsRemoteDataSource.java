package com.example.l_assistant.News.data.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.l_assistant.News.data.ZhihuDailyNews;
import com.example.l_assistant.News.data.ZhihuDailyNewsQuestion;
import com.example.l_assistant.News.data.datasource.ZhihuDailyNewsDataSource;
import com.example.l_assistant.News.retrofit.RetrofitService;
import com.example.l_assistant.News.util.DateFormat;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zkd on 2017/10/17.
 *
 * Implementation of the {@link ZhihuDailyNews} data source that accesses network.
 */

public class ZhihuDailyNewsRemoteDataSource implements ZhihuDailyNewsDataSource {

    @Nullable
    private static ZhihuDailyNewsRemoteDataSource INSTANCE = null;

    // Prevent direct instantiation.
    private ZhihuDailyNewsRemoteDataSource() {
    }

    public static ZhihuDailyNewsRemoteDataSource getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ZhihuDailyNewsRemoteDataSource();
        }
        return INSTANCE;
    }

    // The parameter forceUpdate and addToCache are ignored.
    @Override
    public void getZhihuDailyNews(boolean forceUpdate, boolean clearCache, long date, @NonNull LoadZhihuDailyNewsCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.ZHIHU_DAILY_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService.ZhihuDailyService service = retrofit.create(RetrofitService.ZhihuDailyService.class);

        service.getZhihuList(DateFormat.formatZhihuDailyDateLongToString(date))
             .enqueue(new Callback<ZhihuDailyNews>() {
                 @Override
                 public void onResponse(Call<ZhihuDailyNews> call, Response<ZhihuDailyNews> response) {
                     // Note: Only the timestamp of zhihu daily was set in remote source.
                     // The other two was set in repository due to structure of returning json.
                     long timestamp = DateFormat.formatZhihuDailyDateStringToLong(response.body().getDate());

                     Log.d("TAG", "onResponse: timestamp " + timestamp);

                     for (ZhihuDailyNewsQuestion item : response.body().getStories()){
                         item.setTimestamp(timestamp);
                     }
                     callback.onNewsLoaded(response.body().getStories());
                 }

                 @Override
                 public void onFailure(Call<ZhihuDailyNews> call, Throwable t) {
                     callback.onDataNotAvailable();
                 }
             });
    }

    @Override
    public void getFavorites(@NonNull LoadZhihuDailyNewsCallback callback) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void getItem(int itemId, @NonNull GetNewsItemCallback callback) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void favoriteItem(int itemId, boolean favorite) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void saveAll(@NonNull List<ZhihuDailyNewsQuestion> list) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }
}
