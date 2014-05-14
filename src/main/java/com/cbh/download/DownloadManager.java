package com.cbh.download;

import android.content.Context;
import android.content.Intent;

import com.cbh.db.controller.DownloadEntryController;
import com.cbh.entity.Constants;
import com.cbh.entity.DownloadEntity;
import com.cbh.entity.DownloadStatus;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Simon on 2014/5/12.
 */
public class DownloadManager {

    private Context context;
    private long prevExecuteTime;
    private static final long MIN_EXECUTION_INTERVEL = 500;

    private static DownloadManager instance;

    private DownloadManager(Context context) {
        this.context = context.getApplicationContext();
        // 启动下载服务
        context.startService(new Intent(context, DownloadService.class));
    }

    public static DownloadManager getInstance(Context context) {
        if (instance == null) {
            instance = new DownloadManager(context);
        }
        return instance;
    }

    // 开始
    public void add(DownloadEntity entity) {
        if (checkIfIsExecutable()) {
            context.startService(getIntent(entity, DownloadStatus.ADD));
        }
    }

    // 全部开始
    public void addAll(ArrayList<DownloadEntity> entities) {
        for (DownloadEntity entity : entities) {
            context.startService(getIntent(entity, DownloadStatus.ADD));
        }
    }

    // 暂停
    public void pause(DownloadEntity entity) {
        if (checkIfIsExecutable()) {
            context.startService(getIntent(entity, DownloadStatus.PAUSE));
        }
    }

    // 全部暂停
    public void pauseAll() {
        if (checkIfIsExecutable()) {
            context.startService(getIntent(null, DownloadStatus.PAUSEALL));
        }
    }

    // 继续
    public void resume(DownloadEntity entity) {
        if (checkIfIsExecutable()) {
            context.startService(getIntent(entity, DownloadStatus.RESUME));
        }
    }

    // 全部继续
    public void resumeAll() {
        if (checkIfIsExecutable()) {
            context.startService(getIntent(null, DownloadStatus.RESUMEALL));
        }
    }

    // 取消
    public void cancel(DownloadEntity entity) {
        if (checkIfIsExecutable()) {
            context.startService(getIntent(entity, DownloadStatus.CANCEL));
        }
    }

    // 添加观察者
    public void addObserver(DataWatcher observer) {
        DataChanger.getInstance().addObserver(observer);
    }

    // 移除观察者
    public void removeObserver(DataWatcher observer) {
        DataChanger.getInstance().deleteObserver(observer);
    }

    // 移除所有观察者
    public void removeObservers() {
        DataChanger.getInstance().deleteObservers();
    }

    private boolean checkIfIsExecutable() {
        long currExecuteTime = System.currentTimeMillis();
        if (currExecuteTime - prevExecuteTime < MIN_EXECUTION_INTERVEL) {
            return false;
        } else {
            prevExecuteTime = currExecuteTime;
            return true;
        }
    }

    private Intent getIntent(DownloadEntity entity, DownloadStatus status) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(Constants.KEY_DOWNLOAD_ENTITY, entity);
        intent.putExtra(Constants.KEY_DOWNLOAD_ACTION, status);
        return intent;
    }
}
