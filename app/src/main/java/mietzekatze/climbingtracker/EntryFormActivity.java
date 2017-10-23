package mietzekatze.climbingtracker;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;
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
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mietzekatze.climbingtracker.dataHandling.DataBaseContract;
import mietzekatze.climbingtracker.dataHandling.DatePickerFragment;
import mietzekatze.climbingtracker.dataHandling.HTMLParser;
import static mietzekatze.climbingtracker.dataHandling.DataBaseContract.scalesAndGrades;

/**
 * Created by lisza on 08.10.17.
 */

public class EntryFormActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private AutoCompleteTextView areaEditText;
    private AutoCompleteTextView summitEditText;
    private CursorAdapter summitSuggestionsAdapter;
    private EditText routeEditText;
    private Spinner stateSpinner;
    private Spinner gradeSpinner;
    private FloatingActionButton saveButton;
    private String areaSelection;
    private String summitSelection;
    private String[] currentScale;
    private EditText datePickerText;


   //declare whether the entered route is just new, climbed as follower, leader or..well...with sack
    private int routeState = 0;
    private int routeGrade = 0;
    private Uri routeUri = null;
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
        currentScale = scalesAndGrades.get(OverviewActivity.currentScalePreference);

        areaEditText = (AutoCompleteTextView) findViewById(R.id.edit_area_name);
        areaEditText.setOnTouchListener(touchListener);

        summitEditText = (AutoCompleteTextView) findViewById(R.id.edit_summit_name);
        summitEditText.setOnTouchListener(touchListener);


        routeEditText = (EditText) findViewById(R.id.edit_route_name);
        routeEditText.setOnTouchListener(touchListener);

        stateSpinner = (Spinner) findViewById(R.id.spinner_status);
        stateSpinner.setOnTouchListener(touchListener);
        setupStateSpinner();

        gradeSpinner = (Spinner) findViewById(R.id.spinner_grade);
        gradeSpinner.setOnTouchListener(touchListener);
        setupGradeSpinner();

        datePickerText = (EditText) findViewById(R.id.date_slot);
        datePickerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });

        /*Figure out the starting intent and adapt view an behaviour*/
        Intent addOrEdit = getIntent();
        routeUri = addOrEdit.getData();
        if(routeUri != null){
            Log.i("Edit Mode routeUri: ", routeUri.toString());
            setTitle(R.string.entry_form_edit);
            getLoaderManager().initLoader(EDIT_LOADER_ID, null, this);
        }
        saveButton = (FloatingActionButton) findViewById(R.id.save_fab);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRoute();
            }
        });


        CursorAdapter areaSuggestionsAdapter = queryForSuggestions(DataBaseContract.AreaEntry.AREAS_CONTENT_URI,
                new String[]{DataBaseContract.AreaEntry.AREA_ID, DataBaseContract.AreaEntry.COLUMN_AREA_NAME},
                null, null,null);
        areaEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    String areaText = ((TextView) view).getText().toString();
                    Log.i("Entered Area was: ", areaText);
                    summitSuggestionsAdapter =
                            queryForSuggestions(DataBaseContract.SummitEntry.SUMMITS_CONTENT_URI,
                            new String[]{DataBaseContract.SummitEntry.SUMMIT_ID,
                            DataBaseContract.SummitEntry.COLUMN_SUMMIT_NAME},
                            DataBaseContract.SummitEntry.COLUMN_SUMMIT_AREA +" = ?",
                            new String[]{areaText},null);
                    summitEditText.setAdapter(summitSuggestionsAdapter);
                }
            }
        });
        areaEditText.setAdapter(areaSuggestionsAdapter);


        /*summitEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cur = (Cursor) adapterView.getItemAtPosition(position);
                summitSelection = cur.getString(cur.getColumnIndex(DataBaseContract.SummitEntry.COLUMN_SUMMIT_NAME));
                Log.i("Selected Area was: ", summitSelection);
            }
        });*/
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
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setupStateSpinner() {
        ArrayAdapter stateSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_state_options, android.R.layout.simple_spinner_item);
        stateSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        stateSpinner.setAdapter(stateSpinnerAdapter);
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

    private void setupGradeSpinner() {
        ArrayAdapter gradeSpinnerAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, currentScale);
        gradeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        gradeSpinner.setAdapter(gradeSpinnerAdapter);

        gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                routeGrade = position;
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
            areaSelection  = cursor.getString(indexOfArea);
            int difficulty = cursor.getInt(indexOfDifficulty);
            int status = cursor.getInt(indexOfState);

            routeEditText.setText(route);
            summitEditText.setText(summit);
            areaEditText.setText(areaSelection);
            gradeSpinner.setSelection(difficulty);
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
        newRouteData.put(DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_DIFFICULTY, routeGrade);
        Log.i("saveRoute", " new routeState is " + routeState);
        if(routeUri == null) {
            newRouteUri = getContentResolver().insert(DataBaseContract.MyRoutesEntry.MyROUTES_CONTENT_URI, newRouteData);
        } else {
            changedRow = getContentResolver().update(routeUri, newRouteData, null, null);
        }
        /*Afters successfull changing or updating*/
        if(newRouteUri!= null || changedRow != -1) {
            clearFields();
            hasChanged = false;
        }
    }

    private void clearFields() {
        routeEditText.setText("");
        summitEditText.setText("");
        areaSelection = null;
        areaEditText.setText("");
        stateSpinner.setSelection(DataBaseContract.MyRoutesEntry.NOT_DONE);
        gradeSpinner.setSelection(0);
    }
    //TODO: Add param validation and move to static public context in a Utils class


    /**
     * This function retreives data from specified sql table and column and adds them as autocomplete
     * suggestions to the given AutoCompleteTextView
     *@param columns_Id_and_Suggestions: String[] containing 1. the name of the _id column and
     *                                 2. name of the suggestion column in the
     *                                 given table to retrieve a cursor on the data
     */
    private CursorAdapter queryForSuggestions(final Uri tableUri,
                                                 final String[] columns_Id_and_Suggestions,
                                                 String selection, String[] selectionArgs, String sortOrder) {

        final String suggestionColumnName = columns_Id_and_Suggestions[1];
        final String[] suggestions = new String[]{suggestionColumnName};
        final String preSelection = selection;
        final String [] preSelectionArgs = selectionArgs;

        Cursor suggCursor = getContentResolver().query(tableUri,columns_Id_and_Suggestions,
                selection,selectionArgs, sortOrder);

        SimpleCursorAdapter suggestionsAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_dropdown_item_1line,suggCursor,
                suggestions,new int[]{android.R.id.text1}, 1);

        suggestionsAdapter.setStringConversionColumn(suggCursor.getColumnIndex(suggestionColumnName));
        suggestionsAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence allreadyEnteredChars) {
                String filterString = null;
                String combinedSelection = preSelection;
                String[] combinedSelectionArgs = preSelectionArgs;
                if(allreadyEnteredChars != null) {
                    filterString = "%"+allreadyEnteredChars.toString()+"%";
                    if(preSelection == null) {
                        combinedSelection  = suggestionColumnName + " LIKE ?";
                        combinedSelectionArgs = new String[]{filterString};
                    } else {
                        combinedSelection = preSelection + " AND " + suggestionColumnName + " LIKE ?";
                        combinedSelectionArgs = new String[]{preSelectionArgs[0], filterString};
                    }
                }
                return getContentResolver().query(tableUri,columns_Id_and_Suggestions,
                        combinedSelection, combinedSelectionArgs, null);
            }
        });
        return suggestionsAdapter;
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

}
