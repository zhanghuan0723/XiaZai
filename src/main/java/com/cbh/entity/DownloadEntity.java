package com.cbh.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Simon on 2014/5/12.
 */
@DatabaseTable(tableName = "DownloadEntry")
public class DownloadEntity implements Serializable {

    public static final String CREATE_TIME = "createTime";

    @DatabaseField(id = true)
    private String id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String url;
    @DatabaseField
    private String path;
    @DatabaseField
    private int fileSize;
    @DatabaseField
    private int progress;

    @DatabaseField
    private DownloadStatus status = DownloadStatus.NOTHING;  // 下载状态
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<Integer, Integer> downloadedData;        // 对应线程下载的数据，用于多线程

    @DatabaseField
    private long createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public DownloadStatus getStatus() {
        return status;
    }

    public void setStatus(DownloadStatus status) {
        this.status = status;
    }

    public HashMap<Integer, Integer> getDownloadedData() {
        return downloadedData;
    }

    public void setDownloadedData(HashMap<Integer, Integer> downloadedData) {
        this.downloadedData = downloadedData;
    }

    public static String getCreateTime() {
        return CREATE_TIME;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
