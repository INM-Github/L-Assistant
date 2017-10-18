package com.example.l_assistant.News.data.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.l_assistant.News.data.ZhihuDailyContent;
import com.example.l_assistant.News.data.datasource.ZhihuDailyContentDataSource;
import com.example.l_assistant.News.retrofit.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zkd on 2017/10/17.
 *
 * Implementation of the {@link ZhihuDailyContent} data source that accesses network.
 */

public class ZhihuDailyContentRemoteDataSource implements ZhihuDailyContentDataSource {

    @Nullable
    private static ZhihuDailyContentRemoteDataSource INSTANCE = null;

    // Prevent direct instantiation.
    private ZhihuDailyContentRemoteDataSource(){
    }

    public static ZhihuDailyContentRemoteDataSource getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ZhihuDailyContentRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void getZhihuDailyContent(int id, @NonNull LoadZhihuDailyContentCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.ZHIHU_DAILY_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService.ZhihuDailyService service = retrofit.create(RetrofitService.ZhihuDailyService.class);

        service.getZhihuContent(id)
                .enqueue(new Callback<ZhihuDailyContent>() {
                    @Override
                    public void onResponse(Call<ZhihuDailyContent> call, Response<ZhihuDailyContent> response) {
                        callback.onContentLoaded(response.body());
                    }

                    @Override
                    public void onFailure(Call<ZhihuDailyContent> call, Throwable t) {
                        callback.onDataNotAvailable();
                    }
                });
    }

    @Override
    public void saveContent(@NonNull ZhihuDailyContent content) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }
}
