package com.example.l_assistant.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.l_assistant.data.ZhihuDailyContent;
import com.example.l_assistant.data.ZhihuDailyNewsQuestion;
import com.example.l_assistant.database.dao.ZhihuDailyContentDao;
import com.example.l_assistant.database.dao.ZhihuDailyNewsDao;

/**
 * Created by zkd on 2017/10/14.
 */

@Database(entities = {
        ZhihuDailyNewsQuestion.class,
        ZhihuDailyContent.class,},
        version = 1)
public abstract class AppDatabase extends RoomDatabase {

    static final String DATABASE_NAME = "L-Assistant-db";

    public abstract ZhihuDailyNewsDao zhihuDailyNewsDao();

    public abstract ZhihuDailyContentDao zhihuDailyContentDao();

}
