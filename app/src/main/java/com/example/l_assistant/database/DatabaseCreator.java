package com.example.l_assistant.database;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.PipedReader;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zkd on 2017/10/14.
 */

public class DatabaseCreator {

    @Nullable
    private static DatabaseCreator INSTANCE = null;

    private AppDatabase mDb;
    //原子类AtomicBoolean
    //compareAndSet(exist, true),if AtomicBoolean == exist: set(true);
    private final AtomicBoolean mInitializing = new AtomicBoolean(true);
    private final AtomicBoolean mIsDbCreated = new AtomicBoolean(false);

    // For Singleton instantiation
    private static final byte[] LOCK = new byte[0];
    //PS：零长度的byte数组对象创建起来将比任何对象都经济――查看编译后的字节码：
    // 生成零长度的byte[]对象只需3条操作码，而Object lock = new Object()则需要7行操作码。

    //synchronized同步锁
    public synchronized static DatabaseCreator getInstance(){
        if(INSTANCE == null){
            synchronized (LOCK){
                if(INSTANCE == null){
                    INSTANCE = new DatabaseCreator();
                }
            }
        }
        return INSTANCE;
    }

    public void createDb(Context context){

        Log.d("DatabaseCreator", "Creating DB from " + Thread.currentThread().getName());

        if(!mInitializing.compareAndSet(true, false)){
            return;
        }

        new AsyncTask<Context, Void, Void>() {

            @Override
            protected Void doInBackground(Context... contexts) {
                Log.d("DatabaseCreator", "Starting bg job " + Thread.currentThread().getName());

                Context ctx = contexts[0].getApplicationContext();

                mDb = Room.databaseBuilder(ctx, AppDatabase.class, AppDatabase.DATABASE_NAME).build();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mIsDbCreated.set(true);
            }
        }.execute(context.getApplicationContext());


    }

    public boolean isDatabaseCreated() {
        return mIsDbCreated.get();
    }

    @Nullable
    public AppDatabase getDatabase() {
        return mDb;
    }
}

