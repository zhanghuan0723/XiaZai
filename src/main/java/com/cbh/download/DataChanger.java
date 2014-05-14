package com.cbh.download;

import com.cbh.entity.DownloadEntity;
import com.cbh.util.TextUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Observable;

/**
 * Created by Simon on 2014/5/12.
 * 被观察者
 */
public class DataChanger extends Observable {

    // 正在下载tasks
    private HashMap<String, DownloadTask> mDownloadingTasks;

    // 等待下载的entities
    private LinkedHashMap<String, DownloadEntity> mDownloadingEntities;

    private static DataChanger instance;

    private DataChanger() {
        mDownloadingTasks = new HashMap<String, DownloadTask>();
        mDownloadingEntities = new LinkedHashMap<String, DownloadEntity>();
    }

    public static DataChanger getInstance() {
        if (instance == null) {
            instance = new DataChanger();
        }
        return instance;
    }

    public void notifyDataChanged(DownloadEntity entity) {
        setChanged();
        notifyObservers();
    }

    public void setDownloadQueue(LinkedHashMap<String, DownloadEntity> queue) {
        if (TextUtil.isValidate(queue)) {
            mDownloadingEntities = queue;
        }
    }

    public LinkedHashMap<String, DownloadEntity> getDownloadQueue() {
        return mDownloadingEntities;
    }

    public HashMap<String, DownloadTask> getDownloadTasks() {
        return mDownloadingTasks;
    }

}
