package com.bkav.newMusic;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public class FavoriteSongsProvider extends ContentProvider {
    private static final String DB_SONGS= "db_songs";
    private static final int DB_VESION = 1;
    private static final String TABLE_FAVORITESONGS="favoritesongs";
    static final String ID_FAVORITE="id";
    static final String ID_PROVIDER="id_provider";
    static final String FAVORITE="is_favorite";
    static final String COUNT="count_of_play";
    static final String CONTENT_PATH="listsongs";
    static final String CREATE_TABLE_FAVORITESONGS=
            "create table "+ TABLE_FAVORITESONGS+"("+ID_FAVORITE+ "integer primary key autoincrement,"+
                                                    ID_PROVIDER + " integer ,"+
                                                    FAVORITE + " integer default 0, " + //  0 : not like // 1 : stop like // 2 : like
                                                    COUNT + " integer default 0  );";
    private static HashMap<String, String> HASMAP;
    private SQLiteDatabase database;
    private static final int URI_ALL_ITEM_CODE = 1;
    private static final int URI_ONE_ITEM_CODE = 2;
    private static UriMatcher sUriMatcher;
    private  static  String AUTHORITY="com.music.provider";
    static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CONTENT_PATH);

    private static class FavoriteSongsDatabase extends SQLiteOpenHelper {


        public FavoriteSongsDatabase(@Nullable Context context) {
            super(context, DB_SONGS, null, DB_VESION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE_FAVORITESONGS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITESONGS);
            onCreate(sqLiteDatabase);
        }
    }
    @Override
    public boolean onCreate() {
        FavoriteSongsDatabase mFavoriteSongsDatabase=new FavoriteSongsDatabase(getContext());
        database=mFavoriteSongsDatabase.getWritableDatabase();
        if(database==null)
            return true;
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder=new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_FAVORITESONGS);
        switch (sUriMatcher.match(uri)){
            case URI_ALL_ITEM_CODE:
                queryBuilder.setProjectionMap(HASMAP);
                break;
            case URI_ONE_ITEM_CODE:
                queryBuilder.appendWhere(ID_FAVORITE+"="+uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sUriMatcher);
        }
        if(sortOrder==null||sortOrder==""){
            sortOrder=ID_FAVORITE;
        }
        Cursor cursor=queryBuilder.query(database,projection,selection,selectionArgs,null,null,sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
        //return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)){
            case URI_ALL_ITEM_CODE:
                return "com.music.dir"+CONTENT_PATH;
            case URI_ONE_ITEM_CODE:
                return "com.music.item"+CONTENT_PATH;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long rowID = database.insert(TABLE_FAVORITESONGS, "", values);

        if (rowID > 0) {
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count =0;
        switch (sUriMatcher.match(uri)){
            case URI_ALL_ITEM_CODE:
                count= database.update(TABLE_FAVORITESONGS, values, selection,selectionArgs);
                break;
            case URI_ONE_ITEM_CODE:
                count= database.update(TABLE_FAVORITESONGS,values,
                        ID_FAVORITE +" = "+uri.getPathSegments().get(1)+(!TextUtils.isEmpty(selection)?"AND ("+selection +')':""),selectionArgs);
                break;
            default:
                //throw new IllegalAccessException("Unknow URI "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }
}
