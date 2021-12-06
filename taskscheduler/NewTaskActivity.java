package com.example.taskscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.taskscheduler.task_database.FeedHelper;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class NewTaskActivity extends AppCompatActivity {
    private static final String LOG_TAG = NewTaskActivity.class.getSimpleName();
    private FeedHelper taskHelper2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
    }

    @Override
    public void onDestroy() {
        // db.close(); // close connection upon the finish() calls --closing at the functions instead
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }


    // -- tested these methods pair separately -functional
    // string in indicated euro format to Unix epoch ms
    public long string_date_epoch(String s) {
        try {
            // s string input strictly in this format
            Date z = new SimpleDateFormat("dd/MM/yyyy").parse(s);
            Log.d(LOG_TAG, z.toString());
            long epoch = z.getTime(); // returns the epoch time in milliseconds, hopefully
            return epoch;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return Log.d(LOG_TAG, "jesus no");
    }

    /* ! Call requires API Level 26 for java.time.Instant#ofEpochMilli and the others
     * set min to match target at 28
     */
    // Unix epoch ms to LocalDate format (strictly ISO), then to euro format, to match user input
    public String epoch_to_date(long e) {
        LocalDate s;
        // LocalDate object in ISO format (could probably order lexicographically in this)
        s = Instant.ofEpochMilli(e).atZone(ZoneId.systemDefault()).toLocalDate();
        // format to euro form
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String l = s.format(formatter);
        return l;
    }


    // return to MainActivity
    public void backFunc(View view) {
        finish();
    }

    // -- working, sqlitebrowser check
    // save given user input to the DB alongside a generated creation date, & return to main
    public void saveFunc(View view) {
        // get current user input from the fields
        EditText title_ = findViewById(R.id.title_ET);
        EditText descr_ = findViewById(R.id.descr_ET);
        EditText dealine_ = findViewById(R.id.deadline_ET);

        String title = title_.getText().toString();
        String descr = descr_.getText().toString();
        String deadline = dealine_.getText().toString();
        String status = "0"; // completion status: 0 -> uncompleted; String for the query

        // generate current date-time, and turn deadline to ms
        long creation_ms = System.currentTimeMillis(); // (current Unix time in ms)
        long deadline_ms = string_date_epoch(deadline);
        String creation = epoch_to_date(creation_ms).toString();

        taskHelper2 = new FeedHelper(this);
        // set up the 7 db fields
        taskHelper2.insertion(title, descr, deadline_ms, deadline, creation_ms, creation, status);

        finish();
    }

    // ίδια λειτουργεία με την 'Αποθήκευση', απλά αποθηκεύει την νέα εργασία ως ολοκληρωμένη (;)
    // ["η εργασία θα αποθηκεύεται ως ολοκληρωμένη και η εφαρμογή θα επιστρέφει στην αρχική οθόνη"]
    public void doneFunc(View view) {
        // get current user input from the fields
        EditText title_ = findViewById(R.id.title_ET);
        EditText descr_ = findViewById(R.id.descr_ET);
        EditText dealine_ = findViewById(R.id.deadline_ET);

        String title = title_.getText().toString();
        String descr = descr_.getText().toString();
        String deadline = dealine_.getText().toString();
        String status = "1"; // completion status: 1 -> completed; String for the query

        // generate current date-time, and turn deadline to ms
        long creation_ms = System.currentTimeMillis(); // (current Unix time in ms)
        long deadline_ms = string_date_epoch(deadline);
        String creation = epoch_to_date(creation_ms).toString();

        taskHelper2 = new FeedHelper(this);
        taskHelper2.insertion(title, descr, deadline_ms, deadline, creation_ms, creation, status);

        finish();
    }

}