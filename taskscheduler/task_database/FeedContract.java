package com.example.taskscheduler.task_database;

import android.provider.BaseColumns;

public class FeedContract {
    public static final String DB_NAME = "FeedDB";
    public static final int DB_VERSION = 1;

    public class FeedEntry implements BaseColumns {
        public static final String TABLE = "tasks";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCR = "description";
        public static final String COLUMN_NAME_DEADLINE = "deadline";
        public static final String COLUMN_NAME_DEADLINE_TEXT = "deadline_text"; // formatted for output
        public static final String COLUMN_NAME_DATE = "date"; // the creation date
        public static final String COLUMN_NAME_DATE_TEXT = "date_text";
        public static final String COLUMN_NAME_STATUS = "status"; // binary indicator of completion
    }
    // _id hidden

    // doubled the date columns to both types to bypass the formatted date cursor output problem
}