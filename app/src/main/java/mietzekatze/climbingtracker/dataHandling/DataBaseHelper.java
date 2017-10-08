package mietzekatze.climbingtracker.dataHandling;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lisza on 06.10.17.
 */
import mietzekatze.climbingtracker.dataHandling.DataBaseContract;
import mietzekatze.climbingtracker.dataHandling.DataBaseContract.*;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SaxonianSwiss.db";
    public static final int DATABASE_VERSION = 1;

    /**
     * Strings that defines the SQL statements to be executed to create the ares, summits and
     * paths tables
    */
    static final String SQL_CREATE_TABLE_AREAS = "CREATE TABLE " + AreaEntry.TABLE_NAME + "("
            + AreaEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AreaEntry.COLUMN_AREA_NAME + " TEXT NOT NULL UNIQUE " + ");";

    static final String SQL_CREATE_TABLE_SUMMITS = "CREATE TABLE " + SummitEntry.TABLE_NAME + "("
            + SummitEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SummitEntry.COLUMN_SUMMIT_NUMBER + " INTEGER UNIQUE AUTOINCREMENT, "
            + SummitEntry.COLUMN_SUMMIT_NAME + " TEXT NOT NULL,"
            + SummitEntry.COLUMN_SUMMIT_GEOTAG + " TEXT UNIQUE,"
            + SummitEntry.COLUMN_SUMMIT_AREA + " TEXT NOT NULL,"
            + "FOREIGN KEY (" + SummitEntry.COLUMN_SUMMIT_AREA
            + ") REFERENCES "+ AreaEntry.TABLE_NAME +"("+ AreaEntry.COLUMN_AREA_NAME+")" +");";

    static final String SQL_CREATE_TABLE_ROUTES = "CREATE TABLE " + RoutesEntry.TABLE_NAME + "("
            + RoutesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RoutesEntry.COLUMN_ROUTES_NAME + " TEXT NOT NULL,"
            + RoutesEntry.COLUMN_ROUTES_STATUS + " INTEGER DEFAULT " + RoutesEntry.NOT_DONE +","
            + RoutesEntry.COLUMN_ROUTES_SUMMIT_ID + " INTEGER,"
            + " FOREIGN KEY(" + RoutesEntry.COLUMN_ROUTES_SUMMIT_ID
            + ") REFERENCES "+ SummitEntry.TABLE_NAME +"( "+ SummitEntry.COLUMN_SUMMIT_NUMBER+")" +");";

    static final String SQL_CREATE_TABLE_MyROUTES = "CREATE TABLE " + MyRoutesEntry.TABLE_NAME + "("
            + MyRoutesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MyRoutesEntry.COLUMN_ROUTE_NAME + " TEXT NOT NULL,"
            + MyRoutesEntry.COLUMN_ROUTE_STATUS + " INTEGER DEFAULT " + MyRoutesEntry.NOT_DONE +","
            + MyRoutesEntry.COLUMN_ROUTE_SUMMIT + " TEXT,"
            + MyRoutesEntry.COLUMN_ROUTE_AREA + " TEXT,"
            + MyRoutesEntry.COLUMN_ROUTE_DIFFICULTY+ " INTEGER" +");";


    public DataBaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_AREAS);
        db.execSQL(SQL_CREATE_TABLE_SUMMITS);
        db.execSQL(SQL_CREATE_TABLE_ROUTES);
        db.execSQL(SQL_CREATE_TABLE_MyROUTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
