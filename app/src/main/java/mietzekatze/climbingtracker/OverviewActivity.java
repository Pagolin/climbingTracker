package mietzekatze.climbingtracker;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import mietzekatze.climbingtracker.dataHandling.DataBaseContract;
import mietzekatze.climbingtracker.dataHandling.DataBaseContract.*;
import mietzekatze.climbingtracker.dataHandling.HTMLParser;

public class OverviewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static String[] MyROUTES_PROJECTION = {  MyRoutesEntry._ID,
            MyRoutesEntry.COLUMN_ROUTE_NAME,
            MyRoutesEntry.COLUMN_ROUTE_SUMMIT,
            MyRoutesEntry.COLUMN_ROUTE_AREA,
            MyRoutesEntry.COLUMN_ROUTE_STATUS,
            MyRoutesEntry.COLUMN_ROUTE_DIFFICULTY};

    Cursor myRoutesCursor;
    MyRoutesCursorAdapter myRoutesCursorAdapter;

    //TODO: Use sharedPreferences for scale Preference
    //TODO: Replace ListView by RecyclerView

    private static int CURSOR_LOADER_ID = 0;
    public static String currentScalePreference = DataBaseContract.SCALE_SAX;
    public SharedPreferences sharedPref;
    private ListView routesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {Intent intent = new Intent(OverviewActivity.this, EntryFormActivity.class);
                startActivity(intent);
            }
        });

        routesList = (ListView) findViewById(R.id.recent_routes_list);
        routesList.setEmptyView(findViewById(R.id.empty_view));

        //Instanciate the contact to the underlying db and connect it to the list view
        myRoutesCursor = getContentResolver().query(MyRoutesEntry.MyROUTES_CONTENT_URI,
                MyROUTES_PROJECTION, null, null, null);
        myRoutesCursorAdapter = new MyRoutesCursorAdapter(this, myRoutesCursor);
        routesList.setAdapter(myRoutesCursorAdapter);
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        routesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long itemId) {
                Uri routeUri = ContentUris.withAppendedId(MyRoutesEntry.MyROUTES_CONTENT_URI, itemId);
                Intent editRouteIntent = new Intent(OverviewActivity.this, EntryFormActivity.class);
                editRouteIntent.setData(routeUri);
                startActivity(editRouteIntent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_area, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.set_scale_saxonian) {
            currentScalePreference = DataBaseContract.SCALE_SAX;
            //TODO: Find less brute force solution to rerender listview when settings change
            recreate();
            return true;
        } else if(id == R.id.set_scale_french) {
            currentScalePreference = DataBaseContract.SCALE_FRENCH;
            recreate();
            return true;
        } else if(id == R.id.set_scale_sierra) {
            currentScalePreference = DataBaseContract.SCALE_SIERRA;
            recreate();
            return true;
        } else if(id == R.id.set_scale_uiaa) {
            currentScalePreference = DataBaseContract.SCALE_UIAA;
            recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, MyRoutesEntry.MyROUTES_CONTENT_URI,
                MyROUTES_PROJECTION,null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        myRoutesCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        myRoutesCursorAdapter.swapCursor(null);
    }
}
