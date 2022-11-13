package com.example.swishbirthday;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddBirthdayActivity extends AppCompatActivity {

    private Toast toast;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private ImageView clearTimeButton;

    private EditText editTextAddName, editTextAddDate, editTextAddTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_birthday);
        initializeVariables();
        initializePickers();
    }

    private void initializeVariables() {
        toast = null;
        editTextAddName = (EditText) findViewById(R.id.editTextAddName);
        editTextAddDate = (EditText) findViewById(R.id.editTextAddDate);
        editTextAddTime = (EditText) findViewById(R.id.editTextAddTime);
        clearTimeButton = (ImageView) findViewById((R.id.buttonClearTime));
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
                editTextAddDate.setText(new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date));
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

            editTextAddTime.setText(selectedTime);
            clearTimeButton.setVisibility(View.VISIBLE);
        };

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(this, timeSetListener, hour,
                minute, DateFormat.is24HourFormat(this));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editTextAddDate: datePickerDialog.show(); break;
            case R.id.editTextAddTime: timePickerDialog.show(); break;
            case R.id.buttonClearTime: clearTimeField(); break;
            case R.id.buttonAddBirthday: addBirthday(); break;
        }
    }

    private void clearTimeField() {
        editTextAddTime.setText("");
        clearTimeButton.setVisibility(View.GONE);
    }

    private void addBirthday() {
        String name = editTextAddName.getText().toString();
        if (name.isEmpty()) {
            showToast(getResources().getString(R.string.empty_name));
        } else {
            String date = editTextAddDate.getText().toString();
            if (date.isEmpty()) {
                showToast(getResources().getString(R.string.empty_date));
            } else {
                date = convertDate(date);
                String time = editTextAddTime.getText().toString();
                BirthDbHelper dbHelper = new BirthDbHelper(this, "SwishBirthday.db");
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                values.put(BirthContract.BirthEntry.COLUMN_NAME_NOMBRE, name);
                values.put(BirthContract.BirthEntry.COLUMN_NAME_FECHA, date);
                if (!time.isEmpty()) values.put(BirthContract.BirthEntry.COLUMN_NAME_HORA, time);
                db.insert(BirthContract.BirthEntry.TABLE_NAME, null, values);

                showToast(getResources().getString(R.string.toast_add));
                finish();
            }
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