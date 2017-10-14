package com.example.l_assistant;

import android.view.View;

/**
 * Created by zkd on 2017/10/14.
 */

public interface BaseView<T> {

    void setPresenter(T presenter);

    void initViews(View view);


}
