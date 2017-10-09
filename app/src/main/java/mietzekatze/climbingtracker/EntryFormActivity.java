package mietzekatze.climbingtracker;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import mietzekatze.climbingtracker.dataHandling.DataBaseContract;
import mietzekatze.climbingtracker.dataHandling.DataBaseHelper;

/**
 * Created by lisza on 08.10.17.
 */

public class EntryFormActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private EditText areaEditText;
    private EditText summitEditText;
    private EditText routeEditText;
    private EditText diffEditText;
    private Spinner stateSpinner;


   //declare whether the entered route is just new, climbed as follower, leader or..well...with sack
    private int routeState = 0;
    private Uri routeUri = null;
    private static int ADD_MODE = 0;
    private static int EDIT_MODE = 1;
    private static int MODE = ADD_MODE;
    private static int EDIT_LOADER_ID = 1;
    public static String[] MyROUTES_PROJECTION = {  DataBaseContract.MyRoutesEntry._ID,
            DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_NAME,
            DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_SUMMIT,
            DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_AREA,
            DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_STATUS,
            DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_DIFFICULTY};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_form);

        // Find all relevant views that we will need to read user input from
        areaEditText = (EditText) findViewById(R.id.edit_area_name);
        //areaEditText.setOnTouchListener(touchListener);
        summitEditText = (EditText) findViewById(R.id.edit_summit_name);
        routeEditText = (EditText) findViewById(R.id.edit_route_name);
        diffEditText = (EditText) findViewById(R.id.edit_difficulty);
        stateSpinner = (Spinner) findViewById(R.id.spinner_status);
        setupSpinner();

        /*Figure out the starting intent and adapt view an behaviour*/
        Intent addOrEdit = getIntent();
        routeUri = addOrEdit.getData();
        if(routeUri != null){
            Log.i("Edit Mode routeUri: ", routeUri.toString());
            setTitle("Edit Route");
            MODE = EDIT_MODE;
            getLoaderManager().initLoader(EDIT_LOADER_ID, null, this);
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.save_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRoute();
            }
        });


    }

    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_state_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        stateSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.state_nothing))) {
                        routeState = DataBaseContract.RoutesEntry.NOT_DONE;
                    } else if (selection.equals(getString(R.string.state_as_follower))) {
                        routeState = DataBaseContract.RoutesEntry.DONE_AS_FOLLOWER;
                    } else if(selection.equals(getString(R.string.state_as_leader))){
                        routeState = DataBaseContract.RoutesEntry.DONE_AS_LEADER;
                    } else {
                        routeState = DataBaseContract.RoutesEntry.SACK;
                    }
                Log.i("stateSpinner", " Set routeState to: " + routeState);
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                routeState = 0; // Unknown
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, routeUri,
                MyROUTES_PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i("onLoadFinished", "loaded"+ routeUri.toString());
        if(cursor.moveToFirst()) {
            int indexOfRoute = cursor.getColumnIndexOrThrow(DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_NAME);
            int indexOfSummit = cursor.getColumnIndexOrThrow(DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_SUMMIT);
            int indexOfArea = cursor.getColumnIndexOrThrow(DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_AREA);
            int indexOfDifficulty = cursor.getColumnIndexOrThrow(DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_DIFFICULTY);
            int indexOfState = cursor.getColumnIndexOrThrow(DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_STATUS);

            String route  = cursor.getString(indexOfRoute);
            String summit  = cursor.getString(indexOfSummit);
            String area  = cursor.getString(indexOfArea);
            int difficulty = cursor.getInt(indexOfDifficulty);
            int status = cursor.getInt(indexOfState);

            routeEditText.setText(route);
            summitEditText.setText(summit);
            areaEditText.setText(area);
            diffEditText.setText(String.valueOf(difficulty));
            stateSpinner.setSelection(status);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        clearFields();
    }

    private void saveRoute() {
        ContentValues newRouteData = new ContentValues();
        newRouteData.put(DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_NAME, routeEditText.getText().toString().trim());
        newRouteData.put(DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_SUMMIT, summitEditText.getText().toString().trim());
        newRouteData.put(DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_AREA, areaEditText.getText().toString().trim());
        newRouteData.put(DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_STATUS, routeState);
        Log.i("saveRoute", " new routeState is " + routeState);
        try {
            newRouteData.put(DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_DIFFICULTY, Integer.parseInt(diffEditText.getText().toString()));
        }
        catch (NumberFormatException e) {
            newRouteData.put(DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_DIFFICULTY, 0);
        }
        if(MODE == ADD_MODE) {
            Uri newRouteUri = getContentResolver().insert(DataBaseContract.MyRoutesEntry.MyROUTES_CONTENT_URI, newRouteData);
        } else {
            int changedRow = getContentResolver().update(routeUri, newRouteData, null, null);
        }
        clearFields();
    }

    private void clearFields() {
        routeEditText.setText("");
        summitEditText.setText("");
        areaEditText.setText("");
        diffEditText.setText("");
        stateSpinner.setSelection(DataBaseContract.MyRoutesEntry.NOT_DONE);
    }
}
