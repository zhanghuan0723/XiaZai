package com.cbh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cbh.db.DBController;
import com.cbh.db.controller.DownloadEntryController;
import com.cbh.download.DataWatcher;
import com.cbh.download.DownloadManager;
import com.cbh.entity.DownloadEntity;
import com.cbh.util.FileUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button downloadBtn, downloadListBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity);

        downloadBtn = (Button) findViewById(R.id.downloadBtn);
        downloadBtn.setOnClickListener(this);
        downloadListBtn = (Button) findViewById(R.id.downloadListBtn);
        downloadListBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.downloadBtn:
                // 开始下载
                DownloadManager.getInstance(this).addAll(getLists());
                break;
            case R.id.downloadListBtn:
                // 跳至下载列表页面
                Intent intent = new Intent(this, DownloadListActivity.class);
                startActivity(intent);
                break;
        }
    }

    private ArrayList<DownloadEntity> getLists() {
        ArrayList<DownloadEntity> entities = new ArrayList<DownloadEntity>();

        DownloadEntity entity1 = new DownloadEntity();
        entity1.setId("12");
        entity1.setName("baidu_16784655");
        entity1.setFileSize(7681211);
        entity1.setUrl("http://gdown.baidu.com/data/wisegame/68c6f438c9e7ba44/baidu_16784655.apk");
        entity1.setPath(FileUtil.getDownloadTmpPath(entity1.getUrl()));
        entity1.setCreateTime(System.currentTimeMillis());
        entities.add(entity1);

        DownloadEntity entity2 = new DownloadEntity();
        entity2.setId("23");
        entity2.setName("ESFileExplorer_203");
        entity2.setFileSize(4289066);
        entity2.setUrl("http://gdown.baidu.com/data/wisegame/67a6175fdd548224/ESFileExplorer_203.apk");
        entity2.setPath(FileUtil.getDownloadTmpPath(entity2.getUrl()));
        entity2.setCreateTime(System.currentTimeMillis());
        entities.add(entity2);

        DownloadEntity entity3 = new DownloadEntity();
        entity3.setId("34");
        entity3.setName("leanquan_4232869");
        entity3.setFileSize(11241102);
        entity3.setUrl("http://gdown.baidu.com/data/wisegame/932b2fb04ab669ea/leanquan_4232869.apk");
        entity3.setPath(FileUtil.getDownloadTmpPath(entity3.getUrl()));
        entity3.setCreateTime(System.currentTimeMillis());
        entities.add(entity3);

        DownloadEntity entity4 = new DownloadEntity();
        entity4.setId("45");
        entity4.setName("aiwan_16");
        entity4.setFileSize(1078245);
        entity4.setUrl("http://gdown.baidu.com/data/wisegame/aed67fb5ba5ba948/aiwan_16.apk");
        entity4.setPath(FileUtil.getDownloadTmpPath(entity4.getUrl()));
        entity4.setCreateTime(System.currentTimeMillis());
        entities.add(entity4);

        DownloadEntity entity5 = new DownloadEntity();
        entity5.setId("56");
        entity5.setName("huohouliulanqi_2669");
        entity5.setFileSize(6173601);
        entity5.setUrl("http://gdown.baidu.com/data/wisegame/756020dedc51d31c/huohouliulanqi_2669.apk");
        entity5.setPath(FileUtil.getDownloadTmpPath(entity5.getUrl()));
        entity5.setCreateTime(System.currentTimeMillis());
        entities.add(entity5);

        DownloadEntity entity6 = new DownloadEntity();
        entity6.setId("67");
        entity6.setName("yijianrootzhuanyeban_1");
        entity6.setFileSize(739853);
        entity6.setUrl("http://gdown.baidu.com/data/wisegame/5661acf5463b2a73/yijianrootzhuanyeban_1.apk");
        entity6.setPath(FileUtil.getDownloadTmpPath(entity6.getUrl()));
        entity6.setCreateTime(System.currentTimeMillis());
        entities.add(entity6);

        DownloadEntity entity7 = new DownloadEntity();
        entity7.setId("78");
        entity7.setName("baiduyijianroot_2400");
        entity7.setFileSize(4032698);
        entity7.setUrl("http://gdown.baidu.com/data/wisegame/f5fe26ba2a220431/baiduyijianroot_2400.apk");
        entity7.setPath(FileUtil.getDownloadTmpPath(entity7.getUrl()));
        entity7.setCreateTime(System.currentTimeMillis());
        entities.add(entity7);

        return entities;
    }
}
