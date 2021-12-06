package com.example.taskscheduler.task_database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.taskscheduler.MainActivity;

public class FeedHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = FeedHelper.class.getSimpleName();

    public FeedHelper(Context context) {
        super(context, FeedContract.DB_NAME, null, FeedContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // the DB scheme - change primary key originally from DATE to _ID for the CursorAdapter (?)
        // status as text instead of integer for the main query selectionArgs (possibly not needed with valueOf?)
        String createTable = "CREATE TABLE " + FeedContract.FeedEntry.TABLE + " (" +
                FeedContract.FeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FeedContract.FeedEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL, " +
                FeedContract.FeedEntry.COLUMN_NAME_DESCR + " TEXT NOT NULL, " +
                FeedContract.FeedEntry.COLUMN_NAME_DEADLINE + " INTEGER NOT NULL, " +
                FeedContract.FeedEntry.COLUMN_NAME_DEADLINE_TEXT + " TEXT NOT NULL, " +
                FeedContract.FeedEntry.COLUMN_NAME_DATE + " INTEGER NOT NULL, " +
                FeedContract.FeedEntry.COLUMN_NAME_DATE_TEXT + " TEXT NOT NULL, " +
                FeedContract.FeedEntry.COLUMN_NAME_STATUS + " TEXT NOT NULL);";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FeedContract.FeedEntry.TABLE);
        onCreate(db);
    }


    // for the NewTaskActivity insertions, recording a new task
    // fdate arguments passed pre-formatted to the counterpart columns, for the cursor's output
    public void insertion(String title, String descr, long deadline_epoch, String deadline_fdate,
                          long creation_epoch, String creation_fdate, String status) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues val = new ContentValues();
            val.put(FeedContract.FeedEntry.COLUMN_NAME_TITLE, title);
            val.put(FeedContract.FeedEntry.COLUMN_NAME_DESCR, descr);
            // string deadline date input turned to Unix epoch ms for the db beforehand
            val.put(FeedContract.FeedEntry.COLUMN_NAME_DEADLINE, deadline_epoch);
            val.put(FeedContract.FeedEntry.COLUMN_NAME_DEADLINE_TEXT, deadline_fdate);
            val.put(FeedContract.FeedEntry.COLUMN_NAME_DATE, creation_epoch);
            val.put(FeedContract.FeedEntry.COLUMN_NAME_DATE_TEXT, creation_fdate);
            val.put(FeedContract.FeedEntry.COLUMN_NAME_STATUS, status);

            db.insertOrThrow(FeedContract.FeedEntry.TABLE, null, val);
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.d(LOG_TAG, "Insertion error");
        }
        finally {
            db.endTransaction();
            db.close(); // necessasary ?
        }
    }

    // for the MainActivity delete button function; delete row by the unique id as key
    public void deletion(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(FeedContract.FeedEntry.TABLE,  FeedContract.FeedEntry._ID + "=?",
                    new String[] { String.valueOf(id) }); // valueOf int; matches ??
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(LOG_TAG, "Deletion error");
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // for the TaskDetailsActivity value(s) updating; update row (title, descr, dl +status) by id
    public void updating(int id, String title, String descr, long deadline_epoch,
                         String deadline_fdate, String status) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues val = new ContentValues();
            val.put(FeedContract.FeedEntry.COLUMN_NAME_TITLE, title);
            val.put(FeedContract.FeedEntry.COLUMN_NAME_DESCR, descr);
            // string deadline date input turned to Unix epoch ms for the db beforehand
            val.put(FeedContract.FeedEntry.COLUMN_NAME_DEADLINE, deadline_epoch);
            val.put(FeedContract.FeedEntry.COLUMN_NAME_DEADLINE_TEXT, deadline_fdate);
            val.put(FeedContract.FeedEntry.COLUMN_NAME_STATUS, status);

            db.update(FeedContract.FeedEntry.TABLE, val,
                    FeedContract.FeedEntry._ID + " =?", new String[] { String.valueOf(id) });
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(LOG_TAG, "Updating error");
        } finally {
            db.endTransaction();
            db.close();
        }
    }


    // queries to pull specified data to TaskDetailsActivity, by id key

    public String getTitle(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String column_value = "";
        Cursor c = db.rawQuery("SELECT title FROM tasks WHERE _id = ?", new String[] { String.valueOf(id) });
        while (c.moveToNext()) { //Loop through all the records
            //Now on the variable 'c' there is one record
            int column_name = c.getColumnIndex("title"); //Get the index of the column from the table
            column_value = c.getString(column_name); // fatal; searching non-existant column (-1)
        }
        return column_value;
    }

    public String getDescr(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String column_value = "";
        Cursor c = db.rawQuery("SELECT description FROM tasks WHERE _id = ?", new String[] { String.valueOf(id) });
        while (c.moveToNext()) { //Loop through all the records
            //Now on the variable 'c' there is one record
            int column_name = c.getColumnIndex("description"); //Get the index of the column from the table
            column_value = c.getString(column_name);
        }
        return column_value;
    }

    public String getDLText(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String column_value = "";
        Cursor c = db.rawQuery("SELECT deadline_text FROM tasks WHERE _id = ?", new String[] { String.valueOf(id) });
        while (c.moveToNext()) { //Loop through all the records
            //Now on the variable 'c' there is one record
            int column_name = c.getColumnIndex("deadline_text"); //Get the index of the column from the table
            column_value = c.getString(column_name);
        }
        return column_value;
    }

}