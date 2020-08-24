package com.example.to_do;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.prefs.PreferenceChangeEvent;


public class MainActivity extends AppCompatActivity {
    private DataBase dbHelper;
    private ListView all_tasks;
    private ArrayAdapter<String> my_adapter;
    private EditText fieldText;
    private SharedPreferences prefs;
    private String nameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DataBase(this);
        all_tasks = findViewById(R.id.tasks_list);
        fieldText = findViewById(R.id.ListName);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        nameList = prefs.getString("list_name", "");
        fieldText.setText(nameList);


        changeTextAction();

        loadAllTasks();
    }

    private void changeTextAction() {
        fieldText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                SharedPreferences.Editor editprefs = prefs.edit();
                editprefs.putString("list_name", String.valueOf(fieldText.getText()));
                editprefs.apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void loadAllTasks() {
        ArrayList<String> taskList = dbHelper.getAllTasks();
        if (my_adapter == null) {
            my_adapter = new ArrayAdapter<String>(this, R.layout.row, R.id.txt_task, taskList);
            all_tasks.setAdapter(my_adapter);
        } else {
            my_adapter.clear();
            my_adapter.addAll(taskList);
            my_adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        Drawable icon = menu.getItem(0).getIcon();
        icon.mutate();
        icon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_new_task) {
            final EditText userTaskGet = new EditText(this);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Добавление нового задания")
                    .setMessage("Что бы вы хотели добавить?")
                    .setView(userTaskGet).setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String task = String.valueOf(userTaskGet.getText());
                            dbHelper.insertData(task);
                            loadAllTasks();
                        }
                    })
                    .setNegativeButton("Ничего", null)
                    .create();
            dialog.show();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteTask(View view) {
//        View parent = view.getParent();
        TextView txt_task = findViewById(R.id.txt_task);
        String task = String.valueOf(txt_task.getText());
        dbHelper.deleteData(task);
        loadAllTasks();
    }
}