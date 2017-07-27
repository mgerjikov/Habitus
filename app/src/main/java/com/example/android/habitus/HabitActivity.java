package com.example.android.habitus;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.habitus.data.HabitusContract;
import com.example.android.habitus.data.HabitusDbHelper;

import static com.example.android.habitus.R.id.fab;

public class HabitActivity extends AppCompatActivity {

    /**
     * Database helper that will provide us access to the database
     */
    private HabitusDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit);

        // Setup Floating Action Bar to open EditorActivity
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HabitActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access the database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the currenct activity
        mDbHelper = new HabitusDbHelper(this);
        displayDatabaseInfo(readData());
    }

    // Here we are Overriding the onStart() method, and that means when the activity starts again
    // (on start .. get it ? ). This means after it comes back after the user has clicked
    // Save in the editor activity that the list will refresh with the new pet in the database.
    // So this will allow the row count to the screen to increase.
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo(readData());
    }

    // REQUIRED
    // This project requires that we have a read method that returns a Cursor
    private Cursor readData(){
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Creating a projection with all the COLUMNs in the database
        String[] projection = {
                HabitusContract.HabitusEntry._ID, // the id of the habit in the database
                HabitusContract.HabitusEntry.COLUMN_HABIT_NAME, // the name of the habit
                HabitusContract.HabitusEntry.COLUMN_HABIT_TYPE, // the type of the habit
                HabitusContract.HabitusEntry.COLUMN_HABIT_RANK, // the rank of the habit
                HabitusContract.HabitusEntry.COLUMN_HABIT_MEASUREMENT // and the measurement
        };

        // Perform this raw SQL query "SELECT * FROM habits"
        // to get a Cursor that contains all rows from the habits table
        Cursor cursor = db.query(
                HabitusContract.HabitusEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }
    // I split this method up into two smaller methods, one read method -> readData() above, that returns a Cursor,
    // and then one display method -> THIS displayDatabaseInfo() method, that uses the Cursor and then closes it
    private void displayDatabaseInfo(Cursor cursor) {

        // In order to actually display the habits we use the TextView in the activity_habits.xml
        // We use findViewById method and find it ( the text view )
        TextView displayView = (TextView) findViewById(R.id.text_view_habit);

        try {
            // Using while loop to iterate through the rows of the cursor and display
            // the information from each column in this order :
            // _id - name - type - rank - measurement

            displayView.setText("The habits table contains: " + cursor.getCount() + " habits \n\n");
            displayView.append(
                    HabitusContract.HabitusEntry._ID + " - " +
                            HabitusContract.HabitusEntry.COLUMN_HABIT_NAME + " - " +
                            HabitusContract.HabitusEntry.COLUMN_HABIT_TYPE + " - " +
                            HabitusContract.HabitusEntry.COLUMN_HABIT_RANK + " - " +
                            HabitusContract.HabitusEntry.COLUMN_HABIT_MEASUREMENT + "\n"
            );

            // Here we figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(HabitusContract.HabitusEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(HabitusContract.HabitusEntry.COLUMN_HABIT_NAME);
            int typeColumnIndex = cursor.getColumnIndex(HabitusContract.HabitusEntry.COLUMN_HABIT_TYPE);
            int rankColumnIndex = cursor.getColumnIndex(HabitusContract.HabitusEntry.COLUMN_HABIT_RANK);
            int measurementColumnIndex = cursor.getColumnIndex(HabitusContract.HabitusEntry.COLUMN_HABIT_MEASUREMENT);

            // And now we iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Using the index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentType = cursor.getString(typeColumnIndex);
                String currentRank = cursor.getString(rankColumnIndex);
                int currentMeasurement = cursor.getInt(measurementColumnIndex);

                // Display the value of each column of the current row in the cursor in the TextView.
                displayView.append(
                        "\n" +                          // So we can start on a new line each time
                                currentID + " - " +     // the id of the current habit
                                currentName + " - " +   // the name of the current habit
                                currentType + " - " +   // the type of the habit (like eating, reading, drinking etc.)
                                currentRank + " - " +   // the rank of the habit (like good, bad or unknown/not sure)
                                currentMeasurement      // and the measurement
                );
            }
        } finally {
            // Always close the cursor when you're done reading from it. This release all its
            // resources and makes it invalid
            cursor.close();
        }
    }

    private void insertHabit() {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        // Create a ContentValues object where column names are the keys,
        // and habit attributes are the values.
        ContentValues values = new ContentValues();
        values.put(HabitusContract.HabitusEntry.COLUMN_HABIT_NAME, "Reading");
        values.put(HabitusContract.HabitusEntry.COLUMN_HABIT_TYPE, "Books");
        values.put(HabitusContract.HabitusEntry.COLUMN_HABIT_RANK, HabitusContract.HabitusEntry.RANK_GOOD);
        values.put(HabitusContract.HabitusEntry.COLUMN_HABIT_MEASUREMENT, 1);

        // Insert the new row, returning the primary key value of the new row
        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the pets table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Toto.
        long newRowId = db.insert(HabitusContract.HabitusEntry.TABLE_NAME, null, values);
    }

    private void deleteDataBase() {
        /*Get the context which is this activity*/
        Context context = HabitActivity.this;
        /*then delete the database*/
        context.deleteDatabase("habits.db");
        mDbHelper = new HabitusDbHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflated options menu from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertHabit();
                displayDatabaseInfo(readData());
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteDataBase();
                displayDatabaseInfo(readData());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
