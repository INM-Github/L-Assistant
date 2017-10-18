package com.example.l_assistant.News.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by zkd on 2017/10/14.
 */
@Retention(SOURCE)//源代码级别保留
@IntDef({PostType.TYPE_ZHIHU, PostType.TYPE_DOUBAN, PostType.TYPE_GUOKR})
public @interface PostType {

    int TYPE_ZHIHU = 0;

    int TYPE_DOUBAN = 1;

    int TYPE_GUOKR = 2;

}

