package com.example.l_assistant.News.timeline;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.l_assistant.R;
import com.example.l_assistant.News.data.ZhihuDailyNewsQuestion;
import com.example.l_assistant.News.interfaze.OnRecyclerViewItemOnClickListener;

import java.util.List;

/**
 * Created by zkd on 2017/10/15.
 */

public class ZhihuDailyNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    @NonNull
    private final Context mContext;

    @NonNull
    private List<ZhihuDailyNewsQuestion> mList;
    private OnRecyclerViewItemOnClickListener mListener;

    public ZhihuDailyNewsAdapter(@NonNull List<ZhihuDailyNewsQuestion> list,
                                 @NonNull Context context){
        this.mList = list;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_universal_layout, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ZhihuDailyNewsQuestion item = mList.get(position);

        ItemViewHolder holder1 = (ItemViewHolder) holder;

        if(item.getImages().get(0) == null){
            holder1.itemImg.setImageResource(R.drawable.placeholder);
        }else{
            Glide.with(mContext)
                    .load(item.getImages().get(0))
                    .asBitmap()
                    .placeholder(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .error(R.drawable.placeholder)
                    .into(holder1.itemImg);
        }
        holder1.title.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return mList.isEmpty() ? 0 : mList.size();
    }

    public void setItemClickListener(OnRecyclerViewItemOnClickListener listener){
        this.mListener = listener;
    }

    public void updataData(@NonNull List<ZhihuDailyNewsQuestion> list){
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
        notifyItemRemoved(list.size());
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{
        private ImageView itemImg;
        private TextView title;
        private OnRecyclerViewItemOnClickListener listener;

        public ItemViewHolder(View itemView, OnRecyclerViewItemOnClickListener listener){
            super(itemView);
            itemImg = itemView.findViewById(R.id.image_view_cover);
            title = itemView.findViewById(R.id.text_view_title);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(listener != null){
                listener.OnItemClick(view, getLayoutPosition());
            }
        }
    }
}
