package com.example.smartender;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHandler extends SQLiteOpenHelper {

    private Context context;

    public DbHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(EventsDao.CREATE_EVENTS_TABLE);
        db.execSQL(TenderDao.CREATE_TABLE);

        //Agregar el resto de tablas
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(EventsDao.DELETE_TABLE);
        db.execSQL(TenderDao.DELETE_TABLE);
        //Agregar el resto de tablas
        onCreate(db);
    }

    public Context getContext() {
        return context;
    }
}