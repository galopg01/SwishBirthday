package com.example.swishbirthday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class InfoActivity extends AppCompatActivity {

    private Toast toast;

    Birthday birthday;
    BirthDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        toast = null;
        dbHelper = new BirthDbHelper(getApplicationContext(), "SwishBirthday.db");

        Intent intent= getIntent();
        int id =  intent.getIntExtra("birthday", -1);
        System.out.println(id);
        birthday = obtenerCumpleaños(id);

        TextView textName = findViewById(R.id.textName);
        textName.setText(birthday.getNombre());

        TextView textDate = findViewById(R.id.textDate);
        textDate.setText(new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(birthday.getFecha()));

        TextView textHour = findViewById(R.id.textHour);
        textHour.setText(birthday.getHora() == null ? "--:--" : birthday.getHora());

        TextView textYears = findViewById(R.id.textYears);
        textYears.setText(String.valueOf(TimeUnit.MILLISECONDS.toDays((new Date().getTime() - birthday.getFecha().getTime())/365)+1));
    }

    private Birthday obtenerCumpleaños(int id) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();

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
                columns,
                where,
                whereArgs,
                null,
                null,
                null
        );

        cursor.moveToFirst();

        int idb = cursor.getInt(cursor.getColumnIndexOrThrow(BirthContract.BirthEntry._ID));
        String nombre = cursor.getString(cursor.getColumnIndexOrThrow(BirthContract.BirthEntry.COLUMN_NAME_NOMBRE));
        String fecha = cursor.getString(cursor.getColumnIndexOrThrow(BirthContract.BirthEntry.COLUMN_NAME_FECHA));

        Date date=null;
        try {
            date = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).parse(fecha);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        String hora = cursor.getString(cursor.getColumnIndexOrThrow(BirthContract.BirthEntry.COLUMN_NAME_HORA));

        cursor.close();

        return new Birthday(idb, nombre, date, hora);

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
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
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
    public void onResume()
    {
        super.onResume();
        obtenerCumpleaños(birthday.getId());
    }
}