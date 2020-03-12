package com.zzn.filmsearch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * author : 郑振楠
 * date   : 2020/3/11
 */
public class VideoPlyaListAdapter extends RecyclerView.Adapter<VideoPlyaListAdapter.RecordViewHolder> {
    private Context context;
    private ItemClickCallBack clickCallBack;
    private List<PlayurlBean> datas;

    public VideoPlyaListAdapter(Context context) {
        this.context = context;
    }

    public void setClickCallBack(ItemClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface ItemClickCallBack {
        void onItemClick(int pos, PlayurlBean bean);
    }

    public void setDatas(List<PlayurlBean> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void addDatas(List<PlayurlBean> datas) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.video_play_but_item, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecordViewHolder holder, final int position) {
        if (datas != null) {
            if (datas.size() == 1) {
                holder.context.setText("播放");

            } else {
                holder.context.setText("第" + (position + 1) + "集");

            }
            if (datas.get(position).isIscheck()) {
                holder.context.setTextColor(Color.RED);
            }

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

        holder.context.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(new Intent(context, PlayvideoActivity.class));
//                intent.putExtra("Url", datas.get(position).getUrl());
//                intent.putExtra("Name", datas.get(position).getName());
//               context.startActivity(intent);

                PlayUrlEvent event = new PlayUrlEvent();
                event.setPosition(position + 1);
                event.setUrl(datas.get(position).getUrl());
                EventBus.getDefault().post(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    class RecordViewHolder extends RecyclerView.ViewHolder {
        public Button context;


        public RecordViewHolder(View view) {
            super(view);
            context = view.findViewById(R.id.but);
        }
    }
}
