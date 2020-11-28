package com.example.myapplication.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.entities.Events;

import java.util.ArrayList;
import java.util.List;

public class DatabaseProcess {

    Context context;
    SQLiteDatabase db;

    public DatabaseProcess(Context context) {
        this.context = context;
        this.db = context.openOrCreateDatabase(Constants.DATABASE_NAME, Context.MODE_PRIVATE, null);
        db.execSQL(
                "create table if not exists event" +
                        "(event_id integer primary key autoincrement, event_name text, " +
                        "kind_id integer, event_date text, event_loop integer" +
                        ", event_image integer);"
        );
        db.execSQL(
                "create table if not exists kind" +
                        "(kind_id integer primary key autoincrement, kind_name text);"
        );
    }

    public void initializeFirstTime() {
        insertKind("anniversary");
        insertKind("education");
        insertKind("job");
        insertKind("life");
        insertKind("trip");
        insertKind("other");
    }

    public void insertKind(String name) {
        db.execSQL("INSERT INTO kind(kind_name) " +
                "VALUES('" + name + "');");
    }

    public void insertEvent(String name, int kind, String date, int loop, int img) {
        db.execSQL("INSERT INTO event(event_name, kind_id, event_date"
                + ", event_loop, event_image) "
                + "VALUES('" + name + "', " + kind + ", '" + date + "', "
                + loop + ", " + img + ")");
    }

    public Events modifyEvent(boolean isCloud, int id, String name, int kind, String date, int loop, int img) {
        db.execSQL("UPDATE event SET event_name ='" + name + "', kind_id=" + kind
                + ", event_date='" + date + "', event_loop=" + loop + ", event_image=" + img
                +" WHERE event_id=" + id);
        if(!isCloud) {
            Cursor res =
                    db.rawQuery("select * from event natural join kind where event_id=" + id, null);
            res.moveToFirst();
            Events event = new Events(
                    res.getInt(res.getColumnIndex(Constants.EVENT_COLUMN_ID))
                    , res.getString(res.getColumnIndex(Constants.EVENT_COLUMN_NAME))
                    , res.getInt(res.getColumnIndex(Constants.KIND_COLUMN_ID))
                    , res.getString(res.getColumnIndex(Constants.EVENT_COLUMN_DATE))
                    , res.getInt(res.getColumnIndex(Constants.EVENT_COLUMN_LOOP))
                    , res.getInt(res.getColumnIndex(Constants.EVENT_COLUMN_IMAGE)));
            res.close();
            return event;
        } else return null;
    }


    public void deleteEvent(int id) {
        db.execSQL("DELETE FROM event WHERE event_id=" + id);
    }


    public Cursor query(String query) {
        Cursor res = db.rawQuery(query, null);
        res.moveToFirst();
        return res;
    }

    public List<Events> getAllEvent(int type) {
        List<Events> events = new ArrayList<>();
        if (type == -1) {
            Cursor res = db.rawQuery("select * from event natural join kind", null);
            res.moveToFirst();
            try {
                while (!res.isAfterLast()) {
                    Events event = new Events(
                            res.getInt(res.getColumnIndex(Constants.EVENT_COLUMN_ID))
                            , res.getString(res.getColumnIndex(Constants.EVENT_COLUMN_NAME))
                            , res.getInt(res.getColumnIndex(Constants.KIND_COLUMN_ID))
                            , res.getString(res.getColumnIndex(Constants.EVENT_COLUMN_DATE))
                            , res.getInt(res.getColumnIndex(Constants.EVENT_COLUMN_LOOP))
                            , res.getInt(res.getColumnIndex(Constants.EVENT_COLUMN_IMAGE)));
                    events.add(event);
                    res.moveToNext();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            res.close();
        } else {
            Cursor res = db.rawQuery("select * from event natural join kind where kind_id=" + type
                    , null);
            res.moveToFirst();
            try {
                while (!res.isAfterLast()) {
                    Events event = new Events(
                            res.getInt(res.getColumnIndex(Constants.EVENT_COLUMN_ID))
                            , res.getString(res.getColumnIndex(Constants.EVENT_COLUMN_NAME))
                            , res.getInt(res.getColumnIndex(Constants.KIND_COLUMN_ID))
                            , res.getString(res.getColumnIndex(Constants.EVENT_COLUMN_DATE))
                            , res.getInt(res.getColumnIndex(Constants.EVENT_COLUMN_LOOP))
                            , res.getInt(res.getColumnIndex(Constants.EVENT_COLUMN_IMAGE)));
                    events.add(event);
                    res.moveToNext();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            res.close();
        }
        return events;
    }


}
