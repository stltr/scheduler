package com.example.taskscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.taskscheduler.task_database.FeedContract;
import com.example.taskscheduler.task_database.FeedHelper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static java.lang.Integer.valueOf;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_MESSAGE = "com.example.taskscheduler.extra.MESSAGE";
    private FeedHelper taskHelper; // the helper object to interface with the db
    private ListView taskList;
    SimpleCursorAdapter adapt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskHelper = new FeedHelper(this);
        taskList = findViewById(R.id.task_list);
        taskList.setLongClickable(true);

        taskList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            // View: The view within the AbsListView that was clicked
            // int: The position of the view in the list
            // long: The row id of the item that was clicked
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, TaskDetailsActivity.class);
                // same process as used in the delFunc, passing through the id
                TextView tv = (TextView) view.findViewById(R.id.element_title_id);
                String ident = tv.getText().toString();
                Log.i(LOG_TAG, "the putExtra: " + ident); // check
                intent.putExtra(EXTRA_MESSAGE, ident);
                startActivity(intent);
                return false;
            }
        });

        reload();
    }

    @Override
    public void onResume() {
        super.onResume();
        reload(); // reload the ListView from the db's current state
        Log.d(LOG_TAG, "onResume");
    }


    public void newFunc(View view) {
        Intent intent = new Intent(this, NewTaskActivity.class);
        startActivity(intent);
    }

    // delete checked item(s) from the ListView by id, using the FeedHelper deletion() method
    public void delFunc(View view) {
        View v; TextView tv; CheckBox ck; int id;
        // interate the listview rows
        for (int i = 0; i < taskList.getCount(); i++) {
            v = taskList.getChildAt(i); // returns the view at the specified position in the group
            ck = (CheckBox) v.findViewById(R.id.element_checkbox);
            if (ck.isChecked()) { // works -tracks checks
                Log.i(LOG_TAG, "check 1");

                // pick up the id sub-element and use its value as key for the deletion
                tv = (TextView) v.findViewById(R.id.element_title_id);
                id = valueOf(tv.getText().toString());
                taskHelper.deletion(id);
            }
            else Log.i(LOG_TAG, "No 1");
        }
        reload(); // update the UI list

    }


    // run a cursor through the DB and populate the ListView with the contents upon calling
    // recalled on resume to keep the UI current with the DB
    public void reload() {
        SQLiteDatabase db = taskHelper.getReadableDatabase();

        // results ordered by deadline, which is Unix time ms
        String sortOrder = FeedContract.FeedEntry.COLUMN_NAME_DEADLINE + " ASC";

        Cursor c = db.query(FeedContract.FeedEntry.TABLE,
                // projection: list of which columns to return
                new String[] {
                        "_id", // hidden column from the contract
                        FeedContract.FeedEntry.COLUMN_NAME_TITLE,
                        FeedContract.FeedEntry.COLUMN_NAME_DEADLINE_TEXT,
                        FeedContract.FeedEntry.COLUMN_NAME_DATE_TEXT // get the formatted dates
                },
                FeedContract.FeedEntry.COLUMN_NAME_STATUS + "=?",
                new String[] {"0"}, // fills in '?' above; required type String[]
                null, null, sortOrder);

        /*while (c.moveToNext()) {
            int i = c.getColumnIndex();
            myList.add(c.getString(i));
        }*/

        // list of column names representing the data to bind to the UI
        String[] from = new String[] {
                "_id",
                FeedContract.FeedEntry.COLUMN_NAME_TITLE,
                FeedContract.FeedEntry.COLUMN_NAME_DEADLINE_TEXT,
                FeedContract.FeedEntry.COLUMN_NAME_DATE_TEXT
        };
        // the views that should display column in the "from" parameter; should all be TextViews
        int[] to = {
                R.id.element_title_id,
                R.id.element_title_TV,
                R.id.element_deadline_TV,
                R.id.element_creation_TV
        };

        adapt = new SimpleCursorAdapter(this, R.layout.list_element, c, from, to, 0);
        taskList.setAdapter(adapt);

        /* java.lang.IllegalArgumentException: column '_id' does not exist. Available columns: [title, deadline, date]
         * necessary in projection ?
         */

        /*if (adapter == null) {
            adapter = new ArrayAdapter<>(this, R.layout.list_element, R.id.header_title_TV, myList);
            taskList.setAdapter(adapter);
        } else {
            adapter.clear(); // Remove all elements from the list
            adapter.addAll(myList); // Adds the specified Collection at the end of the array
            adapter.notifyDataSetChanged();
        }*/

        // c.close();
        /* android.database.StaleDataException: Attempting to access a closed CursorWindow.
         * Most probable cause: cursor is deactivated prior to calling this method. --how ??
         */
        db.close();
    }

}