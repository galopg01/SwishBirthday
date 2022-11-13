package com.example.swishbirthday;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditBirthdayActivity extends AppCompatActivity {

    private Toast toast;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private ImageView clearTimeButton2;

    private EditText editTextEditName, editTextEditDate, editTextEditTime;
    private SQLiteDatabase db;

    private int birthdayId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_birthday);
        initializeVariables();
        initializePickers();
        retrieveBirthdayAndFillFields();
    }

    private void initializeVariables() {
        toast = null;
        editTextEditName = (EditText) findViewById(R.id.editTextEditName);
        editTextEditDate = (EditText) findViewById(R.id.editTextEditDate);
        editTextEditTime = (EditText) findViewById(R.id.editTextEditTime);
        clearTimeButton2 = (ImageView) findViewById((R.id.buttonClearTime2));

        Intent intent = getIntent();
        birthdayId = intent.getIntExtra("birthday", -1);

        BirthDbHelper dbHelper = new BirthDbHelper(this, "SwishBirthday.db");
        db = dbHelper.getWritableDatabase();
    }

    private void initializePickers() {
        initDatePicker();
        initTimePicker();
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month += 1;
            String dayPadding = day <= 9 ? "0" : "";
            String monthPadding = month <= 9 ? "0" : "";
            String selectedDate = dayPadding + day + "/" + monthPadding + month + "/" + year;

            try {
                Date date = DateFormat.getDateFormat(this).parse(selectedDate);
                editTextEditDate.setText(new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
    }

    private void initTimePicker() {
        TimePickerDialog.OnTimeSetListener timeSetListener = (timePicker, hour, minute) -> {
            String hourPadding = hour <= 9 ? "0" : "";
            String minutePadding = minute <= 9 ? "0" : "";
            String selectedTime = hourPadding + hour + ":" + minutePadding + minute;

            editTextEditTime.setText(selectedTime);
            clearTimeButton2.setVisibility(View.VISIBLE);
        };

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(this, timeSetListener, hour,
                minute, DateFormat.is24HourFormat(this));
    }

    private void retrieveBirthdayAndFillFields() {
        String[] columns = {
                BirthContract.BirthEntry.COLUMN_NAME_NOMBRE,
                BirthContract.BirthEntry.COLUMN_NAME_FECHA,
                BirthContract.BirthEntry.COLUMN_NAME_HORA
        };

        String where = BirthContract.BirthEntry._ID + " = ?";
        String[] whereArgs = { String.valueOf(birthdayId) };

        Cursor cursor = db.query(BirthContract.BirthEntry.TABLE_NAME,
                columns, where, whereArgs, null, null, null);
        cursor.moveToFirst();

        String name = cursor.getString(cursor.getColumnIndexOrThrow(BirthContract.BirthEntry.COLUMN_NAME_NOMBRE));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(BirthContract.BirthEntry.COLUMN_NAME_FECHA));
        date = dateToLocal(date);
        String time = cursor.getString(cursor.getColumnIndexOrThrow(BirthContract.BirthEntry.COLUMN_NAME_HORA));

        cursor.close();

        editTextEditName.setText(name);
        editTextEditDate.setText(date);
        if (time != null) {
            editTextEditTime.setText(time);
            clearTimeButton2.setVisibility(View.VISIBLE);
        }
    }

    private String dateToLocal(String date) {
        Date dateReturned = null;
        try {
            dateReturned = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(dateReturned);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editTextEditDate: datePickerDialog.show(); break;
            case R.id.editTextEditTime: timePickerDialog.show(); break;
            case R.id.buttonClearTime2: clearTimeField(); break;
            case R.id.buttonEditBirthday: editBirthday(); break;
        }
    }

    private void clearTimeField() {
        editTextEditTime.setText("");
        clearTimeButton2.setVisibility(View.GONE);
    }

    private void editBirthday() {
        String name = editTextEditName.getText().toString();
        if (name.isEmpty()) {
            showToast(getResources().getString(R.string.empty_name));
        } else {
            String date = convertDate(editTextEditDate.getText().toString());
            String time = editTextEditTime.getText().toString();
            ContentValues values = new ContentValues();

            values.put(BirthContract.BirthEntry.COLUMN_NAME_NOMBRE, name);
            values.put(BirthContract.BirthEntry.COLUMN_NAME_FECHA, date);
            if (!time.isEmpty()) {
                values.put(BirthContract.BirthEntry.COLUMN_NAME_HORA, time);
            } else {
                values.put(BirthContract.BirthEntry.COLUMN_NAME_HORA, (String) null);
            }

            String where = BirthContract.BirthEntry._ID + " = ?";
            String[] whereArgs = { String.valueOf(birthdayId) };

            db.update(BirthContract.BirthEntry.TABLE_NAME, values, where, whereArgs);

            showToast(getResources().getString(R.string.toast_edit));
            finish();
        }
    }

    private String convertDate(String date) {
        Date dateReturned = null;
        try {
            dateReturned = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(dateReturned);
    }

    private void showToast(String text) {
        if (toast != null) toast.cancel();
        toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.show();
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