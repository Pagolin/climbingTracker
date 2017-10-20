package mietzekatze.climbingtracker.dataHandling;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;


/**
 * Created by lisza on 06.10.17.
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import mietzekatze.climbingtracker.R;
import mietzekatze.climbingtracker.dataHandling.DataBaseContract.*;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SaxonianSwiss.db";
    public static final int DATABASE_VERSION = 1;
    private AssetManager assetManager;

    /**
     * Strings that defines the SQL statements to be executed to create the ares, summits and
     * paths tables
    */

    static final String SQL_CREATE_TABLE_AREAS = "CREATE TABLE IF NOT EXISTS " + AreaEntry.TABLE_NAME + "("
            + AreaEntry.AREA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AreaEntry.COLUMN_AREA_NAME + " TEXT NOT NULL UNIQUE " + ");";

    static final String SQL_CREATE_TABLE_SUMMITS = "CREATE TABLE IF NOT EXISTS " + SummitEntry.TABLE_NAME + "("
            + SummitEntry.SUMMIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SummitEntry.COLUMN_SUMMIT_NAME + " TEXT NOT NULL,"
            + SummitEntry.COLUMN_SUMMIT_GEOTAG + " TEXT UNIQUE,"
            + SummitEntry.COLUMN_SUMMIT_AREA + " TEXT NOT NULL,"
            + "FOREIGN KEY (" + SummitEntry.COLUMN_SUMMIT_AREA
            + ") REFERENCES "+ AreaEntry.TABLE_NAME +"("+ AreaEntry.COLUMN_AREA_NAME+")" +");";

    static final String SQL_CREATE_TABLE_ROUTES = "CREATE TABLE IF NOT EXISTS " + RoutesEntry.TABLE_NAME + "("
            + RoutesEntry.ROUTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RoutesEntry.COLUMN_ROUTES_NAME + " TEXT NOT NULL,"
            + RoutesEntry.COLUMN_ROUTES_SUMMIT_ID + " INTEGER, "
            + RoutesEntry.COLUMN_ROUTES_DIFFICULTY + " INTEGER, "
            + " FOREIGN KEY(" + RoutesEntry.COLUMN_ROUTES_SUMMIT_ID
            + ") REFERENCES "+ SummitEntry.TABLE_NAME +"( "+ SummitEntry.SUMMIT_ID+")" +");";

    static final String SQL_CREATE_TABLE_MyROUTES = "CREATE TABLE IF NOT EXISTS " + MyRoutesEntry.TABLE_NAME + "("
            + MyRoutesEntry.MyROUTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MyRoutesEntry.COLUMN_ROUTE_NAME + " TEXT NOT NULL,"
            + MyRoutesEntry.COLUMN_ROUTE_STATUS + " INTEGER DEFAULT " + MyRoutesEntry.NOT_DONE +","
            + MyRoutesEntry.COLUMN_ROUTE_SUMMIT + " TEXT,"
            + MyRoutesEntry.COLUMN_ROUTE_AREA + " TEXT,"
            + MyRoutesEntry.COLUMN_ROUTE_DIFFICULTY+ " INTEGER" +");";

    Map<String, List<String>> areas;
    Context context;

    public DataBaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.areas = HTMLParser.parseHTMLTableToMap(context, R.raw.areas);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_AREAS);
        db.execSQL(SQL_CREATE_TABLE_SUMMITS);
        db.execSQL(SQL_CREATE_TABLE_ROUTES);
        db.execSQL(SQL_CREATE_TABLE_MyROUTES);

        //TODO: If task fails db is created anyway...Catch fails and make sure db is filled
        FillDBTask fillDB = new FillDBTask(db,context);
        fillDB.execute();
        Log.i("Databasehelper", "Created Database");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    private class FillDBTask extends AsyncTask<Void, Void, String>{
        SQLiteDatabase db;
        Context context;
        FillDBTask(SQLiteDatabase db, Context context){
            this.db = db;
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            this.db.beginTransaction();
            try{
                ContentValues newRow = new ContentValues();
                // Fill areas table
                for(String area : areas.get("Gebiet")){
                    newRow.put(AreaEntry.COLUMN_AREA_NAME,area);
                    this.db.insert(AreaEntry.TABLE_NAME, null, newRow);
                }
                //Fill summits tables
                Map<String, List<String>> summitsMap = HTMLParser.parseHTMLTableToMap(context, R.raw.summits_all);
                List<String> summits = summitsMap.get("Gipfel");
                newRow = new ContentValues();
                for(int i = 0; i<summits.size(); i++){
                    newRow.put(SummitEntry.COLUMN_SUMMIT_NAME, summitsMap.get("Gipfel").get(i));
                    newRow.put(SummitEntry.COLUMN_SUMMIT_AREA, summitsMap.get("Gebiet").get(i));
                    //newRow.put(SummitEntry.COLUMN_SUMMIT_GEOTAG, null);
                    this.db.insert(SummitEntry.TABLE_NAME, null, newRow);
                }
                db.setTransactionSuccessful();
            } finally {
                this.db.endTransaction();
            }
            return null;
        }

    }
}
