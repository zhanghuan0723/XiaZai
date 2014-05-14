package com.cbh.download;

import android.os.Handler;
import android.os.Message;

import com.cbh.db.controller.DownloadEntryController;
import com.cbh.entity.Constants;
import com.cbh.entity.DownloadEntity;
import com.cbh.entity.DownloadStatus;
import com.cbh.util.TextUtil;

import java.util.LinkedHashMap;

/**
 * Created by Simon on 2014/5/12.
 * 下载一个文件类(该类包含多个子线程)
 */
public class DownloadTask extends Thread {

    private DownloadThread[] downloadThreads;
    private DownloadEntity entity;
    private Handler mDownloadHandler;

    private DownloadStatus status = DownloadStatus.DOWNLOADING;

    public DownloadTask(DownloadEntity entity, Handler handler) {
        this.entity = entity;
        this.mDownloadHandler = handler;
        if (!TextUtil.isValidate(entity.getDownloadedData())) {
            LinkedHashMap<Integer, Integer> data = new LinkedHashMap<Integer, Integer>();
            for (int i = 0; i < Constants.MAX_DOWNLOAD_THREAD_SIZE; i++) {
                data.put(i, 0);
            }
            entity.setDownloadedData(data);
        }
    }

    @Override
    public void run() {
        // 计算总块数(多线程下载)
        int blocks = entity.getFileSize() / Constants.MAX_DOWNLOAD_THREAD_SIZE;
        if (entity.getFileSize() % Constants.MAX_DOWNLOAD_THREAD_SIZE != 0) {
            blocks++;
        }
        downloadThreads = new DownloadThread[Constants.MAX_DOWNLOAD_THREAD_SIZE];

        // 判断所有线程是否执行完成(可能下载完成、可能暂停)
        try {
            while (!checkIfIsFinished()) {
                for (int i = 0; i < downloadThreads.length; i++) {
                    if (downloadThreads[i] == null || (downloadThreads[i].isNetError() && downloadThreads[i].isEnd())) {
                        downloadThreads[i] = new DownloadThread(this, i, blocks, entity);
                        downloadThreads[i].setPriority(Thread.MAX_PRIORITY);
                        downloadThreads[i].start();
                    }
                }
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        switch (status) {
            case CANCEL: // 未加break, 会继续往下执行
                entity.setDownloadedData(null);
                entity.setProgress(0);
            case PAUSE:
            case COMPLETED:
            case INTERRUPT:
                // 更改状态
                entity.setStatus(status);
                // 更新数据库
                DownloadEntryController.addOrUpdate(entity);

                Message msg = new Message();
                msg.what = 1;
                msg.obj = entity;
                mDownloadHandler.sendMessage(msg);
                break;
            default:
                break;
        }
    }

    public synchronized void pause() {
        // 更新状态
        status = DownloadStatus.PAUSE;
        // 循环每一个子线程，暂停
        for (int i = 0; i < downloadThreads.length; i++) {
            if (downloadThreads[i] != null) {
                downloadThreads[i].pause();
            }
        }
    }

    public synchronized void pauseByNet() {
        // 更新状态
        status = DownloadStatus.INTERRUPT;
        // 循环每一个子线程，暂停
        for (int i = 0; i < downloadThreads.length; i++) {
            if (downloadThreads[i] != null) {
                downloadThreads[i].pause();
            }
        }
    }

    public synchronized void cancel() {
        // 更新状态
        status = DownloadStatus.CANCEL;
        // 循环每一个子线程，暂停
        for (int i = 0; i < downloadThreads.length; i++) {
            if (downloadThreads[i] != null) {
                downloadThreads[i].pause();
            }
        }
    }

    // 判断所有线程是否执行完成(可能下载完成、可能暂停)
    private synchronized boolean checkIfIsFinished() {
        boolean isFinished = false;
        for (int i = 0; i < downloadThreads.length; i++) {
            if (downloadThreads[i] != null) {
                isFinished = downloadThreads[i].isEnd();
                if (!isFinished) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return isFinished;
    }

    // 更新下载过程的状态
    public synchronized void update(int index, int readLength, int curPos) {
        LinkedHashMap<Integer, Integer> mDownloadData = entity.getDownloadedData();
        mDownloadData.put(index, curPos);
        entity.setProgress(entity.getProgress() + readLength);
        entity.setDownloadedData(mDownloadData);
        if (entity.getProgress() != entity.getFileSize()) {
            // 实时更新进度
            Message msg = new Message();
            msg.what = 1;
            msg.obj = entity;
            mDownloadHandler.sendMessage(msg);
        } else {
            // 下载完成
            status = DownloadStatus.COMPLETED;
            // 更改状态
            entity.setStatus(status);
            // 更新数据库
            DownloadEntryController.addOrUpdate(entity);

            Message msg = new Message();
            msg.what = 1;
            msg.obj = entity;
            mDownloadHandler.sendMessage(msg);
        }
    }

}
