package com.example.to_dolistapp;

import android.provider.BaseColumns;

public class TaskContract {
    private TaskContract() {}

    // Table for Category
    public static final class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
    }
    // Table for Priority
    public static final class PriorityEntry implements BaseColumns {
        public static final String TABLE_NAME = "priorities";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
    }

    // Table for Task
    public static final class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_TASK = "task";
        public static final String COLUMN_CATEGORY_ID = "category_id";
        public static final String COLUMN_PRIORITY_ID = "priority_id";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_DUE_DATE = "due_date";
        public static final String COLUMN_DUE_TIME = "due_time";
        public static final String COLUMN_COMPLETED = "completed";
    }
}
