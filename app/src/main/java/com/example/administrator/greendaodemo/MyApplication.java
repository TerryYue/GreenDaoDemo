package com.example.administrator.greendaodemo;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import me.itangqi.greendao.DaoMaster;
import me.itangqi.greendao.DaoSession;


/**
 * Created by Administrator on 2016-08-05.
 */

public class MyApplication extends Application{

    public DaoSession daoSession;
    public SQLiteDatabase db;
    public DaoMaster.DevOpenHelper helper;
    public DaoMaster daoMaster;


    @Override
    public void onCreate() {
        super.onCreate();
        setupDatabase();
    }

    private void setupDatabase() {
        //创建数据库
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        helper = new DaoMaster.DevOpenHelper(this, "test", null);
        //得到数据库连接对象
        db = helper.getWritableDatabase();
        //得到数据库管理者
        daoMaster =new DaoMaster(db);
        //得到daoSession，可以执行增删改查操作
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

}
