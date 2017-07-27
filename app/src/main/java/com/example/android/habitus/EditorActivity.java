package com.example.android.habitus;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.habitus.data.HabitusContract;
import com.example.android.habitus.data.HabitusDbHelper;

/**
 * Created by Martin on 8.7.2017 г..
 */

public class EditorActivity extends AppCompatActivity {

    /**
     * EditText field to enter the pet's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the pet's breed
     */
    private EditText mTypedEditText;

    /**
     * EditText field to enter the pet's weight
     */
    private EditText mMeasurementEditText;

    /**
     * EditText field to enter the pet's gender
     */
    private Spinner mRankSpinner;

    /**
     * Rank of the habit. The possible values are:
     * 0 for unknown rank, 1 for good, 2 for bad.
     */
    private int mRank = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);


        // Find all relevant views that will need to be read out rom the users input
        mNameEditText = (EditText) findViewById(R.id.edit_habit_name);
        mTypedEditText = (EditText) findViewById(R.id.edit_habit_type);
        mMeasurementEditText = (EditText) findViewById(R.id.edit_habit_measurement);
        mRankSpinner = (Spinner) findViewById(R.id.spinner_rank);

        setupSpinner();

    }

    /**
     * Setup the dropdown spinner that allows the user to select the rank for the habit.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list of options are from the String array.
        // It will use (the spinner will use) the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_rank_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mRankSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mRankSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.rank_good))) {
                        mRank = HabitusContract.HabitusEntry.RANK_GOOD;
                    } else if (selection.equals(getString(R.string.rank_bad))) {
                        mRank = HabitusContract.HabitusEntry.RANK_BAD;
                    } else {
                        mRank = HabitusContract.HabitusEntry.RANK_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mRank = 0; // Unknown
            }
        });
    }

    private void insertHabit() {
        // Read from input fields
        // Use thrim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String typeString = mTypedEditText.getText().toString().trim();
        String measurementString = mMeasurementEditText.getText().toString().trim();
        int measurement = Integer.parseInt(measurementString);

        // Create database helper
        HabitusDbHelper mDbHelper = new HabitusDbHelper(this);

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and habits attributes from the editor are the values
        ContentValues values = new ContentValues();
        values.put(HabitusContract.HabitusEntry.COLUMN_HABIT_NAME, nameString);
        values.put(HabitusContract.HabitusEntry.COLUMN_HABIT_TYPE, typeString);
        values.put(HabitusContract.HabitusEntry.COLUMN_HABIT_RANK, mRank);
        values.put(HabitusContract.HabitusEntry.COLUMN_HABIT_MEASUREMENT, measurement);

        // Insert a new row for habit in the database, returning the ID of that new row.
        long newRowId = db.insert(HabitusContract.HabitusEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving habit", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can diplay a toast with the new row ID.
            Toast.makeText(this, "Habit saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflated menu with options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save habit to database.
                insertHabit();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            case android.R.id.home:
                // Navigate back to parent activity (HabitusActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
