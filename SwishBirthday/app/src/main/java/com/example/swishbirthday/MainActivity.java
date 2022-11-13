package com.example.swishbirthday;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Toast toast;

    private ListView listView;
    private EditText editText;
    private List<Birthday> listaCumpleaños;
    private List<String> listaInfo;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeVariables();
        borrarCumpleaños();
        introducirCumpleañosPrueba();
        obtenerCumpleaños(null);
        mostrarCumpleaños();
        initializeListeners();
    }

    private void initializeVariables() {
        toast = null;
        listView = findViewById(R.id.listView);
        editText = findViewById(R.id.editTextName);

        BirthDbHelper dbHelper = new BirthDbHelper(this, "SwishBirthday.db");
        db = dbHelper.getWritableDatabase();
    }

    private void borrarCumpleaños() {
        db.delete(BirthContract.BirthEntry.TABLE_NAME, null, null);
    }

    private void introducirCumpleañosPrueba() {
        ContentValues values = new ContentValues();

        values.put(BirthContract.BirthEntry.COLUMN_NAME_NOMBRE, "Angelo");
        values.put(BirthContract.BirthEntry.COLUMN_NAME_FECHA, "Apr 27, 2001");
        db.insert(BirthContract.BirthEntry.TABLE_NAME, null, values);

        values.put(BirthContract.BirthEntry.COLUMN_NAME_NOMBRE, "Paula");
        values.put(BirthContract.BirthEntry.COLUMN_NAME_FECHA, "Dec 13, 2001");
        db.insert(BirthContract.BirthEntry.TABLE_NAME, null, values);

        values.put(BirthContract.BirthEntry.COLUMN_NAME_NOMBRE, "Random");
        values.put(BirthContract.BirthEntry.COLUMN_NAME_FECHA, "Dec 13, 2001");
        db.insert(BirthContract.BirthEntry.TABLE_NAME, null, values);

        values.put(BirthContract.BirthEntry.COLUMN_NAME_NOMBRE, "Galo");
        values.put(BirthContract.BirthEntry.COLUMN_NAME_FECHA, "Jan 10, 2001");
        values.put(BirthContract.BirthEntry.COLUMN_NAME_HORA, "23:55");
        db.insert(BirthContract.BirthEntry.TABLE_NAME, null, values);
    }

    @SuppressLint("Range")
    private void obtenerCumpleaños(String name) {
        String[] columns = {
                BirthContract.BirthEntry._ID,
                BirthContract.BirthEntry.COLUMN_NAME_NOMBRE,
                BirthContract.BirthEntry.COLUMN_NAME_FECHA,
                BirthContract.BirthEntry.COLUMN_NAME_HORA
        };

        String sortOrder = BirthContract.BirthEntry.COLUMN_NAME_NOMBRE + " ASC";
        Cursor cursor;
        if (name != null) {
            String where = BirthContract.BirthEntry.COLUMN_NAME_NOMBRE + " LIKE ?";
            String[] whereArgs = { "%" + name + "%" };
            cursor = db.query(BirthContract.BirthEntry.TABLE_NAME, columns, where, whereArgs, null, null, sortOrder);
        } else{
            cursor = db.query(BirthContract.BirthEntry.TABLE_NAME, columns, null, null, null, null, sortOrder);
        }

        try {
            listaCumpleaños = new ArrayList<>();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(BirthContract.BirthEntry._ID));
                String nombre = cursor.getString(cursor.getColumnIndex(BirthContract.BirthEntry.COLUMN_NAME_NOMBRE));
                String fecha = cursor.getString(cursor.getColumnIndex(BirthContract.BirthEntry.COLUMN_NAME_FECHA));

                Date date = null;
                try {
                    date = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).parse(fecha);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String hora = cursor.getString(cursor.getColumnIndex(BirthContract.BirthEntry.COLUMN_NAME_HORA));

                listaCumpleaños.add(new Birthday(id, nombre, date, hora));
            }
        } finally {
            cursor.close();
        }

        listaInfo = new ArrayList<>();
        for (int i = 0; i < listaCumpleaños.size(); i++){
            listaInfo.add(listaCumpleaños.get(i).toString());
        }
    }

    private void mostrarCumpleaños() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaInfo);
        listView.setAdapter(adapter);
    }

    private void initializeListeners() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Birthday birthday = listaCumpleaños.get(position);
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                intent.putExtra("birthday", birthday.getId());
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddBirthdayActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button: filtrarCumpleaños(); break;
        }
    }

    private void filtrarCumpleaños() {
        String name = editText.getText().toString();
        obtenerCumpleaños(name);
        mostrarCumpleaños();
        if (listaInfo.isEmpty()) showToast(getResources().getString(R.string.toast_search));
    }

    private void showToast(String text) {
        if (toast != null) toast.cancel();
        toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        editText.setText("");
        obtenerCumpleaños(null);
        mostrarCumpleaños();
    }
}