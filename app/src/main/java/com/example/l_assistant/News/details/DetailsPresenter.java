package com.example.l_assistant.News.details;

import android.support.annotation.NonNull;

import com.example.l_assistant.News.data.ContentType;
import com.example.l_assistant.News.data.ZhihuDailyContent;
import com.example.l_assistant.News.data.datasource.ZhihuDailyContentDataSource;
import com.example.l_assistant.News.data.repository.ZhihuDailyContentRepository;
import com.example.l_assistant.News.data.repository.ZhihuDailyNewsRepository;
import com.example.l_assistant.R;

/**
 * Created by zkd on 2017/10/19.
 */

public class DetailsPresenter implements DetailsContract.Presenter{

    @NonNull
    private final DetailsContract.View mView;

    private ZhihuDailyNewsRepository mZhihuNewsRepository;
    private ZhihuDailyContentRepository mZhihuContentRepository;

    public DetailsPresenter(@NonNull DetailsContract.View view,
                            @NonNull ZhihuDailyNewsRepository zhihuNewsRepository,
                            @NonNull ZhihuDailyContentRepository zhihuContentRepository) {
        this.mView = view;
        mView.setPresenter(this);
        mZhihuNewsRepository = zhihuNewsRepository;
        mZhihuContentRepository = zhihuContentRepository;
    }

    @Override
    public void start() {

    }

    @Override
    public void favorite(ContentType type, int id, boolean favorite) {
        if (type == ContentType.TYPE_ZHIHU_DAILY) {
            mZhihuNewsRepository.favoriteItem(id, favorite);
        }
    }

    @Override
    public void loadZhihuDailyContent(int id) {
        mZhihuContentRepository.getZhihuDailyContent(id, new ZhihuDailyContentDataSource.LoadZhihuDailyContentCallback() {
            @Override
            public void onContentLoaded(@NonNull ZhihuDailyContent content) {
                if (mView.isActive()) {
                    mView.showZhihuDailyContent(content);
                }
            }

            @Override
            public void onDataNotAvailable() {
                if (mView.isActive()) {
                    mView.showMessage(R.string.something_wrong);
                }
            }
        });
    }

    @Override
    public void getLink(ContentType type, int requestCode, int id) {
        switch (type) {
            case TYPE_ZHIHU_DAILY:
                mZhihuContentRepository.getZhihuDailyContent(id, new ZhihuDailyContentDataSource.LoadZhihuDailyContentCallback() {
                    @Override
                    public void onContentLoaded(@NonNull ZhihuDailyContent content) {
                        if (mView.isActive()) {
                            String url = content.getShareUrl();
                            if (requestCode == DetailsFragment.REQUEST_SHARE) {
                                mView.share(url);
                            } else if (requestCode == DetailsFragment.REQUEST_COPY_LINK) {
                                mView.copyLink(url);
                            } else if (requestCode == DetailsFragment.REQUEST_OPEN_WITH_BROWSER) {
                                mView.openWithBrowser(url);
                            }
                        }
                    }

                    @Override
                    public void onDataNotAvailable() {
                        if (mView.isActive()) {
                            mView.showMessage(R.string.share_error);
                        }
                    }
                });
                default:
                    break;
        }
    }
}
