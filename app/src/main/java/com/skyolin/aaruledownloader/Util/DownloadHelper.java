package com.skyolin.aaruledownloader.Util;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class DownloadHelper {
    public  static final String aaHome = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath()).append("/aaRead/rule/").toString();
    private static final String TAG = DownloadHelper.class.getSimpleName();

    private static List<downloadTask> tasks = new ArrayList<>();
    private static int downloadCount = 0;

    private static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            downloadCount--;
            doNext();
        }
    };

    public static void addTask(String downloadUrl, String fileptah, Handler handler){
        tasks.add(new downloadTask(downloadUrl, fileptah, handler, mHandler));
        doNext();
    }

    private static void doNext(){
        if(tasks.size() > 0 && downloadCount < 1){
            tasks.get(0).start();
            tasks.remove(0);
            downloadCount++;
        }
    }

    static class downloadTask extends Thread {
        private Handler mfinishHandler;
        private Handler mHandler;
        private String downloadUrl;
        private String filePath;

        public downloadTask(String downloadUrl, String fileptah, Handler handler, Handler handler2) {
            this.downloadUrl = downloadUrl;
            this.filePath = fileptah;
            this.mHandler = handler;
            this.mfinishHandler = handler2;
        }

        @Override
        public void run() {

            try {
                URL url = new URL(downloadUrl);
                URLConnection conn = url.openConnection();
                // 读取下载文件总大小
                int fileSize = conn.getContentLength();
                if (fileSize <= 0) {
                    Log.e("error", "get filesize error");
                    return;
                }

                if(mHandler != null){
                    Message msg = new Message();
                    msg.getData().putInt("totalSize", fileSize);
                    mHandler.sendMessage(msg);
                }

                File file = new File(filePath);
                byte[] buffer = new byte[1024];
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                FileOutputStream raf = new FileOutputStream(file, false);

                int len;
                int downloadedAllSize = 0;
                while ((len = bis.read(buffer, 0, 1024)) != -1) {
                    raf.write(buffer, 0, len);
                    downloadedAllSize += len;
                    if(mHandler != null){
                        Message msg = new Message();
                        msg.getData().putInt("size", downloadedAllSize);
                        mHandler.sendMessage(msg);
                    }
                }
                raf.close();
                bis.close();

                Message msg = new Message();
                msg.getData().putString("done", downloadUrl);
                mfinishHandler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
