package com.example.swishbirthday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class InfoActivity extends AppCompatActivity {

    private Toast toast;

    private Birthday birthday;
    private TextView textName, textDate, textTime, textYears;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        initializeVariables();
        setLabel();
        mostrarCumpleaños();
    }

    private void initializeVariables() {
        toast = null;
        textName = findViewById(R.id.textName);
        textDate = findViewById(R.id.textDate);
        textTime = findViewById(R.id.textTime);
        textYears = findViewById(R.id.textYears);

        BirthDbHelper dbHelper = new BirthDbHelper(this, "SwishBirthday.db");
        db = dbHelper.getWritableDatabase();

        Intent intent = getIntent();
        int id = intent.getIntExtra("birthday", -1);
        birthday = obtenerCumpleaños(id);
    }

    private Birthday obtenerCumpleaños(int id) {
        String[] columns = {
                BirthContract.BirthEntry._ID,
                BirthContract.BirthEntry.COLUMN_NAME_NOMBRE,
                BirthContract.BirthEntry.COLUMN_NAME_FECHA,
                BirthContract.BirthEntry.COLUMN_NAME_HORA
        };

        String where = BirthContract.BirthEntry._ID + " = ?";
        String[] whereArgs = { String.valueOf(id) };

        Cursor cursor = db.query(
                BirthContract.BirthEntry.TABLE_NAME,
                columns, where, whereArgs, null, null, null);
        cursor.moveToFirst();

        int idb = cursor.getInt(cursor.getColumnIndexOrThrow(BirthContract.BirthEntry._ID));
        String nombre = cursor.getString(cursor.getColumnIndexOrThrow(BirthContract.BirthEntry.COLUMN_NAME_NOMBRE));
        String fecha = cursor.getString(cursor.getColumnIndexOrThrow(BirthContract.BirthEntry.COLUMN_NAME_FECHA));

        Date date = null;
        try {
            date = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String hora = cursor.getString(cursor.getColumnIndexOrThrow(BirthContract.BirthEntry.COLUMN_NAME_HORA));

        cursor.close();

        return new Birthday(idb, nombre, date, hora);
    }

    private void setLabel() {
        if (Locale.getDefault().getLanguage().equals("es")) {
            setTitle("Cumpleaños de " + birthday.getNombre());
        } else {
            setTitle(birthday.getNombre() + "’s birthday");
        }
    }

    private void mostrarCumpleaños() {
        textName.setText(birthday.getNombre());
        textDate.setText(new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(birthday.getFecha()));
        textTime.setText(birthday.getHora() == null ? "--:--" : birthday.getHora());
        textYears.setText(String.valueOf(TimeUnit.MILLISECONDS.toDays((new Date().getTime() - birthday.getFecha().getTime())/365)+1));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonEdit: goToEditActivity(); break;
            case R.id.buttonDelete: deleteBirthday(); break;
        }
    }

    private void goToEditActivity() {
        Intent intent = new Intent(InfoActivity.this, EditBirthdayActivity.class);
        intent.putExtra("birthday", birthday.getId());
        startActivity(intent);
    }

    private void deleteBirthday() {
        String where = BirthContract.BirthEntry._ID + " = ?";
        String[] whereArgs = { String.valueOf(birthday.getId()) };
        db.delete(BirthContract.BirthEntry.TABLE_NAME, where, whereArgs);

        showToast(getResources().getString(R.string.toast_delete));
        finish();
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
        birthday = obtenerCumpleaños(birthday.getId());
        setLabel();
        mostrarCumpleaños();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}