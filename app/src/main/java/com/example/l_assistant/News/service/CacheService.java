package com.example.l_assistant.News.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.preference.PreferenceManager;

import com.example.l_assistant.News.data.PostType;
import com.example.l_assistant.News.data.ZhihuDailyContent;
import com.example.l_assistant.News.data.ZhihuDailyNewsQuestion;
import com.example.l_assistant.News.database.AppDatabase;
import com.example.l_assistant.News.database.DatabaseCreator;
import com.example.l_assistant.News.retrofit.RetrofitService;
import com.example.l_assistant.News.util.InfoConstants;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CacheService extends Service {

    public static final String FLAG_ID = "flag_id";
    public static final String FLAG_TYPE = "flag_type";

    public static final String BROADCAST_FILTER_ACTION = "com.example.l_assistant.LOCAL_BROADCAST";

    private static final int MSG_CLEAR_CACHE_DONE = 1;

    @Nullable
    private AppDatabase mDb = null;

    private LocalReceiver mReceiver;

    private RetrofitService.ZhihuDailyService mZhihuService;

    private boolean mZhihuCacheDone = false;

    private final Handler mHandler = new Handler(message -> {
        switch (message.what) {
            case MSG_CLEAR_CACHE_DONE:
                this.stopSelf();
                break;
            default:
                break;
        }
        return true;
    });

    @Override
    public void onCreate() {
        super.onCreate();

        mReceiver = new LocalReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_FILTER_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);

        mZhihuService = new Retrofit.Builder()
                .baseUrl(RetrofitService.ZHIHU_DAILY_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitService.ZhihuDailyService.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mReceiver != null){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    // A local broadcast receiver that receives broadcast from the corresponding fragment.
    class LocalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int id = intent.getIntExtra(FLAG_ID, 0);
            @PostType
            int type = intent.getIntExtra(FLAG_TYPE, 0);
            switch (type) {
                case PostType.TYPE_ZHIHU:
                    cacheZhihuDailyContent(id);
                    break;
                case PostType.TYPE_DOUBAN:
//                    cacheDoubanContent(id);
                    break;
                case PostType.TYPE_GUOKR:
//                    cacheGuokrContent(id);
                    break;
                default:
                    break;
            }
        }

    }

    private void cacheZhihuDailyContent(int id) {
        if(mDb == null){
            DatabaseCreator creator = DatabaseCreator.getInstance();
            if(!creator.isDatabaseCreated()){
                creator.createDb(this);
            }
            mDb = creator.getDatabase();
        }

        new Thread(() -> {
            if(mDb != null && mDb.zhihuDailyContentDao().queryContentById(id) == null){
                mDb.beginTransaction();
                try {
                    // Call execute() rather than enqueue()
                    // or you will go back to main thread in onResponse() function.
                    ZhihuDailyContent tmp = mZhihuService.getZhihuContent(id).execute().body();
                    if(tmp != null){
                        mDb.zhihuDailyContentDao().insert(tmp);
                        mDb.setTransactionSuccessful();
                    }
                } catch (IOException e){
                    e.printStackTrace();
                } finally {
                    mDb.endTransaction();
                    mZhihuCacheDone = true;

                }
            }
        }).start();
    }

    private void clearTimeoutContent(){
        if(mDb == null){
            DatabaseCreator creator = DatabaseCreator.getInstance();
            if(!creator.isDatabaseCreated()){
                creator.createDb(this);
            }
            mDb = creator.getDatabase();

            int dayCount = getDayOfSavingArticles(
                    Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(CacheService.this).getString(InfoConstants.KEY_TIME_OF_SAVING_ARTICLES, "2")));

            new Thread(() -> {
                if (mDb != null) {
                    mDb.beginTransaction();
                    try {
                        long timeInMillis = Calendar.getInstance().getTimeInMillis() - dayCount * 24 * 60 * 60 * 1000;

                        // Clear cache of zhihu daily
                        List<ZhihuDailyNewsQuestion> zhihuTimeoutItems = mDb.zhihuDailyNewsDao().queryAllTimeoutItems(timeInMillis);
                        for (ZhihuDailyNewsQuestion q : zhihuTimeoutItems) {
                            mDb.zhihuDailyNewsDao().delete(q);
                            mDb.zhihuDailyContentDao().delete(mDb.zhihuDailyContentDao().queryContentById(q.getId()));
                        }
                        mHandler.sendEmptyMessage(MSG_CLEAR_CACHE_DONE);

                    } finally {
                        mDb.endTransaction();
                    }
                }
            }).start();
        }
    }

    private int getDayOfSavingArticles(int id){
        switch (id){
            case 0:
                return 1;
            case 1:
                return 5;
            case 2:
                return 15;
            case 3:
                return 30;
            default:
                return 15;
        }
    }
}
