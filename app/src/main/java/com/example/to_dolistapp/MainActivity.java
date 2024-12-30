package com.example.to_dolistapp;
// import library
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Data> taskData;
    private List<Data> originalTaskData;
    private TaskDBHelper dbHelper;
    private FloatingActionButton fabAddTask;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private Spinner spinnerSelectByCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database helper
        dbHelper = new TaskDBHelper(this);

        // Initialize data lists
        taskData = new ArrayList<>();
        originalTaskData = new ArrayList<>();

        // Initialize UI components
        fabAddTask = findViewById(R.id.fab_add_task);
        recyclerView = findViewById(R.id.recyclerview);
        spinnerSelectByCategory = findViewById(R.id.spinnerselectbycategory);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(this, taskData);
        recyclerView.setAdapter(adapter);

        // Load tasks from SQLite
        loadTasksFromSQLite(taskData);
        originalTaskData.addAll(taskData);

        // Setup Category Spinner
        setupCategorySpinner();

        // Setup Floating Action Button
        setupFabAddTask();

        // Setup RecyclerView Item Click Listeners
        setupRecyclerViewListeners();
    }

    private void setupCategorySpinner() {
        // Get category names from database
        String[] categories = dbHelper.getCategoryNames();

        // Create adapter for spinner
        List<String> spinnerCategories = new ArrayList<>();
        spinnerCategories.add("All Categories");
        for (String category : categories) {
            spinnerCategories.add(category);
        }

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                spinnerCategories
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSelectByCategory.setAdapter(categoryAdapter);

        // Set spinner listener
        spinnerSelectByCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                filterTasksByCategory(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Show all tasks if nothing is selected
                adapter.updateData(originalTaskData);
            }
        });
    }

    private void setupFabAddTask() {
        fabAddTask.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerViewListeners() {
        adapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                Intent intent = new Intent(MainActivity.this, editTask.class);
                intent.putExtra("task", taskData.get(position).getName());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(int position) {
                deleteTask(position);
                taskData.remove(position);
                Toast.makeText(MainActivity.this, "Task Deleted", Toast.LENGTH_SHORT).show();
                adapter.notifyItemRemoved(position);

            }

            @Override
            public void onCheckboxClick(int position) {
                markTaskAsComplete(position);
                taskData.remove(position);
                Toast.makeText(MainActivity.this, "Task Completed", Toast.LENGTH_SHORT).show();
                adapter.notifyItemRemoved(position);
            }
        });
    }

    private void deleteTask(int position) {
        String task = taskData.get(position).getName();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(TaskContract.TaskEntry.TABLE_NAME,
                TaskContract.TaskEntry.COLUMN_TASK + " = ?",
                new String[]{task});

        taskData.remove(position);
        originalTaskData.remove(position);
        adapter.notifyItemRemoved(position);
    }

    private void filterTasksByCategory(String category) {
        List<Data> filteredTasks = new ArrayList<>();

        if (category.equals("All Categories")) {
            // Show all tasks
            filteredTasks.addAll(originalTaskData);
        } else {
            // Filter tasks by category
            for (Data task : originalTaskData) {
                if (task.getCategory().equalsIgnoreCase(category)) {
                    filteredTasks.add(task);
                }
            }
        }

        // Update adapter with filtered data
        taskData.clear();
        taskData.addAll(filteredTasks);
        adapter.notifyDataSetChanged();
    }

    public void loadTasksFromSQLite(List<Data> data) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " +
                        "tasks.task, " +
                        "tasks.due_date, " +
                        "tasks.due_time, " +
                        "categories.name AS category, " +
                        "priorities.name AS priority, " +
                        "tasks.notes " +
                        "FROM tasks " +
                        "LEFT JOIN categories " +
                        "ON tasks.category_id = categories.id " +
                        "LEFT JOIN priorities " +
                        "ON tasks.priority_id = priorities.id",
                null
        );

        while (cursor.moveToNext()) {
            @SuppressLint("Range") String taskName = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK));
            @SuppressLint("Range") String taskDate = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DUE_DATE));
            @SuppressLint("Range") String taskTime = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DUE_TIME));
            @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category"));
            @SuppressLint("Range") String priority = cursor.getString(cursor.getColumnIndex("priority"));
            @SuppressLint("Range") String notes = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NOTES));

            data.add(new Data(taskName, taskDate, taskTime, category, priority, notes));
        }

        cursor.close();
        db.close();
    }

    public void markTaskAsComplete(int position) {
        String task = taskData.get(position).getName();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(TaskContract.TaskEntry.TABLE_NAME,
                TaskContract.TaskEntry.COLUMN_TASK + " = ?",
                new String[]{task});

        taskData.remove(position);
        originalTaskData.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    // Inner class for task data
    public class Data {
        String name;
        String date;
        String time;
        String category;
        String priority;
        String notes;

        Data(String name, String date, String time, String category, String priority, String notes) {
            this.name = name;
            this.date = date;
            this.time = time;
            this.category = category;
            this.priority = priority;
            this.notes = notes;
        }

        public String getName() {
            return name;
        }

        public String getDate() {
            return date;
        }

        public String getTime() {
            return time;
        }

        public String getCategory() {
            return category;
        }

        public String getPriority() {
            return priority;
        }

        public String getNotes() {
            return notes;
        }
    }
}
