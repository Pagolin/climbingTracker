package mietzekatze.climbingtracker;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import mietzekatze.climbingtracker.dataHandling.DataBaseContract;

public class OverviewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static String[] MyROUTES_PROJECTION = {  DataBaseContract.MyRoutesEntry._ID,
            DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_NAME,
            DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_SUMMIT,
            DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_AREA,
            DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_STATUS,
            DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_DIFFICULTY};

    MyRoutesCursorAdapter myRoutesCursorAdapter;

    private static int CURSOR_LOADER_ID = 0;


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

        ListView routesList = (ListView) findViewById(R.id.recent_routes_list);
        routesList.setEmptyView(findViewById(R.id.empty_view));

        //Instanciate the contact to the underlying db and connect it to the list view
        Cursor standardCursor = getContentResolver().query(DataBaseContract.MyRoutesEntry.MyROUTES_CONTENT_URI,
                MyROUTES_PROJECTION, null, null, null);
        myRoutesCursorAdapter = new MyRoutesCursorAdapter(this, standardCursor);
        routesList.setAdapter(myRoutesCursorAdapter);
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        routesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long itemId) {
                Uri petUri = ContentUris.withAppendedId(DataBaseContract.MyRoutesEntry.MyROUTES_CONTENT_URI, itemId);
                Intent editPetIntent = new Intent(OverviewActivity.this, EntryFormActivity.class);
                editPetIntent.setData(petUri);
                startActivity(editPetIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_area, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, DataBaseContract.MyRoutesEntry.MyROUTES_CONTENT_URI,
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
