package com.example.swishbirthday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    BirthDbHelper dbHelper;
    List<Birthday> listaCumpleaños;
    List<String> listaInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new BirthDbHelper(getApplicationContext(), "SwishBirthday.db");

        listView= findViewById(R.id.listView);

        borrarCumpleaños();
        introducirCumpleañosPrueba();

        obtenerCumpleaños();

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaInfo);
        listView.setAdapter(adapter);
    }

    private void borrarCumpleaños() {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();

        long cnt = db.delete(BirthContract.BirthEntry.TABLE_NAME, null, null);
    }

    private void introducirCumpleañosPrueba() {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BirthContract.BirthEntry.COLUMN_NAME_NOMBRE, "Angelo");
        values.put(BirthContract.BirthEntry.COLUMN_NAME_FECHA, "Apr 27, 2001");
        long id = db.insert(BirthContract.BirthEntry.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(BirthContract.BirthEntry.COLUMN_NAME_NOMBRE, "Galo");
        values.put(BirthContract.BirthEntry.COLUMN_NAME_FECHA, "Jan 10, 2001");
        values.put(BirthContract.BirthEntry.COLUMN_NAME_HORA, "23:55");
        id = db.insert(BirthContract.BirthEntry.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(BirthContract.BirthEntry.COLUMN_NAME_NOMBRE, "Paula");
        values.put(BirthContract.BirthEntry.COLUMN_NAME_FECHA, "Dec 13, 2001");
        id = db.insert(BirthContract.BirthEntry.TABLE_NAME, null, values);


    }

    private void obtenerCumpleaños() {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();

        listaCumpleaños = new ArrayList<>();

        String[] columns = {
                BirthContract.BirthEntry._ID,
                BirthContract.BirthEntry.COLUMN_NAME_NOMBRE,
                BirthContract.BirthEntry.COLUMN_NAME_FECHA,
                BirthContract.BirthEntry.COLUMN_NAME_HORA
        };

        String sortOrder =  BirthContract.BirthEntry.COLUMN_NAME_FECHA + " ASC";

        Cursor cursor = db.query(BirthContract.BirthEntry.TABLE_NAME, columns, null, null, null, null, sortOrder);

        try {
            while (cursor.moveToNext()) {
                /*String nombre = cursor.getString(cursor.getColumnIndex(BirthContract.BirthEntry.COLUMN_NAME_NOMBRE));
                String fecha = cursor.getString(cursor.getColumnIndex(BirthContract.BirthEntry.COLUMN_NAME_FECHA));
                String hora = cursor.getString(cursor.getColumnIndex(BirthContract.BirthEntry.COLUMN_NAME_HORA));*/
                String nombre = cursor.getString(1);
                String fecha = cursor.getString(2);

                Date date=null;
                try {
                    date = new SimpleDateFormat("MMM dd, yyyy",new Locale("en","EN")).parse(fecha);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String hora = cursor.getString(3);

                listaCumpleaños.add(new Birthday(nombre, date, hora));
            }
        } finally {
            cursor.close();
        }

        listaInfo = new ArrayList<>();

        for(int i=0;i<listaCumpleaños.size();i++){
            listaInfo.add(listaCumpleaños.get(i).toString());
        }

    }
}