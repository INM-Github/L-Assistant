package com.example.l_assistant.News.details;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.l_assistant.News.customtabs.CustomTabHelper;
import com.example.l_assistant.News.data.ContentType;
import com.example.l_assistant.News.data.ZhihuDailyContent;
import com.example.l_assistant.R;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by zkd on 2017/10/19.
 * Main UI for the details screen.
 * Display the content of {@link ZhihuDailyContent}, {@link DoubanMomentContent} and {@link GuokrHandpickContentResult}.
 * Shown by {@link DetailsActivity}. Works with {@link DetailsPresenter}.
 */

public class DetailsFragment extends Fragment implements DetailsContract.View{

    private ImageView mImageView;
    private CollapsingToolbarLayout mToolbarLayout;
    private Toolbar toolbar;
    private WebView mWebView;
    private NestedScrollView mScrollView;

    private DetailsContract.Presenter mPresenter;

    private int mId;
    private ContentType mType;
    private String mTitle;

    private boolean mIsFavorite = false;

    public static int REQUEST_SHARE = 0;
    public static int REQUEST_COPY_LINK = 1;
    public static int REQUEST_OPEN_WITH_BROWSER = 2;


    public DetailsFragment(){
        // Requires an empty constructor.
    }

    public static DetailsFragment newInstance(){
        return new DetailsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mId = getActivity().getIntent().getIntExtra(DetailsActivity.KEY_ARTICLE_ID, -1);
        mType = (ContentType) getActivity().getIntent().getSerializableExtra(DetailsActivity.KEY_ARTICLE_TYPE);
        mTitle = getActivity().getIntent().getStringExtra(DetailsActivity.KEY_ARTICLE_TITLE);
        mIsFavorite = getActivity().getIntent().getBooleanExtra(DetailsActivity.KEY_ARTICLE_IS_FAVORITE,false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);

        initViews(view);

        setTitle(mTitle);

        toolbar.setOnClickListener(v -> mScrollView.smoothScrollTo(0, 0));

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
        if (mType == ContentType.TYPE_ZHIHU_DAILY) {
            mPresenter.loadZhihuDailyContent(mId);
        } else if (mType == ContentType.TYPE_DOUBAN_MOMENT){
//            mPresenter.loadDoubanContent(mId);
        } else {
//            mPresenter.loadGuokrHandpickContent(mId);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_more, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().onBackPressed();
        } else if (id == R.id.action_more) {

            final BottomSheetDialog dialog = new BottomSheetDialog(getActivity());

            View view = getActivity().getLayoutInflater().inflate(R.layout.actions_details_sheet, null);

            AppCompatTextView favorite = view.findViewById(R.id.text_view_favorite);
            AppCompatTextView copyLink = view.findViewById(R.id.text_view_copy_link);
            AppCompatTextView openWithBrowser = view.findViewById(R.id.text_view_open_with_browser);
            AppCompatTextView share = view.findViewById(R.id.text_view_share);

            if (mIsFavorite) {
                favorite.setText(R.string.unfavorite);
            } else {
                favorite.setText(R.string.favorite);
            }

            // add to bookmarks or delete from bookmarks
            favorite.setOnClickListener(v -> {
                dialog.dismiss();
                mIsFavorite = !mIsFavorite;
                mPresenter.favorite(mType, mId, mIsFavorite);
            });

            // copy the article's link to clipboard
            copyLink.setOnClickListener(v -> {
                mPresenter.getLink(mType, REQUEST_COPY_LINK, mId);
                dialog.dismiss();
            });

            // open the link in system browser
            openWithBrowser.setOnClickListener(v -> {
                mPresenter.getLink(mType, REQUEST_OPEN_WITH_BROWSER, mId);
                dialog.dismiss();
            });

            // getLink the content as text
            share.setOnClickListener(v -> {
                mPresenter.getLink(mType, REQUEST_SHARE, mId);
                dialog.dismiss();
            });

            dialog.setContentView(view);
            dialog.show();
        }
        return true;
    }

    @Override
    public void setPresenter(DetailsContract.Presenter presenter) {
        if(presenter != null){
            mPresenter = presenter;
        }
    }

    @Override
    public void initViews(View view){
        mImageView = view.findViewById(R.id.image_view);
        mToolbarLayout = view.findViewById(R.id.toolbar_layout);
        toolbar = view.findViewById(R.id.toolbar);
        mWebView = view.findViewById(R.id.web_view);
        mScrollView = view.findViewById(R.id.nested_scroll_view);

        DetailsActivity activity = (DetailsActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);

        mWebView.setScrollbarFadingEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(false);
    }

    @Override
    public void showMessage(int stringRes) {
        Toast.makeText(getContext(), stringRes, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean isActive() {
        return isAdded() && isResumed();
    }

    @Override
    public void showZhihuDailyContent(@NonNull ZhihuDailyContent content) {
        if (content.getBody() != null) {
            String result = content.getBody();
            result = result.replace("<div class=\"img-place-holder\">", "");
            result = result.replace("<div class=\"headline\">", "");
            ;
            String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/zhihu_daily.css\" type=\"text/css\">";

            String theme = "<body className=\"\" onload=\"onLoaded()\">";
//            if (mIsNightMode) {
//                theme = "<body className=\"\" onload=\"onLoaded()\" class=\"night\">";
//            }

            result = "<!DOCTYPE html>\n"
                    + "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                    + "<head>\n"
                    + "\t<meta charset=\"utf-8\" />"
                    + css
                    + "\n</head>\n"
                    + theme
                    + result
                    + "</body></html>";

            mWebView.loadDataWithBaseURL("x-data://base", result,"text/html","utf-8",null);
        } else {
            mWebView.loadDataWithBaseURL("x-data://base", content.getShareUrl(),"text/html","utf-8",null);
        }

        setCover(content.getImage());

    }

    @Override
    public void share(@Nullable String link) {
        try {
            Intent shareIntent = new Intent().setAction(Intent.ACTION_SEND).setType("text/plain");
            String shareText = "" + mTitle + " " + link;
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)));
        } catch (android.content.ActivityNotFoundException e) {
            showMessage(R.string.something_wrong);
        }
    }

    @Override
    public void copyLink(@Nullable String link) {
        if(link != null) {
            ClipboardManager manager = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("text", Html.fromHtml(link).toString());
            manager.setPrimaryClip(clipData);
            showMessage(R.string.copied_to_clipboard);
        } else {
            showMessage(R.string.something_wrong);
        }
    }

    @Override
    public void openWithBrowser(@Nullable String link) {
        if(link != null){
            CustomTabHelper.openUrl(getContext(), link);
        } else {
            showMessage(R.string.something_wrong);
        }
    }

    private void setTitle(@NonNull String title){
        setCollapsingToolbarLayoutTitle(title);
    }

    private void setCollapsingToolbarLayoutTitle(String title) {
        mToolbarLayout.setTitle(title);
        mToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        mToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        mToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBarPlus1);
        mToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPlus1);
    }

    private void setCover(@Nullable String url) {
        if (url != null) {
            Glide.with(getContext())
                    .load(url)
                    .asBitmap()
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .error(R.drawable.placeholder)
                    .into(mImageView);
        } else {
            mImageView.setImageResource(R.drawable.placeholder);
        }
    }
}
