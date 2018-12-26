package mietzekatze.climbingtracker.dataHandling;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mietzekatze.climbingtracker.R;

/**
 * Created by lisza on 06.10.17.
 */

public class DataBaseProvider extends ContentProvider {

    //private DataBaseHelper dbHelper;
    private KletterDBHelper kletterDBHelper;
    private SQLiteDatabase wegeDB;
    private static final int ALLAreas = 100;
    private static final int SINGLEArea = 101;
    private static final int ALLSummits = 200;
    private static final int SINGLESummit = 202;
    private static final int ALLRoutes = 300;
    private static final int SINGLERoute = 303;
    private static final int ALLMyRoutes = 400;
    private static final int SINGLEMyRoute = 404;

    // Creates a UriMatcher object. and define in the static{}-statement all uri's to be recognized
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //uri contains the path to areas_table-> uri response code ALLAreas
        sUriMatcher.addURI(DataBaseContract.CONTENT_AUTHORITY, DataBaseContract.PATH_AREAS, ALLAreas);
        //uri contains the path to areas_table plus any row number -> uri response code SINGLEArea
        sUriMatcher.addURI(DataBaseContract.CONTENT_AUTHORITY, DataBaseContract.PATH_AREAS + "/#", SINGLEArea);
        sUriMatcher.addURI(DataBaseContract.CONTENT_AUTHORITY, DataBaseContract.PATH_SUMMITS, ALLSummits);
        sUriMatcher.addURI(DataBaseContract.CONTENT_AUTHORITY, DataBaseContract.PATH_SUMMITS + "/#", SINGLESummit);
        sUriMatcher.addURI(DataBaseContract.CONTENT_AUTHORITY, DataBaseContract.PATH_ROUTES, ALLRoutes);
        sUriMatcher.addURI(DataBaseContract.CONTENT_AUTHORITY, DataBaseContract.PATH_ROUTES + "/#", SINGLERoute);
        sUriMatcher.addURI(DataBaseContract.CONTENT_AUTHORITY, DataBaseContract.PATH_MyROUTES, ALLMyRoutes);
        sUriMatcher.addURI(DataBaseContract.CONTENT_AUTHORITY, DataBaseContract.PATH_MyROUTES + "/#", SINGLEMyRoute);
    }

    @Override
    public boolean onCreate() {
        //dbHelper = new DataBaseHelper(this.getContext());
        kletterDBHelper = new KletterDBHelper(this.getContext());
        try {
            kletterDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        return true;
    }

    /***********************************************************************************************
     * Handle Queries
     **********************************************************************************************/

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        //SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            wegeDB = kletterDBHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case ALLAreas:
                if (TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";
                cursor = wegeDB.query(DataBaseContract.AreaEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder); break;
            case SINGLEArea:
                selection = "_ID = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = wegeDB.query(DataBaseContract.AreaEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder); break;

            case ALLSummits:
                if (TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";
                cursor = wegeDB.query(DataBaseContract.SummitEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder); break;
            case SINGLESummit:
                selection = "_ID = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = wegeDB.query(DataBaseContract.SummitEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder); break;
            case ALLRoutes:
                if (TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";
                cursor = wegeDB.query(DataBaseContract.RoutesEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder); break;
            case SINGLERoute:
                selection = "_ID = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = wegeDB.query(DataBaseContract.RoutesEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder); break;
            case ALLMyRoutes:
                if (TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";
                cursor = wegeDB.query(DataBaseContract.MyRoutesEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder); break;
            case SINGLEMyRoute:
                selection = "_ID = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = wegeDB.query(DataBaseContract.MyRoutesEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder); break;
            default:
                throw new IllegalArgumentException("Can't parse unknown Uri" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


    /***********************************************************************************************
     * Handle Inserts
     **********************************************************************************************/

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues){
        switch (sUriMatcher.match(uri)) {
            case ALLAreas:
                return null;//return insertArea(uri, contentValues);
            case ALLSummits:
                return null;// return insertSummit(uri, contentValues);
            case ALLRoutes:
                return null;//return insertRoute(uri, contentValues);
            case ALLMyRoutes:
                return insertMyRoute(uri, contentValues);

            default:
                throw new IllegalArgumentException("Inserting is not supported for Uri " + uri);
        }
    }

    @Nullable
    private Uri insertMyRoute(Uri uri, ContentValues contentValues) {
        //SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            wegeDB = kletterDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        try {
            sanityCheck(contentValues);
            long newRowId = wegeDB.insert(uri.getLastPathSegment(),null,contentValues);
            if (newRowId == -1) {
                Toast.makeText(this.getContext(), "Error with saving route", Toast.LENGTH_SHORT).show();
                return null;
            } else {
                Toast.makeText(this.getContext(), R.string.saving_success, Toast.LENGTH_SHORT).show();
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, newRowId);
            }
        }
        catch (IllegalArgumentException e) {
            //TODO: Specify handling different IllegalArgs cases
            Toast.makeText(this.getContext(), e.getMessage() + " No route added", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public Uri insertRoutes(Uri uri) {
        return null;
    }

    private Uri insertSummit(Uri uri, ContentValues contentValues) {
        //TODO: Check content Values, insert Area if not in database, insert Summit
        return null;
    }

    private Uri insertArea(Uri uri, ContentValues contentValues) {
        //TODO: Check contentValues, insert Area
        return null;
    }

    /***********************************************************************************************
     * Handle Delets
     **********************************************************************************************/
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        //SQLiteDatabase database = dbHelper.getWritableDatabase();


        try {
            wegeDB = kletterDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SINGLEMyRoute:
                selection = DataBaseContract.MyRoutesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = wegeDB.delete(DataBaseContract.MyRoutesEntry.TABLE_NAME, selection,
                        selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                Log.i("DBProvider: ", "deleted rows: "+ rowsDeleted);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if(rowsDeleted != 0) {getContext().getContentResolver().notifyChange(uri, null);}
        return rowsDeleted;
    }


    /***********************************************************************************************
     * Handle Updates
     **********************************************************************************************/
    /**So far changes shall only be allowed on the MyRoutes table*/
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        int nrOfupdatedRows;
        switch (sUriMatcher.match(uri)) {
            case SINGLEMyRoute:
                selection = DataBaseContract.MyRoutesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                nrOfupdatedRows = updateMyRoute(uri, contentValues, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return nrOfupdatedRows;
            default:
                throw new IllegalArgumentException("Inserting is not supported for Uri " + uri);
        }
    }

    private int updateMyRoute(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        try {
            wegeDB = kletterDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        //TODO: Extend functionality to use foreign keys for summit and if necessary insert summit
        try {
            sanityCheck(contentValues);
            int nrOfUpdatedRows = wegeDB.update(DataBaseContract.MyRoutesEntry.TABLE_NAME,
                    contentValues, selection, selectionArgs);
            Toast.makeText(this.getContext(), "Changed "+nrOfUpdatedRows+ " rows", Toast.LENGTH_SHORT).show();
            return nrOfUpdatedRows;
        }
        catch (IllegalArgumentException e) {
            //TODO: Specify handling different IllegalArgs cases
            Toast.makeText(this.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return -1;
        }
    }


    private void sanityCheck(ContentValues contentValues) {
        String name = contentValues.getAsString(DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_NAME);
        Float diff = contentValues.getAsFloat(String.valueOf(DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_DIFFICULTY));

        if (name == null || name =="" || name.isEmpty()) {
            throw new IllegalArgumentException("Route requires a name");
        }
        if (diff == null || diff < 0) {
            throw new IllegalArgumentException("Route seems to have no or negative difficulty");
        }
    }



}
