package com.cbh.download;

import com.cbh.entity.DownloadEntity;

import java.util.LinkedHashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Simon on 2014/5/12.
 * 观察者
 */
public abstract class DataWatcher implements Observer {

    @Override
    public void update(Observable observable, Object data) {
        onDownloadStatusChanged(((DataChanger)observable).getDownloadQueue());
    }

    public abstract void onDownloadStatusChanged(LinkedHashMap<String, DownloadEntity> mDownloadQueue);
}
