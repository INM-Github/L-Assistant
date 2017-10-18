package com.example.l_assistant.News.data.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.l_assistant.News.data.ZhihuDailyContent;
import com.example.l_assistant.News.data.datasource.ZhihuDailyContentDataSource;

/**
 * Created by zkd on 2017/10/17.
 * Concrete implementation to load {@link ZhihuDailyContent} from the data sources into a cache.
 * <p>
 *     Use the remote data source firstly, which is obtained from the server.
 *     If the remote data was not available, then use the local data source,
 *     which was from the locally persisted in database.
 */
public class ZhihuDailyContentRepository implements ZhihuDailyContentDataSource{

    @Nullable
    private static ZhihuDailyContentRepository INSTANCE = null;

    @NonNull
    private final ZhihuDailyContentDataSource mLocalDataSource;

    @NonNull
    private final ZhihuDailyContentDataSource mRemoteDataSource;

    @Nullable
    private ZhihuDailyContent mContent;

    private ZhihuDailyContentRepository(@NonNull ZhihuDailyContentDataSource localDataSource,
                                        @NonNull ZhihuDailyContentDataSource remoteDataSource){
        mLocalDataSource = localDataSource;
        mRemoteDataSource = remoteDataSource;
    }

    public static ZhihuDailyContentRepository getInstance(@NonNull ZhihuDailyContentDataSource localDataSource,
                                                          @NonNull ZhihuDailyContentDataSource remoteDataSource){
        if(INSTANCE == null){
            INSTANCE = new ZhihuDailyContentRepository(localDataSource, remoteDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    @Override
    public void getZhihuDailyContent(int id, @NonNull LoadZhihuDailyContentCallback callback) {
        if(mContent != null){
            callback.onContentLoaded(mContent);
            return;
        }

        mRemoteDataSource.getZhihuDailyContent(id, new LoadZhihuDailyContentCallback() {
            @Override
            public void onContentLoaded(@NonNull ZhihuDailyContent content) {
                if(mContent == null){
                    mContent = content;
                    saveContent(content);
                }
                callback.onContentLoaded(content);
            }

            @Override
            public void onDataNotAvailable() {
                mLocalDataSource.getZhihuDailyContent(id, new LoadZhihuDailyContentCallback() {
                    @Override
                    public void onContentLoaded(@NonNull ZhihuDailyContent content) {
                        if(mContent == null){
                            mContent = content;
                        }
                        callback.onContentLoaded(content);
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
    public void saveContent(@NonNull ZhihuDailyContent content) {
        // Note: Setting of timestamp was done in the {@link ZhihuDailyContentLocalDataSource} class.
        mLocalDataSource.saveContent(content);
        mRemoteDataSource.saveContent(content);
    }
}
