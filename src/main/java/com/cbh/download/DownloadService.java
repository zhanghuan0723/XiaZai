package com.cbh.download;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.cbh.db.controller.DownloadEntryController;
import com.cbh.entity.Constants;
import com.cbh.entity.DownloadEntity;
import com.cbh.entity.DownloadStatus;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Simon on 2014/5/12.
 */
public class DownloadService extends Service {

    private DataChanger mDownloadChanger;
    private ConnectionBroadcast connectionReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Handler mDownloadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.KEY_HANDLER_DATA_CHANGED:
                    DownloadEntity entity = (DownloadEntity) msg.obj;
                    mDownloadChanger.notifyDataChanged(entity);
                    checkStatus(entity);
                    break;
            }
        }
    };

    private void checkStatus(DownloadEntity entity) {
        switch (entity.getStatus()) {
            // 如果当前下载完成或者暂停或者取消，则下载下一个
            case CANCEL:
                delEntity(entity);
            case COMPLETED:
            case PAUSE:
                mDownloadChanger.getDownloadTasks().remove(entity.getId());
                LinkedHashMap<String, DownloadEntity> mDownloadQueue = mDownloadChanger.getDownloadQueue();
                for (Map.Entry<String, DownloadEntity> entry : mDownloadQueue.entrySet()) {
                    if (entry.getValue().getStatus() == DownloadStatus.WAITING) {
                        addDownload(entry.getValue());
                    }
                }
                break;
            case DOWNLOADING:
                // send notification
                break;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 注册网络监听广播接收器
        // connectionReceiver = new ConnectionBroadcast();
        // IntentFilter intentFilter = new IntentFilter();
        // intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        // registerReceiver(connectionReceiver, intentFilter);

        // init DataChanger
        mDownloadChanger = DataChanger.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // get action、DownloadEntity
        if (intent != null && intent.getExtras() != null) {
            DownloadEntity entity = (DownloadEntity) intent.getSerializableExtra(Constants.KEY_DOWNLOAD_ENTITY);
            DownloadStatus status = (DownloadStatus) intent.getSerializableExtra(Constants.KEY_DOWNLOAD_ACTION);
            switch (status) {
                case ADD:
                    addDownload(entity);
                    break;
                case PAUSE:
                    pauseDownload(entity);
                    break;
                case PAUSEALL:
                    pauseAll();
                    break;
                case RESUME:
                    resumeDownload(entity);
                    break;
                case RESUMEALL:
                    resumeAll();
                    break;
                case CANCEL:
                    cancelDownload(entity);
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectionReceiver);
    }

    private synchronized void addDownload(DownloadEntity entity) {
        // 加入等待下载列表
        if (!mDownloadChanger.getDownloadQueue().containsKey(entity.getId())) {
            mDownloadChanger.getDownloadQueue().put(entity.getId(), entity);
        } else {
            // 若添加重复下载项
            // 1. 当前待下载列表中已存在
            // 2. 当前待下载项状态为NOTHING
            if (entity.getStatus() == DownloadStatus.NOTHING) {
                return;
            }
        }

        // 判断当前下载任务数是否超过设置的最大下载数
        if (mDownloadChanger.getDownloadTasks().size() >= Constants.MAX_DOWNLOAD_FILE_SIZE) {
            setStatus(entity, DownloadStatus.WAITING);
        } else {
            startDownload(entity);
        }
    }

    private synchronized void startDownload(DownloadEntity entity) {
        setStatus(entity, DownloadStatus.DOWNLOADING);

        // 开启下载线程，handler用于更新主线程
        DownloadTask task = new DownloadTask(entity, mDownloadHandler);
        mDownloadChanger.getDownloadTasks().put(entity.getId(), task);
        task.start();
    }

    private void pauseDownload(DownloadEntity entity) {
        HashMap<String, DownloadTask> tasks = mDownloadChanger.getDownloadTasks();
        if (tasks.containsKey(entity.getId())) {
            tasks.get(entity.getId()).pause();
        }
    }

    private void resumeDownload(DownloadEntity entity) {
        if (mDownloadChanger.getDownloadQueue().containsKey(entity.getId())) {
            // 若下载完成
            DownloadEntity currEntity = mDownloadChanger.getDownloadQueue().get(entity.getId());
            if (currEntity.getFileSize() == currEntity.getProgress()) {
                return;
            }
            addDownload(mDownloadChanger.getDownloadQueue().get(entity.getId()));
        }
    }

    private void cancelDownload(DownloadEntity entity) {
        HashMap<String, DownloadTask> tasks = mDownloadChanger.getDownloadTasks();
        if (tasks.containsKey(entity.getId())) {
            tasks.get(entity.getId()).cancel();
        } else {
            delEntity(entity);
        }
    }

    // 暂停全部(暂停未完成的)
    private void pauseAll() {
        // 修改除完成状态外为暂停状态
        LinkedHashMap<String, DownloadEntity> queue = mDownloadChanger.getDownloadQueue();
        for (Map.Entry<String, DownloadEntity> entry : queue.entrySet()) {
            if (entry.getValue().getStatus() != DownloadStatus.COMPLETED) {
                entry.getValue().setStatus(DownloadStatus.PAUSE);
            }
        }

        // 改变正在下载的状态
        HashMap<String, DownloadTask> tasks = mDownloadChanger.getDownloadTasks();
        for (Map.Entry<String, DownloadTask> entry : tasks.entrySet()) {
            entry.getValue().pause();
        }
    }

    // 继续全部(继续未完成的)
    private void resumeAll() {
        LinkedHashMap<String, DownloadEntity> queue = mDownloadChanger.getDownloadQueue();
        for (Map.Entry<String, DownloadEntity> entry : queue.entrySet()) {
            if (entry.getValue().getStatus() != DownloadStatus.COMPLETED) {
                addDownload(entry.getValue());
            }
        }
    }

    // 通知观察者更改(UI)状态
    private void setStatus(DownloadEntity entity, DownloadStatus status) {
        entity.setStatus(status);
        DownloadEntryController.addOrUpdate(entity);
        mDownloadChanger.notifyDataChanged(entity);
    }

    private void delEntity(DownloadEntity entity) {
        // 删除本地文件
        File file = new File(entity.getPath());
        if (file.exists()) {
            file.delete();
        }

        // 删除数据库记录
        DownloadEntryController.delete(entity.getId());

        // 重新构建queue
        LinkedHashMap<String, DownloadEntity> newQueue = new LinkedHashMap<String, DownloadEntity>();
        LinkedHashMap<String, DownloadEntity> queue = mDownloadChanger.getDownloadQueue();
        for (Map.Entry<String, DownloadEntity> entry : queue.entrySet()) {
            if (!entity.getId().equals(entry.getValue().getId())) {
                newQueue.put(entry.getKey(), entry.getValue());
            }
        }
        mDownloadChanger.setDownloadQueue(newQueue);
    }

    class ConnectionBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            // 仅wifi
            if (wifiNetInfo != null && wifiNetInfo.isConnected()) {
                resumeAll();
            } else {
                pauseAll();
            }
        }
    }

}
