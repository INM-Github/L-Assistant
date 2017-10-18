package com.example.l_assistant.News.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.l_assistant.News.data.ZhihuDailyContent;

/**
 * Created by zkd on 2017/10/14.
 */

@Dao
public interface ZhihuDailyContentDao {

    @Query("SELECT * FROM zhihu_daily_content WHERE id = :id")
    ZhihuDailyContent queryContentById(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ZhihuDailyContent content);

    @Update
    void update(ZhihuDailyContent content);

    @Delete
    void delete(ZhihuDailyContent content);

}
