package com.anand.spaceexplorer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "MainActivity";
    private TextView dateText;
    private Button buttonSearch;
    private String queryDate;
    Calendar calendarSet = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateText = findViewById(R.id.date_text);
        Button buttonApod = findViewById(R.id.apod_button);
        buttonSearch = findViewById(R.id.search_button);

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year, mon, day;
                year = calendar.get(Calendar.YEAR);
                mon = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(MainActivity.this, MainActivity.this, year, mon, day);
                datePicker.show();
            }
        });

        buttonApod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (queryDate == null) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setMessage("Select Date first ");
                    dialog.setTitle("Wait !");
                    dialog.setIcon(R.drawable.ic_block);
                    dialog.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                }
                            });
                    AlertDialog alertDialog = dialog.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                } else {
                    Intent intent = new Intent(MainActivity.this, ApodActivity.class);
                    intent.putExtra("date", queryDate);
                    startActivity(intent);
                }
            }

        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        calendarSet.set(Calendar.YEAR, year);
        calendarSet.set(Calendar.MONTH, month);
        calendarSet.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        Date currentDate= Calendar.getInstance().getTime();
        Log.i(TAG, "onDateSet: Current Date"+currentDate);
        Date date = calendarSet.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if (!date.after(currentDate)){
            dateText.setText(dateFormat.format(date));
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            queryDate = dateFormat.format(date);
        }else{
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setMessage("Select a date from past... ");
            dialog.setTitle("Error !");
            dialog.setIcon(R.drawable.ic_block);
            dialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                        }
                    });
            AlertDialog alertDialog = dialog.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
    }
}