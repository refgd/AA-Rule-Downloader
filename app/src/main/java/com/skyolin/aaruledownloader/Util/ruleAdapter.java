package com.skyolin.aaruledownloader.Util;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.skyolin.aaruledownloader.MainActivity;
import com.skyolin.aaruledownloader.R;
import com.skyolin.aaruledownloader.SQHelper.DatabaseHandler;
import com.skyolin.aaruledownloader.SQHelper.ruleField;

import java.util.List;

public class ruleAdapter extends ArrayAdapter<ruleField> {

    public ruleAdapter(Context context, int textViewResourceId,
                       List<ruleField> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final ruleField info = getItem(position);

        final ViewHolder holder;

        if(null == row) {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.file_download_row, parent, false);

            holder = new ViewHolder();
            holder.textView = (TextView) row.findViewById(R.id.downloadFileName);
            holder.progressBar = (ProgressBar) row.findViewById(R.id.downloadProgressBar);
            holder.button = (Button)row.findViewById(R.id.downloadButton);
            holder.info = info;

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
            holder.info = info;
        }
        String upD = "";
        if(info.hasUpdate()==1){
            upD = "[有更新]";
            holder.textView.setTextColor(Color.parseColor("#009933"));
        }else{
            holder.textView.setTextColor(Color.BLACK);
        }
        holder.textView.setText(info.getName() + upD);

        holder.button.setEnabled(!info.isDownloading());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info.setDownloading(true);
                holder.textView.setText(info.getName() + "[更新中]");
                holder.textView.setTextColor(Color.BLUE);
                holder.button.setEnabled(false);
                holder.button.invalidate();
                DownloadHelper.addTask("http://www.aareadapp.com/test/admin" + info.getUrl(),
                        DownloadHelper.aaHome + info.getIndex() + ".txt",
                        new Handler(){
                            @Override
                            public void handleMessage(Message msg) {
                                int size = msg.getData().getInt("size", 0);
                                if(size>0){
                                    holder.progressBar.setProgress(size);
                                    if(size == holder.progressBar.getMax()){
                                        holder.textView.setText(info.getName());
                                        holder.textView.setTextColor(Color.BLACK);
                                        holder.button.setEnabled(true);
                                        holder.info.setUpdate(0);
                                        MainActivity.db.updateRule(holder.info);
                                    }
                                }else{
                                    int totalSize = msg.getData().getInt("totalSize", 0);
                                    if(totalSize>0)
                                        holder.progressBar.setMax(totalSize);
                                }
                            }
                        });
            }
        });

        return row;
    }

    private static class ViewHolder {
        TextView textView;
        ProgressBar progressBar;
        Button button;
        ruleField info;
    }

}
