package com.example.taskscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.taskscheduler.task_database.FeedHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskDetailsActivity extends AppCompatActivity {
    private static final String LOG_TAG = TaskDetailsActivity.class.getSimpleName();
    private FeedHelper taskHelper3;
    private int the_id; // trying to get a global here for the intent extra..

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        taskHelper3 = new FeedHelper(this);

        Intent intent = getIntent();
        String id_ = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Log.i(LOG_TAG, "the getExtra: " + id_); // check
        int id = Integer.valueOf(id_);

        the_id = id;

        EditText title_ = findViewById(R.id.title_ET2);
        EditText descr_ = findViewById(R.id.descr_ET2);
        EditText deadline_ = findViewById(R.id.deadline_ET2);

        // set the ETs with the values mapping to the intent's key
        title_.setText(taskHelper3.getTitle(id));
        descr_.setText(taskHelper3.getDescr(id));
        deadline_.setText(taskHelper3.getDLText(id));
    }

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


    public void backFunc2(View view)  {
        Log.i(LOG_TAG, "leaving TaskDetails..");
        finish();
    }

    public void saveFunc2(View view) {
        // get the current edittext values
        EditText title_ = findViewById(R.id.title_ET2);
        EditText descr_ = findViewById(R.id.descr_ET2);
        EditText deadline_ = findViewById(R.id.deadline_ET2);

        String title = title_.getText().toString();
        String descr = descr_.getText().toString();
        String deadline = deadline_.getText().toString();
        long deadline_ms = string_date_epoch(deadline);
        String status = "0";

        taskHelper3.updating(the_id, title, descr, deadline_ms, deadline, status);

        finish();
    }

    public void doneFunc2(View view) {
        // get the current edittext values
        EditText title_ = findViewById(R.id.title_ET2);
        EditText descr_ = findViewById(R.id.descr_ET2);
        EditText deadline_ = findViewById(R.id.deadline_ET2);

        String title = title_.getText().toString();
        String descr = descr_.getText().toString();
        String deadline = deadline_.getText().toString();
        long deadline_ms = string_date_epoch(deadline);
        String status = "1";

        taskHelper3.updating(the_id, title, descr, deadline_ms, deadline, status);

        finish();
    }

}