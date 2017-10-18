package com.example.l_assistant.News.data.repository;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.l_assistant.News.data.ZhihuDailyNewsQuestion;
import com.example.l_assistant.News.data.datasource.ZhihuDailyNewsDataSource;
import com.example.l_assistant.News.data.local.ZhihuDailyNewsLocalDataSource;
import com.example.l_assistant.News.data.remote.ZhihuDailyNewsRemoteDataSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zkd on 2017/10/17.
 * Concrete implementation to load {@link ZhihuDailyNewsQuestion}s from the data sources into a cache.
 * <p>
 *     Use the remote data source firstly, which is obtained from the server.
 *     If the remote data was not available, then use the local data source,
 *     which was from the locally persisted in database.
 */

public class ZhihuDailyNewsRepository implements ZhihuDailyNewsDataSource{

    @Nullable
    private static ZhihuDailyNewsRepository INSTANCE = null;

    @NonNull
    private final ZhihuDailyNewsDataSource mLocalDataSource;

    @NonNull
    private final ZhihuDailyNewsDataSource mRemoteDataSource;

    private Map<Integer, ZhihuDailyNewsQuestion> mCacheItems;

    // Prevent direct instantiation
    private ZhihuDailyNewsRepository(@NonNull ZhihuDailyNewsLocalDataSource localDataSource,
                                     @NonNull ZhihuDailyNewsRemoteDataSource remoteDataSource){
        this.mLocalDataSource = localDataSource;
        this.mRemoteDataSource = remoteDataSource;
    }

    public static ZhihuDailyNewsRepository getInstance(@NonNull ZhihuDailyNewsLocalDataSource localDataSource,
                                                       @NonNull ZhihuDailyNewsRemoteDataSource remoteDataSource){
        if(INSTANCE == null){
            INSTANCE = new ZhihuDailyNewsRepository(localDataSource, remoteDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    @Override
    public void getZhihuDailyNews(boolean forceUpdate, boolean clearCache, long date, @NonNull LoadZhihuDailyNewsCallback callback) {
        if(mCacheItems != null && !forceUpdate){
            callback.onNewsLoaded(new ArrayList<>(mCacheItems.values()));
            return;
        }

        // Get data by accessing network first.
        mRemoteDataSource.getZhihuDailyNews(false, clearCache, date, new LoadZhihuDailyNewsCallback(){

            @Override
            public void onNewsLoaded(@NonNull List<ZhihuDailyNewsQuestion> list) {
                refreshCache(clearCache, list);
                callback.onNewsLoaded(new ArrayList<>(mCacheItems.values()));
                // Save these item to database.
                saveAll(list);
            }

            @Override
            public void onDataNotAvailable() {
                mLocalDataSource.getZhihuDailyNews(false, false, date, new LoadZhihuDailyNewsCallback() {
                    @Override
                    public void onNewsLoaded(@NonNull List<ZhihuDailyNewsQuestion> list) {
                        refreshCache(clearCache, list);
                        callback.onNewsLoaded(new ArrayList<>(mCacheItems.values()));
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void getFavorites(@NonNull LoadZhihuDailyNewsCallback callback) {
        mLocalDataSource.getFavorites(new LoadZhihuDailyNewsCallback() {
            @Override
            public void onNewsLoaded(@NonNull List<ZhihuDailyNewsQuestion> list) {
                callback.onNewsLoaded(list);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void getItem(int itemId, @NonNull GetNewsItemCallback callback) {
        ZhihuDailyNewsQuestion cachedItem = getItemWithId(itemId);

        if(cachedItem != null){
            callback.onItemLoaded(cachedItem);
            return;
        }

        mLocalDataSource.getItem(itemId, new GetNewsItemCallback() {
            @Override
            public void onItemLoaded(@NonNull ZhihuDailyNewsQuestion item) {
                if(mCacheItems == null){
                    mCacheItems = new LinkedHashMap<Integer, ZhihuDailyNewsQuestion>();
                }
                mCacheItems.put(item.getId(), item);
                callback.onItemLoaded(item);
            }

            @Override
            public void onDataNotAvailable() {
                mRemoteDataSource.getItem(itemId, new GetNewsItemCallback() {
                    @Override
                    public void onItemLoaded(@NonNull ZhihuDailyNewsQuestion item) {
                        if(mCacheItems == null){
                            mCacheItems = new LinkedHashMap<Integer, ZhihuDailyNewsQuestion>();
                            callback.onItemLoaded(item);
                        }
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Nullable
    private ZhihuDailyNewsQuestion getItemWithId(int itemId) {
        return (mCacheItems == null || mCacheItems.isEmpty()) ? null : mCacheItems.get(itemId);
    }

    @Override
    public void favoriteItem(int itemId, boolean favorite) {
        mRemoteDataSource.favoriteItem(itemId, favorite);
        mLocalDataSource.favoriteItem(itemId, favorite);

        ZhihuDailyNewsQuestion cacheItem = getItemWithId(itemId);
        if (cacheItem != null) {
            cacheItem.setFavorite(favorite);
        }
    }

    @Override
    public void saveAll(@NonNull List<ZhihuDailyNewsQuestion> list) {
        mLocalDataSource.saveAll(list);
        mRemoteDataSource.saveAll(list);

        if (mCacheItems == null) {
            mCacheItems = new LinkedHashMap<>();
        }

        for (ZhihuDailyNewsQuestion item : list) {
            // Note:  Setting of timestamp was done in the {@link ZhihuDailyNewsRemoteDataSource} class.
            mCacheItems.put(item.getId(), item);
        }
    }

    private void refreshCache(boolean clearCache, List<ZhihuDailyNewsQuestion> list){
        if(mCacheItems == null){
            mCacheItems = new LinkedHashMap<>();
        }
        if(clearCache){
            mCacheItems.clear();
        }
        for (ZhihuDailyNewsQuestion item :list){
            mCacheItems.put(item.getId(), item);
        }
    }
}
