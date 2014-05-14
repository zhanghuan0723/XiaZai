package com.cbh.db.controller;

import com.cbh.db.DBController;
import com.cbh.db.DBNotInitializeException;
import com.cbh.entity.DownloadEntity;
import com.cbh.util.TextUtil;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DownloadEntryController {

    private static Dao<DownloadEntity, String> getDao() throws SQLException, DBNotInitializeException {
        return DBController.getDB().getDao(DownloadEntity.class);
    }

    public static boolean addOrUpdate(DownloadEntity dto) {
        try {
            getDao().createOrUpdate(dto);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DBNotInitializeException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void addOrUpdate(ArrayList<DownloadEntity> callback) {
        if (!TextUtil.isValidate(callback)) {
            return;
        }
        try {
            for (DownloadEntity homework : callback) {
                getDao().createOrUpdate(homework);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DBNotInitializeException e) {
            e.printStackTrace();
        }
    }

    public static DownloadEntity queryById(String id) {
        try {
            return getDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DBNotInitializeException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean delete(String id) {
        try {
            getDao().deleteById(id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DBNotInitializeException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static LinkedHashMap<String, DownloadEntity> queryAllUnCompletedRecord() {
        LinkedHashMap<String, DownloadEntity> queue = null;
        List<DownloadEntity> resultList = null;
        try {
            QueryBuilder<DownloadEntity, String> queryBuilder = getDao().queryBuilder();
            queryBuilder.orderBy(DownloadEntity.CREATE_TIME, false);
            resultList = getDao().query(queryBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DBNotInitializeException e) {
            e.printStackTrace();
        }
        if (TextUtil.isValidate(resultList)) {
            queue = new LinkedHashMap<String, DownloadEntity>();
            for (DownloadEntity entry : resultList) {
                queue.put(entry.getId(), entry);
            }
        }
        return queue;
    }

    public static List<DownloadEntity> queryAllDownloadEntity() {
        List<DownloadEntity> resultList = null;
        try {
            QueryBuilder<DownloadEntity, String> queryBuilder = getDao().queryBuilder();
            queryBuilder.orderBy(DownloadEntity.CREATE_TIME, false);
            resultList = getDao().query(queryBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DBNotInitializeException e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
