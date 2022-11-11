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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    EditText editText;
    BirthDbHelper dbHelper;
    List<Birthday> listaCumpleaños;
    List<String> listaInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new BirthDbHelper(getApplicationContext(), "SwishBirthday.db");

        listView= findViewById(R.id.listView);
        editText = findViewById(R.id.editTextName);

        borrarCumpleaños();
        introducirCumpleañosPrueba();

        obtenerCumpleaños(null);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaInfo);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Birthday birthday = listaCumpleaños.get(position);
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                intent.putExtra("birthday", birthday.getId());
                startActivity(intent);
            }
        });
    }

    private void borrarCumpleaños() {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();

        db.delete(BirthContract.BirthEntry.TABLE_NAME, null, null);
    }

    private void introducirCumpleañosPrueba() {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BirthContract.BirthEntry.COLUMN_NAME_NOMBRE, "Angelo");
        values.put(BirthContract.BirthEntry.COLUMN_NAME_FECHA, "2001-04-27");
        db.insert(BirthContract.BirthEntry.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(BirthContract.BirthEntry.COLUMN_NAME_NOMBRE, "Galo");
        values.put(BirthContract.BirthEntry.COLUMN_NAME_FECHA, "2001-01-10");
        values.put(BirthContract.BirthEntry.COLUMN_NAME_HORA, "23:55");
        db.insert(BirthContract.BirthEntry.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(BirthContract.BirthEntry.COLUMN_NAME_NOMBRE, "Paula");
        values.put(BirthContract.BirthEntry.COLUMN_NAME_FECHA, "2001-12-13");
        db.insert(BirthContract.BirthEntry.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(BirthContract.BirthEntry.COLUMN_NAME_NOMBRE, "Random");
        values.put(BirthContract.BirthEntry.COLUMN_NAME_FECHA, "2010-12-13");
        db.insert(BirthContract.BirthEntry.TABLE_NAME, null, values);


    }

    @SuppressLint("Range") private void obtenerCumpleaños(String name) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();

        listaCumpleaños = new ArrayList<>();

        String[] columns = {
                BirthContract.BirthEntry._ID,
                BirthContract.BirthEntry.COLUMN_NAME_NOMBRE,
                BirthContract.BirthEntry.COLUMN_NAME_FECHA,
                BirthContract.BirthEntry.COLUMN_NAME_HORA
        };

        String sortOrder = /*"DATE(" +new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ")-DATE("+ BirthContract.BirthEntry.COLUMN_NAME_FECHA+ ")"*/ BirthContract.BirthEntry.COLUMN_NAME_NOMBRE + " ASC";
        Cursor cursor;
        if(name != null) {
            String where = BirthContract.BirthEntry.COLUMN_NAME_NOMBRE + " LIKE ?";
            String[] whereArgs = { "%" + name + "%" };
            cursor = db.query(BirthContract.BirthEntry.TABLE_NAME, columns, where, whereArgs, null, null, sortOrder);

        }else{
            cursor = db.query(BirthContract.BirthEntry.TABLE_NAME, columns, null, null, null, null, sortOrder);

        }

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(BirthContract.BirthEntry._ID));
                String nombre = cursor.getString(cursor.getColumnIndex(BirthContract.BirthEntry.COLUMN_NAME_NOMBRE));
                String fecha = cursor.getString(cursor.getColumnIndex(BirthContract.BirthEntry.COLUMN_NAME_FECHA));


                Date date=null;
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd",new Locale("en","EN")).parse(fecha);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String hora = cursor.getString(cursor.getColumnIndex(BirthContract.BirthEntry.COLUMN_NAME_HORA));

                listaCumpleaños.add(new Birthday(id,nombre, date, hora));
            }
        } finally {
            cursor.close();
        }

        listaInfo = new ArrayList<>();

        for(int i=0;i<listaCumpleaños.size();i++){
            listaInfo.add(listaCumpleaños.get(i).toString());
        }
    }

    public void onClick(View view) {

        switch ( view . getId ()) {
            case R.id. button : String name= editText.getText().toString();
                obtenerCumpleaños(name);
                System.out.println("Nombre: "+name);
                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaInfo);
                listView.setAdapter(adapter);
                break ;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        editText.setText("");
        obtenerCumpleaños(null);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaInfo);
        listView.setAdapter(adapter);
    }
}