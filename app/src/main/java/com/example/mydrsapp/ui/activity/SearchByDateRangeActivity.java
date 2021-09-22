package com.example.mydrsapp.ui.activity;

import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mydrsapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class SearchByDateRangeActivity extends AppCompatActivity {

    TextView startDate, endDate;
    String sDate, eDate, monthS, monthE, startingDate, endingDate, todayDate;
    int sMonth, eMonth, count, startCount;
    Date currentDate, date1, date2, date3, date4;
    ArrayList currentSepDate;
    long startD, endD, todayD, diff, diff1;


    Button search;
    String id, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_date_range);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setNavigationBarColor(Color.parseColor("#0272B9"));
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString("id");
            Log.i("Patient ID Share", id);
            name = bundle.getString("name");
            Log.i("Patient Name Share", name);
        }
        Log.i("Outside Share ID", id);
        Log.i("Outside Share Name", name);

        currentSepDate = new ArrayList<>();

        startDate = findViewById(R.id.start_date);
        endDate = findViewById(R.id.end_date);
        search = findViewById(R.id.search_dr);
        search.setTextColor(getResources().getColor(R.color.de_active));

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

//-------------------------------------- Today's Date ----------------------------------------------
        currentDate = Calendar.getInstance().getTime();
        Log.i("Current/Today's Date", String.valueOf(currentDate));

        String[] allDate = currentDate.toString().split(" ", 6);

        for (String sepDate : allDate) {
            Log.i("Sep Date", sepDate);
            currentSepDate.add(sepDate);
        }
        String dayC = (String) currentSepDate.get(0);
        String monC = (String) currentSepDate.get(1);
        String dateC = (String) currentSepDate.get(2);
        String timeC = (String) currentSepDate.get(3);
        String gmtC = (String) currentSepDate.get(4);
        String yearC = (String) currentSepDate.get(5);

        Log.i("Separated Day", dayC);
        Log.i("Separated Date", dateC);
        Log.i("Separated Month", monC);
        Log.i("Separate timeC", timeC);
        Log.i("Separated Year", yearC);

        if (monC.equals("Jan")) {
            count = 1;
        } else if (monC.equals("Feb")) {
            count = 2;
        } else if (monC.equals("Mar")) {
            count = 3;
        } else if (monC.equals("Apr")) {
            count = 4;
        } else if (monC.equals("May")) {
            count = 5;
        } else if (monC.equals("Jun")) {
            count = 6;
        } else if (monC.equals("Jul")) {
            count = 7;
        } else if (monC.equals("Aug")) {
            count = 8;
        } else if (monC.equals("Sep")) {
            count = 9;
        } else if (monC.equals("Oct")) {
            count = 10;
        } else if (monC.equals("Nov")) {
            count = 11;
        } else if (monC.equals("Dec")) {
            count = 12;
        }

        todayDate = dateC + "/" + count + "/" + yearC;
        Log.i("Today's Date", todayDate);

//------------------------------------- Set Start Date ---------------------------------------------
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(SearchByDateRangeActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        if (month == 0) {
                            sMonth = 1;
                            monthS = "Jan";
                        } else if (month == 1) {
                            monthS = "Feb";
                            sMonth = 2;
                        } else if (month == 2) {
                            monthS = "Mar";
                            sMonth = 3;
                        } else if (month == 3) {
                            sMonth = 4;
                            monthS = "Apr";
                        } else if (month == 4) {
                            sMonth = 5;
                            monthS = "May";
                        } else if (month == 5) {
                            sMonth = 6;
                            monthS = "Jun";
                        } else if (month == 6) {
                            sMonth = 7;
                            monthS = "Jul";
                        } else if (month == 7) {
                            sMonth = 8;
                            monthS = "Aug";
                        } else if (month == 8) {
                            sMonth = 9;
                            monthS = "Sep";
                        } else if (month == 9) {
                            sMonth = 10;
                            monthS = "Oct";
                        } else if (month == 10) {
                            sMonth = 11;
                            monthS = "Nov";
                        } else if (month == 11) {
                            sMonth = 12;
                            monthS = "Dec";
                        }

                        sDate = dayOfMonth + "-" + monthS + "-" + year;
                        startDate.setText(sDate);
                        startCount =startCount+1;
                        Log.i("Length of Start Date", String.valueOf(startCount));

                        startingDate = dayOfMonth + "/" + sMonth + "/" + year;

                        Log.i("Separated sDate", startingDate);
                        Log.i("Separated sMonth", String.valueOf(sMonth));

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            date1 = simpleDateFormat.parse(startingDate);
                            Log.i("Date Parsed", String.valueOf(date1));
                            date2 = simpleDateFormat.parse(todayDate);
                            startD = date1.getTime();
                            todayD = date2.getTime();

                            diff = (todayD - startD) / 86400000;
                            Log.i("Diff Start-Today Date", String.valueOf(diff));

                            //----------------------------------------------------------------------
                            DatePickerDialog datePickerDialog = new DatePickerDialog(SearchByDateRangeActivity.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                    if (month == 0) {
                                        eMonth = 1;
                                        monthE = "Jan";
                                    } else if (month == 1) {
                                        monthE = "Feb";
                                        eMonth = 2;
                                    } else if (month == 2) {
                                        monthE = "Mar";
                                        eMonth = 3;
                                    } else if (month == 3) {
                                        eMonth = 4;
                                        monthE = "Apr";
                                    } else if (month == 4) {
                                        eMonth = 5;
                                        monthE = "May";
                                    } else if (month == 5) {
                                        eMonth = 6;
                                        monthE = "Jun";
                                    } else if (month == 6) {
                                        eMonth = 7;
                                        monthE = "Jul";
                                    } else if (month == 7) {
                                        eMonth = 8;
                                        monthE = "Aug";
                                    } else if (month == 8) {
                                        eMonth = 9;
                                        monthE = "Sep";
                                    } else if (month == 9) {
                                        eMonth = 10;
                                        monthE = "Oct";
                                    } else if (month == 10) {
                                        eMonth = 11;
                                        monthE = "Nov";
                                    } else if (month == 11) {
                                        eMonth = 12;
                                        monthE = "Dec";
                                    }

                                    eDate = dayOfMonth + "-" + monthE + "-" + year;
                                    endDate.setText(eDate);
                                    endingDate = dayOfMonth + "/" + eMonth + "/" + year;

                                    Log.i("Separated eDate", endingDate);
                                    Log.i("Separated eMonth", String.valueOf(eMonth));

                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    try {
                                        date3 = simpleDateFormat.parse(endingDate);
                                        Log.i("Date Parsed", String.valueOf(date3));
                                        date4 = simpleDateFormat.parse(todayDate);

                                        endD = date3.getTime();
                                        todayD = date4.getTime();

                                        diff1 = (todayD - endD) / 86400000;
                                        Log.i("Diff End-Today Date", String.valueOf(diff1));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, year, month, day);

                            datePickerDialog.getDatePicker().setMinDate(startD);
                            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                            datePickerDialog.show();
                            //----------------------------------------------------------------------
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

//--------------------------------------- Set End Date ---------------------------------------------

        Log.i("Length of Start Date2", String.valueOf(startCount));

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Length of Start Date3", String.valueOf(startCount));
                if (startCount==1){

                DatePickerDialog datePickerDialog = new DatePickerDialog(SearchByDateRangeActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


                        if (month == 0) {
                            eMonth = 1;
                            monthE = "Jan";
                        } else if (month == 1) {
                            monthE = "Feb";
                            eMonth = 2;
                        } else if (month == 2) {
                            monthE = "Mar";
                            eMonth = 3;
                        } else if (month == 3) {
                            eMonth = 4;
                            monthE = "Apr";
                        } else if (month == 4) {
                            eMonth = 5;
                            monthE = "May";
                        } else if (month == 5) {
                            eMonth = 6;
                            monthE = "Jun";
                        } else if (month == 6) {
                            eMonth = 7;
                            monthE = "Jul";
                        } else if (month == 7) {
                            eMonth = 8;
                            monthE = "Aug";
                        } else if (month == 8) {
                            eMonth = 9;
                            monthE = "Sep";
                        } else if (month == 9) {
                            eMonth = 10;
                            monthE = "Oct";
                        } else if (month == 10) {
                            eMonth = 11;
                            monthE = "Nov";
                        } else if (month == 11) {
                            eMonth = 12;
                            monthE = "Dec";
                        }

                        eDate = dayOfMonth + "-" + monthE + "-" + year;
                        endDate.setText(eDate);

                        endingDate = dayOfMonth + "/" + eMonth + "/" + year;

                        Log.i("Separated eDate", endingDate);
                        Log.i("Separated eMonth", String.valueOf(eMonth));

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            date3 = simpleDateFormat.parse(endingDate);
                            Log.i("Date Parsed", String.valueOf(date3));
                            date4 = simpleDateFormat.parse(todayDate);

                            endD = date3.getTime();
                            todayD = date4.getTime();

                            diff1 = (todayD - endD) / 86400000;
                            Log.i("Diff End-Today Date", String.valueOf(diff1));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, year, month, day);

                datePickerDialog.getDatePicker().setMinDate(startD);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        }
        });
    }

    public void onSearchDR(View view) {
        String countSt = startDate.getText().toString().trim();
        String countEt = endDate.getText().toString().trim();
        if (!countSt.isEmpty() && !countEt.isEmpty()) {
            search.setTextColor(getResources().getColor(R.color.active));
            Intent i1 = new Intent(SearchByDateRangeActivity.this, ResultForSearchByDateActivity.class);
            i1.putExtra("startDays", diff);
            i1.putExtra("endDays", diff1);
            i1.putExtra("id10", id);
            i1.putExtra("name11", name);
            startActivity(i1);
            overridePendingTransition(0, 0);
        }
    }

    public void date_range_back(View view) {
        Intent i1 = new Intent(SearchByDateRangeActivity.this, SearchActivity.class);
        i1.putExtra("id3", id);
        i1.putExtra("name", name);
        startActivity(i1);
        overridePendingTransition(0, 0);
        finish();
    }
}