package com.example.l_assistant.News.retrofit;

import com.example.l_assistant.News.data.ZhihuDailyContent;
import com.example.l_assistant.News.data.ZhihuDailyNews;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by zkd on 2017/10/14.
 */

public interface RetrofitService {
    String ZHIHU_DAILY_BASE = "https://news-at.zhihu.com/api/4/news/";

    String DOUBAN_MOMENT_BASE = "https://moment.douban.com/api/";

    String GUOKR_HANDPICK_BASE = "http://apis.guokr.com/minisite/";

    interface ZhihuDailyService {

        @GET("before/{date}")
        Call<ZhihuDailyNews> getZhihuList(@Path("date") String date);

        @GET("{id}")
        Call<ZhihuDailyContent> getZhihuContent(@Path("id") int id);

    }

}
