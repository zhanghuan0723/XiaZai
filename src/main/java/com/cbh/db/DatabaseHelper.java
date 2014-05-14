package com.cbh.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cbh.entity.DownloadEntity;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.android.DatabaseTableConfigUtil;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Database helper class used to manage the creation and upgrading of your
 * database. This class also usually provides the DAOs used by the other
 * classes.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    protected AndroidConnectionSource connectionSource = new AndroidConnectionSource(this);

    public DatabaseHelper(Context context, String dbName, CursorFactory factory, int version) {
        super(context, dbName, factory, version);
    }

    public <D extends Dao<T, ?>, T> D getDao(Class<T> clazz) throws SQLException {
        // lookup the dao, possibly invoking the cached database config
        Dao<T, ?> dao = DaoManager.lookupDao(connectionSource, clazz);
        if (dao == null) {
            // try to use our new reflection magic
            DatabaseTableConfig<T> tableConfig = DatabaseTableConfigUtil.fromClass(connectionSource, clazz);
            if (tableConfig == null) {
                /**
                 * TODO: we have to do this to get to see if they are using the
                 * deprecated annotations like {@link DatabaseFieldSimple}.
                 */
                dao = (Dao<T, ?>) DaoManager.createDao(connectionSource, clazz);
            } else {
                dao = (Dao<T, ?>) DaoManager.createDao(connectionSource, tableConfig);
            }
        }
        @SuppressWarnings("unchecked")
        D castDao = (D) dao;
        return castDao;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DatabaseConnection conn = connectionSource.getSpecialConnection();
        boolean clearSpecial = false;
        if (conn == null) {
            conn = new AndroidDatabaseConnection(db, true);
            try {
                connectionSource.saveSpecialConnection(conn);
                clearSpecial = true;
            } catch (SQLException e) {
                throw new IllegalStateException("Could not save special connection", e);
            }
        }
        try {
            onCreate();
        } catch (DBNotInitializeException e) {
            Log.e(DatabaseHelper.class.getName(), "DBNotInitializeException", e);
        } finally {
            if (clearSpecial) {
                connectionSource.clearSpecialConnection(conn);
            }
        }
    }

    private void onCreate() throws DBNotInitializeException {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, DownloadEntity.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new DBNotInitializeException("Can't create database");
        }
    }

    @Override
    public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DatabaseConnection conn = connectionSource.getSpecialConnection();
        boolean clearSpecial = false;
        if (conn == null) {
            conn = new AndroidDatabaseConnection(db, true);
            try {
                connectionSource.saveSpecialConnection(conn);
                clearSpecial = true;
            } catch (SQLException e) {
                throw new IllegalStateException("Could not save special connection", e);
            }
        }
        try {
            onUpgrade(oldVersion, newVersion);
        } catch (DBNotInitializeException e) {
            Log.e(DatabaseHelper.class.getName(), "DBNotInitializeException", e);
        } finally {
            if (clearSpecial) {
                connectionSource.clearSpecialConnection(conn);
            }
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher
     * version number. This allows you to adjust the various data to match the
     * new version number.
     *
     * @throws DBNotInitializeException
     */
    private void onUpgrade(int oldVersion, int newVersion) throws DBNotInitializeException {
        Log.i(DatabaseHelper.class.getName(), "onUpgrade");
        clearAllData();
    }

    public void clearAllData() {
        try {
            TableUtils.dropTable(connectionSource, DownloadEntity.class, true);
            onCreate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
    }
}