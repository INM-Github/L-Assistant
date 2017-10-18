package com.example.l_assistant.News.data.local;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.l_assistant.News.data.ZhihuDailyNewsQuestion;
import com.example.l_assistant.News.data.datasource.ZhihuDailyNewsDataSource;
import com.example.l_assistant.News.database.AppDatabase;
import com.example.l_assistant.News.database.DatabaseCreator;

import java.util.List;

/**
 * Created by zkd on 2017/10/17.
 *
 *  Concrete implementation of a {@link ZhihuDailyNewsQuestion} data source as database.
 */

public class ZhihuDailyNewsLocalDataSource implements ZhihuDailyNewsDataSource {

    @Nullable
    private static ZhihuDailyNewsLocalDataSource INSTANCE = null;

    @Nullable
    private AppDatabase mDb = null;

    private ZhihuDailyNewsLocalDataSource(@NonNull Context context){
        DatabaseCreator creator = DatabaseCreator.getInstance();
        if(!creator.isDatabaseCreated()){
            creator.createDb(context);
        }
    }

    public static ZhihuDailyNewsLocalDataSource getInstance(@NonNull Context context){
        if(INSTANCE == null){
            INSTANCE = new ZhihuDailyNewsLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void getZhihuDailyNews(boolean forceUpdate, boolean clearCache, long date, @NonNull LoadZhihuDailyNewsCallback callback) {

        if(mDb == null){
            mDb = DatabaseCreator.getInstance().getDatabase();
        }

        if(mDb != null){
            new AsyncTask<Void, Void, List<ZhihuDailyNewsQuestion>>(){

                @Override
                protected List<ZhihuDailyNewsQuestion> doInBackground(Void... voids) {
                    return mDb.zhihuDailyNewsDao().queryAllByDate(date);
                }

                @Override
                protected void onPostExecute(List<ZhihuDailyNewsQuestion> list) {
                    super.onPostExecute(list);
                    if(list == null){
                        callback.onDataNotAvailable();
                    }else{
                        callback.onNewsLoaded(list);
                    }
                }
            }.execute();
        }
    }

    @Override
    public void getFavorites(@NonNull LoadZhihuDailyNewsCallback callback) {
        if(mDb == null){
            mDb = DatabaseCreator.getInstance().getDatabase();
        }else {
            new AsyncTask<Void, Void, List<ZhihuDailyNewsQuestion>>(){

                @Override
                protected List<ZhihuDailyNewsQuestion> doInBackground(Void... voids) {
                    return mDb.zhihuDailyNewsDao().queryAllFavorites();
                }

                @Override
                protected void onPostExecute(List<ZhihuDailyNewsQuestion> list) {
                    super.onPostExecute(list);
                    if(list == null){
                        callback.onDataNotAvailable();
                    }else{
                        callback.onNewsLoaded(list);
                    }
                }
            }.execute();
        }
    }

    @Override
    public void getItem(int itemId, @NonNull GetNewsItemCallback callback) {
        if(mDb == null){
            mDb = DatabaseCreator.getInstance().getDatabase();
        }else{
            new AsyncTask<Void, Void, ZhihuDailyNewsQuestion>(){

                @Override
                protected ZhihuDailyNewsQuestion doInBackground(Void... voids) {
                    return mDb.zhihuDailyNewsDao().queryItemById(itemId);
                }

                @Override
                protected void onPostExecute(ZhihuDailyNewsQuestion item) {
                    super.onPostExecute(item);
                    if(item == null){
                        callback.onDataNotAvailable();
                    }else{
                        callback.onItemLoaded(item);
                    }
                }
            }.execute();
        }
    }

    @Override
    public void favoriteItem(int itemId, boolean favorite) {
        if(mDb == null){
            mDb = DatabaseCreator.getInstance().getDatabase();
        }else{
            new Thread(() -> {
                ZhihuDailyNewsQuestion tmp = mDb.zhihuDailyNewsDao().queryItemById(itemId);
                tmp.setFavorite(favorite);
                mDb.zhihuDailyNewsDao().update(tmp);
            }).start();
        }
    }

    @Override
    public void saveAll(@NonNull List<ZhihuDailyNewsQuestion> list) {
        if(mDb == null){
            mDb = DatabaseCreator.getInstance().getDatabase();
        } else {
            new Thread(() -> {
                mDb.beginTransaction();
                try {
                    mDb.zhihuDailyNewsDao().insertAll(list);
                    mDb.setTransactionSuccessful();
                } finally {
                    mDb.endTransaction();
                }
            }).start();
        }
    }
}
