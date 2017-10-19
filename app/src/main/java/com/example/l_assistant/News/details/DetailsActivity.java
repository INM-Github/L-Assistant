package com.example.l_assistant.News.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.l_assistant.News.data.ContentType;
import com.example.l_assistant.News.data.local.ZhihuDailyContentLocalDataSource;
import com.example.l_assistant.News.data.local.ZhihuDailyNewsLocalDataSource;
import com.example.l_assistant.News.data.remote.ZhihuDailyContentRemoteDataSource;
import com.example.l_assistant.News.data.remote.ZhihuDailyNewsRemoteDataSource;
import com.example.l_assistant.News.data.repository.ZhihuDailyContentRepository;
import com.example.l_assistant.News.data.repository.ZhihuDailyNewsRepository;
import com.example.l_assistant.R;

/**
 * Created by zkd on 2017/10/17.
 */

public class DetailsActivity extends AppCompatActivity{
    public static final String KEY_ARTICLE_TYPE = "KEY_ARTICLE_TYPE";
    public static final String KEY_ARTICLE_ID = "KEY_ARTICLE_ID";
    public static final String KEY_ARTICLE_TITLE = "KEY_ARTICLE_TITLE";
    public static final String KEY_ARTICLE_IS_FAVORITE = "KEY_ARTICLE_IS_FAVORITE";

    private DetailsFragment mDetailsFragment;

    private ContentType mType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame);

        if (savedInstanceState != null) {
            mDetailsFragment = (DetailsFragment) getSupportFragmentManager().getFragment(savedInstanceState, DetailsFragment.class.getSimpleName());
        } else {
            mDetailsFragment = DetailsFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mDetailsFragment, DetailsFragment.class.getSimpleName())
                    .commit();
        }

        mType = (ContentType) getIntent().getSerializableExtra(KEY_ARTICLE_TYPE);
        if (mType == ContentType.TYPE_ZHIHU_DAILY) {

            new DetailsPresenter(mDetailsFragment,
                    ZhihuDailyNewsRepository.getInstance(ZhihuDailyNewsLocalDataSource.getInstance(DetailsActivity.this),
                            ZhihuDailyNewsRemoteDataSource.getInstance()),
                    ZhihuDailyContentRepository.getInstance(ZhihuDailyContentRemoteDataSource.getInstance(),
                            ZhihuDailyContentLocalDataSource.getInstance(DetailsActivity.this)));

        } else if (mType == ContentType.TYPE_DOUBAN_MOMENT) {

        } else if (mType == ContentType.TYPE_GUOKR_HANDPICK) {

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZhihuDailyContentRepository.destroyInstance();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mDetailsFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, DetailsFragment.class.getSimpleName(), mDetailsFragment);
        }
    }
}
