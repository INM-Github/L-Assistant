package com.example.l_assistant.News.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.l_assistant.News.data.ContentType;
import com.example.l_assistant.News.data.PostType;
import com.example.l_assistant.News.data.ZhihuDailyNewsQuestion;
import com.example.l_assistant.News.details.DetailsActivity;
import com.example.l_assistant.News.service.CacheService;
import com.example.l_assistant.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


/**
 * Created by zkd on 2017/10/15.
 *
 * Main UI for the zhihu daily news screen.
 * Displays a grid of {@link ZhihuDailyNewsQuestion}s.
 */

public class ZhihuDailyFragment extends Fragment
        implements ZhihuDailyContract.View {

    private ZhihuDailyContract.Presenter mPresenter;

    // View references.
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private View mEmptyView;
    private FloatingActionButton fab;

    private ZhihuDailyNewsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private int mYear, mMonth, mDay;

    private boolean mIsFirstLoad = true;
    private int mListSize = 0;

    public ZhihuDailyFragment() {
        // An empty constructor is needed as a fragment.
    }

    public static ZhihuDailyFragment newInstance() {
        return new ZhihuDailyFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timeline_page_fragment, container, false);

        initViews(view);

        mRefreshLayout.setOnRefreshListener(() -> {
            Calendar c = Calendar.getInstance();
            c.setTimeZone(TimeZone.getTimeZone("GMT+08"));
            mPresenter.loadNews(true, true, c.getTimeInMillis());
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    fab.hide();
                    if (mLayoutManager.findLastCompletelyVisibleItemPosition() == mListSize - 1) {
                        loadMore();
                    }
                } else {
                    fab.show();
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        c.set(mYear, mMonth, mDay);
        setLoadingIndicator(mIsFirstLoad);
        if (mIsFirstLoad) {
            mPresenter.loadNews(true, false, c.getTimeInMillis());
            mIsFirstLoad = false;
        } else {
            mPresenter.loadNews(false, false, c.getTimeInMillis());
        }

    }

    @Override
    public void setPresenter(ZhihuDailyContract.Presenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }

    @Override
    public void initViews(View view) {
        mRefreshLayout = view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorAccent));
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mEmptyView = view.findViewById(R.id.empty_view);
        fab = getActivity().findViewById(R.id.fab);
    }

    @Override
    public boolean isActive() {
        return isAdded() && isResumed();
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        mRefreshLayout.post(() -> mRefreshLayout.setRefreshing(active));
    }

    @Override
    public void showResult(@NonNull List<ZhihuDailyNewsQuestion> list) {
        if (mAdapter == null) {
            mAdapter = new ZhihuDailyNewsAdapter(list, getContext());
            mAdapter.setItemClickListener((v, i) -> {

                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.KEY_ARTICLE_ID, list.get(i).getId());
                intent.putExtra(DetailsActivity.KEY_ARTICLE_TYPE, ContentType.TYPE_ZHIHU_DAILY);
                intent.putExtra(DetailsActivity.KEY_ARTICLE_TITLE, list.get(i).getTitle());
                intent.putExtra(DetailsActivity.KEY_ARTICLE_IS_FAVORITE, list.get(i).isFavorite());
                startActivity(intent);

            });
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.updataData(list);
        }

        mListSize = list.size();

        mEmptyView.setVisibility(list.isEmpty() ? View.VISIBLE : View.INVISIBLE);

        // Cache data of the items
        for (ZhihuDailyNewsQuestion item : list) {
            Intent intent = new Intent(CacheService.BROADCAST_FILTER_ACTION);
            intent.putExtra(CacheService.FLAG_ID, item.getId());
            intent.putExtra(CacheService.FLAG_TYPE, PostType.TYPE_ZHIHU);
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
        }
    }

    private void loadMore() {
        Calendar c = Calendar.getInstance();
        c.set(mYear, mMonth, --mDay);
        mPresenter.loadNews(true, false, c.getTimeInMillis());
    }

    public void showDatePickerDialog() {
        Calendar c = Calendar.getInstance();
        c.set(mYear, mMonth, mDay);
        DatePickerDialog dialog = DatePickerDialog.newInstance((datePickerDialog, year, monthOfYear, dayOfMonth) -> {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            c.set(mYear, monthOfYear, mDay);

            mPresenter.loadNews(true, true, c.getTimeInMillis());

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        dialog.setMaxDate(Calendar.getInstance());

        Calendar minDate = Calendar.getInstance();
        minDate.set(2013, 5, 20);
        dialog.setMinDate(minDate);
        dialog.vibrate(false);

        dialog.show(getActivity().getFragmentManager(), ZhihuDailyFragment.class.getSimpleName());

    }

}
