package com.cbh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.cbh.R;
import com.cbh.download.DownloadManager;
import com.cbh.entity.DownloadEntity;

import java.util.ArrayList;

/**
 * Created by Simon on 2014/4/18.
 */
public class DownManagerLVAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<DownloadEntity> aList;

    public DownManagerLVAdapter(Context c, ArrayList<DownloadEntity> list) {
        this.mContext = c;
        this.aList = list;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return aList.size();
    }

    @Override
    public Object getItem(int position) {
        return aList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);

            holder = new Holder();
            holder.titleTV = (TextView) convertView.findViewById(R.id.titleTV);
            holder.progressTV = (TextView) convertView.findViewById(R.id.progressTV);
            //holder.pauseBtn = (Button) convertView.findViewById(R.id.pauseBtn);
            //holder.resumeBtn = (Button) convertView.findViewById(R.id.resumeBtn);
            holder.delBtn = (Button) convertView.findViewById(R.id.delBtn);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.titleTV.setText(aList.get(position).getName());
        holder.progressTV.setText((aList.get(position).getProgress() * 100L / aList.get(position).getFileSize() + "%"));

        /*
        holder.pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager.getInstance(mContext).pause(aList.get(position));
            }
        });
        holder.resumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager.getInstance(mContext).resume(aList.get(position));
            }
        });
        */
        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager.getInstance(mContext).cancel(aList.get(position));
            }
        });

        // 设置tag
        holder.progressTV.setTag(aList.get(position).getId());
        return convertView;
    }

    public static class Holder {
        public TextView titleTV;
        public TextView progressTV;
        //public Button pauseBtn;
        //public Button resumeBtn;
        public Button delBtn;
    }
}
