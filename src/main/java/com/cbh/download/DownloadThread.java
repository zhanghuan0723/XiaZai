package com.cbh.download;

import com.cbh.entity.DownloadEntity;

import org.apache.http.HttpStatus;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 * Created by Simon on 2014/5/12.
 * 下载一个文件中某一段类(多线程下载)
 */
public class DownloadThread extends Thread {
    private static final int TIMEOUT_CONNECT = 10000;
    private static final int TIMEOUT_READ = 10000;
    private static final int IO_BUFFER_SIZE = 4 * 1024;

    private DownloadTask downloadTask;
    private DownloadEntity entity;
    private int index;
    private int blocks;
    private int startPosition;
    private int endPosition;
    private int downloadedLength;

    // 是否下载结束
    private boolean isEnd = false;
    private boolean isPaused;
    private boolean isNetError;

    public DownloadThread(DownloadTask downloadTask, int index, int blocks, DownloadEntity entity) {
        this.downloadTask = downloadTask;
        this.index = index;
        this.blocks = blocks;
        this.entity = entity;

        this.downloadedLength = entity.getDownloadedData().get(index);
        this.startPosition = index * blocks + downloadedLength;
        this.endPosition = (index + 1) * blocks - 1;
    }

    @Override
    public void run() {
        // download each block
        try {
            URL url = new URL(entity.getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_CONNECT);
            connection.setReadTimeout(TIMEOUT_READ);
            connection.setRequestProperty(
                    "Accept",
                    "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
            connection.setRequestProperty("Accept-Language", "zh-CN");
            connection.setRequestProperty("Referer", url.toString());
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Range", "bytes=" + startPosition + "-" + endPosition); // 设置获取实体数据的范围
            connection.setRequestProperty("User-Agent",
                    "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
            connection.setRequestProperty("Connection", "Keep-Alive");
            int resCode = connection.getResponseCode();
            int fileSize = connection.getContentLength();
            InputStream in = null;
            switch (resCode) {
                case HttpStatus.SC_OK:
                case HttpStatus.SC_PARTIAL_CONTENT:
                    String encoding = connection.getContentEncoding();
                    if (encoding != null && "gzip".equalsIgnoreCase(encoding)) {
                        in = new GZIPInputStream(connection.getInputStream());
                    } else if (encoding != null && "deflate".equalsIgnoreCase(encoding)) {
                        in = new InflaterInputStream(connection.getInputStream());
                    } else {
                        in = connection.getInputStream();
                    }

                    RandomAccessFile file = new RandomAccessFile(entity.getPath(), "rwd");
                    file.setLength(entity.getFileSize());
                    file.seek(startPosition);
                    byte[] b = new byte[IO_BUFFER_SIZE];
                    int buff;
                    while ((buff = in.read(b)) != -1) {
                        downloadedLength += buff;
                        file.write(b, 0, buff);
                        downloadTask.update(index, buff, downloadedLength);
                        if (isPaused) {
                            break;
                        }
                    }
                    isEnd = true;
                    file.close();
                    in.close();
                    break;
            }
        } catch (Exception e) {
            isNetError = true;
            e.printStackTrace();
        }
    }

    public synchronized void pause() {
        isPaused = true;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public boolean isNetError() {
        return isNetError;
    }
}
