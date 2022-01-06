package com.typical_coderr.deliverit_mobile.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Wed
 * Time: 8:04 PM
 */
public class ShipmentScheduleContentProvider extends ContentProvider {

    public ShipmentScheduleContentProvider() {

    }

    // Authority
    private static final String PROVIDER_NAME = "com.typical_coderr.deliverit_mobile.provider";

    // Content URI
    private static final String URL = "content://" + PROVIDER_NAME + "/shipmentSchedule";

    // Constants
    public static final Uri CONTENT_URI = Uri.parse(URL);
    public static final String SHIPMENT_ID ="shipmentId";
    public static final String PICKUP_LOCATION = "pickupLocation";
    public static final String PICKUP_DATE = "pickup";
    public static final String DROP_LOCATION = "dropOffLocation";
    public static final String DROP_DATE = "arrival";
    public static final String RECEIVER_CONTACT = "receiverContactNumber";
    public static final String SENDER_CONTACT = "senderContactNumber";
    public static final String DESCRIPTION = "description";
    public static final String SENDER_FIRSTNAME = "senderFirstName";
    public static final String SENDER_LASTNAME = "senderLastName";
    public static final String RECEIVER_NAME = "receiverName";
    public static final String DRIVER = "driver";
    public static final String PRIORITY = "priority";
    private static final int uriCode = 1;
    private static final UriMatcher uriMatcher;
    private static HashMap<String, String> values;

    static {
        // to match the content URI
        // every time user access table under content provider
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // to access whole table
        uriMatcher.addURI(PROVIDER_NAME, "shipmentSchedule", uriCode);

        // to access a particular row
        // of the table
        uriMatcher.addURI(PROVIDER_NAME, "shipmentSchedule/*", uriCode);
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case uriCode:
                return "vnd.android.cursor.dir/shipmentSchedule";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }



    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        if (db != null) {
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);
        switch (uriMatcher.match(uri)) {
            case uriCode:
                qb.setProjectionMap(values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (sortOrder == null || sortOrder == "") {
            sortOrder = SHIPMENT_ID;
        }
        Cursor c = qb.query(db, projection, selection, selectionArgs, null,
                null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }



    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = db.insert(TABLE_NAME, "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLiteException("Failed to add a record into " + uri);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case uriCode:
                count = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case uriCode:
                count = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /*Handling database*/

    private SQLiteDatabase db;

    // name of the database
    static final String DATABASE_NAME = "DeliveritDB";

    // table name of the database
    static final String TABLE_NAME = "shipmentSchedule";

    // database version
    static final int DATABASE_VERSION = 1;

    // sql query to create the table
    static final String CREATE_DB_TABLE = " CREATE TABLE " + TABLE_NAME
            + "(shipmentId INTEGER PRIMARY KEY,"
            + "pickupLocation  TEXT NOT NULL,"
            + "pickup TEXT NOT NULL,"
            + "dropOffLocation TEXT NOT NULL,"
            + "arrival TEXT NOT NULL,"
            + "receiverContactNumber TEXT NOT NULL,"
            + "senderContactNumber TEXT NOT NULL,"
            + "description TEXT NOT NULL,"
            + "senderFirstName TEXT NOT NULL,"
            + "senderLastName TEXT NOT NULL,"
            + "driver TEXT NOT NULL,"
            + "priority TEXT NOT NULL,"
            + "receiverName TEXT NOT NULL);";


    // creating a database
    private static class DatabaseHelper extends SQLiteOpenHelper {

        // defining a constructor
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // creating a table in the database
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // sql query to drop a table
            // having similar name
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
