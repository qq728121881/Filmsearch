package com.zzn.filmsearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * author : 郑振楠
 * date   : 2020/3/11
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.RecordViewHolder> {
    private Context context;
    private ItemClickCallBack clickCallBack;
    private List<FilmBean> datas;

    public VideoListAdapter(Context context) {
        this.context = context;
    }

    public void setClickCallBack(ItemClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface ItemClickCallBack {
        void onItemClick(int pos, FilmBean bean);
    }

    public void setDatas(List<FilmBean> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void addDatas(List<FilmBean> datas) {
        this.datas.addAll(datas);
        notifyDataSetChanged();
    }

    public void clearDatas() {
        if (datas != null) {
            datas.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecordViewHolder holder, final int position) {
        if (datas != null) {
            FilmBean dataBean = datas.get(position);
            holder.context.setText(dataBean.getName());

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickCallBack != null) {
                    if (datas != null) {
                        clickCallBack.onItemClick(position, datas.get(position));
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    class RecordViewHolder extends RecyclerView.ViewHolder {
        public TextView context;


        public RecordViewHolder(View view) {
            super(view);
            context = view.findViewById(R.id.context);
        }
    }
}
