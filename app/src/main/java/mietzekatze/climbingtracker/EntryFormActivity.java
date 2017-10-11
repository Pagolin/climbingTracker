package mietzekatze.climbingtracker;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    private Spinner gradeSpinner;


   //declare whether the entered route is just new, climbed as follower, leader or..well...with sack
    private int routeState = 0;
    private Uri routeUri = null;
    private static int ADD_MODE = 0;
    private static int EDIT_MODE = 1;
    private static int MODE = ADD_MODE;
    private static int EDIT_LOADER_ID = 1;
    private boolean hasChanged = false;
    public static String[] MyROUTES_PROJECTION = {  DataBaseContract.MyRoutesEntry._ID,
            DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_NAME,
            DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_SUMMIT,
            DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_AREA,
            DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_STATUS,
            DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_DIFFICULTY};

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            hasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_form);

        // Find all relevant views that we will need to read user input from
        areaEditText = (EditText) findViewById(R.id.edit_area_name);
        areaEditText.setOnTouchListener(touchListener);
        summitEditText = (EditText) findViewById(R.id.edit_summit_name);
        summitEditText.setOnTouchListener(touchListener);
        routeEditText = (EditText) findViewById(R.id.edit_route_name);
        routeEditText.setOnTouchListener(touchListener);
        diffEditText = (EditText) findViewById(R.id.edit_difficulty);
        diffEditText.setOnTouchListener(touchListener);
        stateSpinner = (Spinner) findViewById(R.id.spinner_status);
        stateSpinner.setOnTouchListener(touchListener);
        setupStateSpinner();
        gradeSpinner = (Spinner) findViewById(R.id.spinner_grade);
        gradeSpinner.setOnTouchListener(touchListener);
        setupGradeSpinner();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete_item:
                deleteRoute();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!hasChanged) {
                    NavUtils.navigateUpFromSameTask(EntryFormActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EntryFormActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteRoute() {
        if(routeUri != null) {
            getContentResolver().delete(routeUri, null, null);
            clearFields();
        }
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setupStateSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter stateSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_state_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        stateSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        stateSpinner.setAdapter(stateSpinnerAdapter);

        // Set the integer mSelected to the constant values
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.state_nothing))) {
                        routeState = DataBaseContract.MyRoutesEntry.NOT_DONE;
                    } else if (selection.equals(getString(R.string.state_as_follower))) {
                        routeState = DataBaseContract.MyRoutesEntry.DONE_AS_FOLLOWER;
                    } else if(selection.equals(getString(R.string.state_as_leader))){
                        routeState = DataBaseContract.MyRoutesEntry.DONE_AS_LEADER;
                    } else {
                        routeState = DataBaseContract.MyRoutesEntry.SACK;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                routeState = 0; // Unknown
            }
        });
    }

    //TODO: query the grades table for the selected grade and store the _id as route grade
    private void setupGradeSpinner() {
        ArrayAdapter gradeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_sax_grade_options, android.R.layout.simple_spinner_item);
        gradeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        gradeSpinner.setAdapter(gradeSpinnerAdapter);

        // Set the integer mSelected to the constant values
        gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                Log.i("EntryView", "GradeSpinner clicked at" + selection);
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
        Uri newRouteUri = null;
        int changedRow = -1;
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
        if(routeUri == null) {
            newRouteUri = getContentResolver().insert(DataBaseContract.MyRoutesEntry.MyROUTES_CONTENT_URI, newRouteData);
        } else {
            changedRow = getContentResolver().update(routeUri, newRouteData, null, null);
        }
        /*Afters uccessfull changing or updating*/
        if(newRouteUri!= null || changedRow != -1) {
            clearFields();
            hasChanged = false;
        }
    }

    private void clearFields() {
        routeEditText.setText("");
        summitEditText.setText("");
        areaEditText.setText("");
        diffEditText.setText("");
        stateSpinner.setSelection(DataBaseContract.MyRoutesEntry.NOT_DONE);
    }
}
