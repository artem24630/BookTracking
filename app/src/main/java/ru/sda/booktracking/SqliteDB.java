package ru.sda.booktracking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SqliteDB {
    private DBHelper helper;

    public SqliteDB(Context context){
        helper = new DBHelper(context);
    }

    public void addBook(String name){
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_NAME, name);

        db.insert(DBHelper.TABLE_CONTACTS, null, contentValues);

        db.close();
    }

    public ArrayList<String> getAllBooks(){
        ArrayList<String> books = new ArrayList<>();
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor cursor = db.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

        if(cursor.moveToFirst())
        {
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);

            do{
                books.add(cursor.getString(nameIndex));
            }while(cursor.moveToNext());
        }
        else
            Log.d("DBWork", "DB is empty");

        cursor.close();
        db.close();

        return books;
    }



    private static class DBHelper extends SQLiteOpenHelper{
        static final int DATABASE_VERSION = 1;
        static final String DATABASE_NAME = "bookDB";
        static final String TABLE_CONTACTS = "books";

        static final String KEY_ID = "_id";
        static final String KEY_NAME = "name";
        static final String KEY_COMMENTS = "comments";


        public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
            super(context, name, factory, version, errorHandler);
        }
        DBHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + TABLE_CONTACTS + "(" + KEY_ID + " integer primary key," + KEY_NAME + " text," + KEY_COMMENTS + " text" + ")");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("drop table if exists " + TABLE_CONTACTS);
            onCreate(db);
        }
    }
}



