package com.cbh;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cbh.adapter.DownManagerLVAdapter;
import com.cbh.db.controller.DownloadEntryController;
import com.cbh.download.DataWatcher;
import com.cbh.download.DownloadManager;
import com.cbh.entity.DownloadEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DownloadListActivity extends Activity implements View.OnClickListener {

    private Button pauseAllBtn, resumeAllBtn;

    private ListView downloadList;
    private DownManagerLVAdapter adapter;
    private ArrayList<DownloadEntity> lists;

    private DataWatcher mDownloadWatcher = new DataWatcher() {

        @Override
        public void onDownloadStatusChanged(HashMap<String, DownloadEntity> mDownloadQueue) {
            updateDownloadStatus(mDownloadQueue);
        }
    };

    private void updateDownloadStatus(HashMap<String, DownloadEntity> entities) {
        // 更改列表数据
        for (Map.Entry<String, DownloadEntity> entry : entities.entrySet()) {
            DownloadEntity currEntity = entry.getValue();

            TextView progressTV = (TextView) downloadList.findViewWithTag(entry.getKey());
            if (progressTV == null) return;
            String percent = currEntity.getProgress() * 100L / currEntity.getFileSize() + "%";
            progressTV.setText(percent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_list);

        init();

        initListData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DownloadManager.getInstance(this).addObserver(mDownloadWatcher);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DownloadManager.getInstance(this).removeObserver(mDownloadWatcher);
    }

    private void init() {
        downloadList = (ListView) findViewById(R.id.downloadList);
        pauseAllBtn = (Button) findViewById(R.id.pauseAllBtn);
        pauseAllBtn.setOnClickListener(this);
        resumeAllBtn = (Button) findViewById(R.id.resumeAllBtn);
        resumeAllBtn.setOnClickListener(this);
    }

    private void initListData() {
        // 查找数据库
        lists = (ArrayList<DownloadEntity>) DownloadEntryController.queryAllDownloadEntity();
        adapter = new DownManagerLVAdapter(this, lists);
        downloadList.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pauseAllBtn:
                DownloadManager.getInstance(this).pauseAll();
                break;
            case R.id.resumeAllBtn:
                DownloadManager.getInstance(this).resumeAll();
                break;
        }
    }

}
