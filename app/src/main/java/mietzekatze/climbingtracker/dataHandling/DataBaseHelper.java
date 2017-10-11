package mietzekatze.climbingtracker.dataHandling;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;


/**
 * Created by lisza on 06.10.17.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mietzekatze.climbingtracker.R;
import mietzekatze.climbingtracker.dataHandling.DataBaseContract.*;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SaxonianSwiss.db";
    public static final int DATABASE_VERSION = 1;

    /**
     * Strings that defines the SQL statements to be executed to create the ares, summits and
     * paths tables
    */


    static final String SQL_CREATE_TABLE_GRADES = "CREATE TABLE IF NOT EXISTS " + GradeEntry.TABLE_NAME + "("
            + GradeEntry.GRADE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + GradeEntry.COLUMN_SAXONIAN + " TEXT, "
            + GradeEntry.COLUMN_FRENCH + " TEXT, "
            + GradeEntry.COLUMN_SIERRA + " TEXT, "
            + GradeEntry.COLUMN_UIAA+ " TEXT "+");";

    static final String SQL_CREATE_TABLE_AREAS = "CREATE TABLE IF NOT EXISTS " + AreaEntry.TABLE_NAME + "("
            + AreaEntry.AREA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AreaEntry.COLUMN_AREA_NAME + " TEXT NOT NULL UNIQUE " + ");";

    static final String SQL_CREATE_TABLE_SUMMITS = "CREATE TABLE IF NOT EXISTS " + SummitEntry.TABLE_NAME + "("
            + SummitEntry.SUMMIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SummitEntry.COLUMN_SUMMIT_NUMBER + " INTEGER, "
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
            + ") REFERENCES "+ SummitEntry.TABLE_NAME +"( "+ SummitEntry.COLUMN_SUMMIT_NUMBER+"), "
            + " FOREIGN KEY(" + RoutesEntry.COLUMN_ROUTES_DIFFICULTY
            + ") REFERENCES "+ GradeEntry.TABLE_NAME +"( "+ GradeEntry.GRADE_ID +")" +");";

    static final String SQL_CREATE_TABLE_MyROUTES = "CREATE TABLE IF NOT EXISTS " + MyRoutesEntry.TABLE_NAME + "("
            + MyRoutesEntry.MyROUTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MyRoutesEntry.COLUMN_ROUTE_NAME + " TEXT NOT NULL,"
            + MyRoutesEntry.COLUMN_ROUTE_STATUS + " INTEGER DEFAULT " + MyRoutesEntry.NOT_DONE +","
            + MyRoutesEntry.COLUMN_ROUTE_SUMMIT + " TEXT,"
            + MyRoutesEntry.COLUMN_ROUTE_AREA + " TEXT,"
            + MyRoutesEntry.COLUMN_ROUTE_DIFFICULTY+ " INTEGER" +");";

    //Data map on difficulty grades in different scales from local as html file
    Map<String, List<String>> gradesMap;

    public DataBaseHelper(Context context, Map<String, List<String>> scalesAndGrades){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.gradesMap = scalesAndGrades;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_GRADES);
        List<String> SAX = new ArrayList<>(gradesMap.get("Sächsisch"));
        List<String> FRE = new ArrayList<>(gradesMap.get("Französisch"));
        List<String> SIERRA = new ArrayList<>(gradesMap.get("Sierra"));
        List<String> UIAA = new ArrayList<>(gradesMap.get("UIAA"));
        for(int i=0; i<SAX.size(); i++) {
            ContentValues newRow = new ContentValues();
            newRow.put(GradeEntry.COLUMN_SAXONIAN, SAX.get(i));
            newRow.put(GradeEntry.COLUMN_FRENCH, FRE.get(i));
            newRow.put(GradeEntry.COLUMN_SIERRA, SIERRA.get(i));
            newRow.put(GradeEntry.COLUMN_UIAA, UIAA.get(i));
            db.insert(GradeEntry.TABLE_NAME, null, newRow);
        }
        db.execSQL(SQL_CREATE_TABLE_AREAS);
        db.execSQL(SQL_CREATE_TABLE_SUMMITS);
        db.execSQL(SQL_CREATE_TABLE_ROUTES);
        db.execSQL(SQL_CREATE_TABLE_MyROUTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
