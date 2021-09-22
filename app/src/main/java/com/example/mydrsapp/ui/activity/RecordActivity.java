package com.example.mydrsapp.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mydrsapp.utils.ApiClient;
import com.example.mydrsapp.utils.FileUtils;
import com.example.mydrsapp.R;
import com.example.mydrsapp.model.RecordRequest;
import com.example.mydrsapp.model.RecordResponse;
import com.example.mydrsapp.model.UploadResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordActivity extends AppCompatActivity {

    String patient_id, specialty_id, specialty_id_all, specialty_name, selected_spc_name, selected_spc_id, provider_name, patient_name, catagory_name, selected_pro_name, provider_id;
    String selected_pro_id, selected_category_name, recordFile2, recordFile3, type;
    int recording_count;
    Button start_record_btn;
    ImageView proImg, wave;
    ImageButton menu_btn;
    //    Animation animation;
    Dialog recordingDialog, dialog, dialog2, dialog3, savingDialog, dialogAbout, dialogVision;
    TextView txt1, txt2, pro, titleButton, titleButtonVision;
    BottomNavigationView bottomNavigationView;
    ArrayList<String> category_list, lastDuration, arrayList, arrayList2, spc_array, data, spc_name, pro_name, pro_id, txt2_array, temp, selected_pro_id_array;
    private RequestQueue mQueue;
    ConstraintLayout selectSpecialty, selectProvider;

    //--------------------------------Start Recording-----------------------------------------------
    int PERMISSION_CODE = 52;
    ImageView recordBtn;
    MediaRecorder mediaRecorder;
    String recordFile;
    int duration;

    String recordPermission = Manifest.permission.RECORD_AUDIO;
    boolean isRecording = false, isResume;

    Handler handler;
    long tMilliSec, tStart, tBuff, tUpdate = 0L;
    int sec, min, milliSec;

    TextView changeStatus;
    Chronometer chronometer;
    TextView select;
    ArrayList<String> category;

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tMilliSec = SystemClock.uptimeMillis() - tStart;
            tUpdate = tBuff + tMilliSec;
            sec = (int) (tUpdate / 1000);
            min = sec / 60;
            sec = sec % 60;
            milliSec = (int) (tUpdate % 100);
            chronometer.setText(String.format("%02d", min) + ":" + String.format("%02d", sec) + ":" + String.format("%02d", milliSec));
            handler.postDelayed(this, 60);
        }
    };
    //----------------------------------------------------------------------------------------------


    ImageView record, search, add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#0272B9"));
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }

        selectSpecialty = findViewById(R.id.selectSpecialty2);
        selectProvider = findViewById(R.id.selectProvider1);

        txt1 = findViewById(R.id.select_soc1);
        txt2 = findViewById(R.id.select_pro1);
        pro = findViewById(R.id.pro1);
        proImg = findViewById(R.id.proImg);

        txt2.setVisibility(View.GONE);
        pro.setVisibility(View.GONE);
        proImg.setVisibility(View.GONE);

        arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        spc_array = new ArrayList<>();
        data = new ArrayList<>();
        spc_name = new ArrayList<>();
        pro_name = new ArrayList<>();
        txt2_array = new ArrayList<>();
        temp = new ArrayList<>();
        pro_id = new ArrayList<>();
        selected_pro_id_array = new ArrayList<>();
        lastDuration = new ArrayList<>();
        category_list = new ArrayList<>();

        start_record_btn = findViewById(R.id.start_record_btn);
        start_record_btn.setVisibility(View.GONE);

        mQueue = Volley.newRequestQueue(RecordActivity.this);

        record = findViewById(R.id.rec_rec);
        record.setImageDrawable(getDrawable(R.drawable.record_active));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            patient_id = bundle.getString("id2");
            Log.i("Patient ID On Record", patient_id);
            patient_name = bundle.getString("name");
            Log.i("patient name on record", patient_name);
        }

        handler = new Handler();

        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.record);
        bottomNavigationView.setItemIconTintList(null);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.search:
                        Intent i1 = new Intent(RecordActivity.this, SearchActivity.class);
                        i1.putExtra("id3", patient_id);
                        i1.putExtra("name", patient_name);
                        startActivity(i1);
                        overridePendingTransition(0, 0);
//                        finish();
                        return true;

                    case R.id.add_dr:
                        Intent i2 = new Intent(RecordActivity.this, NewProviderActivity.class);
                        i2.putExtra("id1", patient_id);
                        i2.putExtra("name", patient_name);
                        i2.putExtra("bottom_nav", true);
                        startActivity(i2);
                        overridePendingTransition(0, 0);
//                        finish();
                        return true;

                    case R.id.record:
                        return true;
                }
                return false;
            }
        });

        //----------------------------get All specialty Ids --------------------------------------------------------------
        String url = "http://65.2.3.41:8080/speciality?patient_id=" + patient_id;
        Log.i("URL: ", url);

        JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response1) {
                try {
                    JSONArray jsonArray1 = response1.getJSONArray("data");
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject data1 = jsonArray1.getJSONObject(i);
                        specialty_id_all = data1.getString("id");
                        specialty_name = data1.getString("name");
//                        Log.i("all specialties:", specialty_id_all);
                        spc_array.add(specialty_id_all);
                        spc_name.add(specialty_name);
                    }
                    Log.i("array of all specialty", String.valueOf(spc_array));

                    //----------------------------get All provider details --------------------------------------------------------------
                    String url1 = "http://65.2.3.41:8080/provider?patient_id=" + patient_id;
                    Log.i("URL: ", url1);

                    JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response1) {
                            try {
                                JSONArray jsonArray1 = response1.getJSONArray("data");
                                for (int i = 0; i < jsonArray1.length(); i++) {
                                    JSONObject data1 = jsonArray1.getJSONObject(i);
                                    specialty_id = data1.getString("speciality_id");
                                    provider_id = data1.getString("id");
                                    provider_name = data1.getString("name");
//                                    Log.i("selected specialty ID: ", specialty_id);
                                    arrayList.add(specialty_id);
                                    pro_name.add(provider_name);
                                    pro_id.add(provider_id);
                                }

                                Log.i("arraylist of spc: ", String.valueOf(arrayList));
                                //------------------check for similar ids---------------------------
                                int size1 = arrayList.size();
                                Log.i("size of array selected:", String.valueOf(size1));
                                int size2 = spc_array.size();
                                Log.i("size of array all: ", String.valueOf(size2));
                                for (int i = 0; i < size2; i++) {
                                    for (int j = 0; j < size1; j++) {
//                                        Log.i("All specialty in for: ", spc_array.get(j));
//                                        Log.i("specialty in for loop: ", arrayList.get(j));
                                        if (arrayList.get(j).equals(spc_array.get(i))) {
                                            data.add(spc_name.get(i));
                                        }
                                    }
                                }
                                int size3 = data.size();
                                Log.i("array of selected spc: ", String.valueOf(data));
                                Collections.sort(data);
                                Log.i("sorted array : ", String.valueOf(data));
                                if (size3 == 1) {
                                    temp.add(data.get(0));
                                } else {
                                    for (int i = 0; i < size3 - 1; i++) {
                                        if (data.get(i) != data.get(i + 1)) {
                                            temp.add(data.get(i));
                                        }
                                    }
                                    for (int k = 0; k < size3; k++) {
                                        if (k == size3 - 1) {
                                            temp.add(data.get(k));
                                        }
                                    }
                                }
                                Log.i("temp array: ", String.valueOf(temp));
                                //------------------------------------------------------------------

                                //----------------------------select_specialty----------------------------------------------

                                selectSpecialty.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        record.setImageDrawable(getDrawable(R.drawable.record_inactive1));

                                        dialog = new Dialog(RecordActivity.this);
                                        dialog.setContentView(R.layout.dialog_selected_specialty);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        ListView listView = dialog.findViewById(R.id.selected_list);
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(RecordActivity.this, R.layout.custom_list_row, temp);
                                        listView.setAdapter(adapter);

                                        dialog2 = new Dialog(RecordActivity.this);
                                        dialog2.setContentView(R.layout.dialog_selected_provider);
                                        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                        ListView listView2 = dialog2.findViewById(R.id.selected_provider_list);
                                        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(RecordActivity.this, R.layout.custom_list_row, txt2_array);
                                        listView2.setAdapter(adapter2);

                                        dialog.show();
                                        dialog.setCancelable(false);

                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                                                record.setImageDrawable(getDrawable(R.drawable.record_active));

                                                txt1.setText(adapter.getItem(position));
                                                txt1.setTextColor(Color.parseColor("#0272B9"));
                                                selected_spc_name = txt1.getText().toString();

                                                txt2.setVisibility(View.VISIBLE);
                                                pro.setVisibility(View.VISIBLE);
                                                proImg.setVisibility(View.VISIBLE);

                                                //---------------get specialty id of selected specialty ------------
                                                for (int i = 0; i < size2; i++) {
                                                    if (selected_spc_name.equals(spc_name.get(i))) {
                                                        selected_spc_id = spc_array.get(i);
//                                                        Log.i("selected spc Id: ", selected_spc_id);
                                                    }
                                                }
                                                //------------------------------------------------------------------

                                                //-----------get provider name according to spc_id------------------
                                                txt2_array.clear();
                                                selected_pro_id_array.clear();
                                                for (int i = 0; i < size1; i++) {
                                                    if (selected_spc_id.equals(arrayList.get(i))) {
                                                        txt2_array.add(pro_name.get(i));
                                                        selected_pro_id_array.add(pro_id.get(i));
                                                    }
                                                }

                                                Log.i("selected pro names: ", String.valueOf(txt2_array));
                                                Log.i("selected_pro_id array:", String.valueOf(selected_pro_id_array));
                                                int count = adapter2.getCount();
                                                if (count == 1) {

                                                    record.setImageDrawable(getDrawable(R.drawable.record_active));

                                                    txt2.setText(adapter2.getItem(0));
                                                    selected_pro_name = adapter2.getItem(0);
                                                    selected_pro_id = selected_pro_id_array.get(0);
                                                    Log.i("selected_pro name: ", selected_pro_name);
                                                    Log.i("selected_pro ID: ", selected_pro_id);
                                                    txt2.setTextColor(Color.parseColor("#0272B9"));
                                                    start_record_btn.setVisibility(View.VISIBLE);
                                                    start_record_btn.setTextColor(Color.parseColor("#0272B9"));
                                                } else if (count > 1) {

                                                    record.setImageDrawable(getDrawable(R.drawable.record_inactive1));

                                                    //---------------------------------select_provider------------------------------------------
                                                    txt2.setText("");
                                                    dialog2 = new Dialog(RecordActivity.this);
                                                    dialog2.setContentView(R.layout.dialog_selected_provider);
                                                    dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                    dialog2.show();

                                                    ListView listView2 = dialog2.findViewById(R.id.selected_provider_list);
                                                    ArrayAdapter<String> adapter2 = new ArrayAdapter<>(RecordActivity.this, R.layout.custom_list_row, txt2_array);
                                                    listView2.setAdapter(adapter2);

                                                    int count2 = adapter2.getCount();
                                                    Log.i("count2 ", String.valueOf(count2));

                                                    listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                            record.setImageDrawable(getDrawable(R.drawable.record_active));

                                                            txt2.setText(adapter2.getItem(position));
                                                            selected_pro_name = adapter2.getItem(position);
                                                            selected_pro_id = selected_pro_id_array.get(position);
                                                            Log.i("selected_pro ID: ", selected_pro_id);
                                                            Log.i("selected_pro name: ", selected_pro_name);
                                                            txt2.setTextColor(Color.parseColor("#0272B9"));
                                                            start_record_btn.setVisibility(View.VISIBLE);
                                                            start_record_btn.setTextColor(Color.parseColor("#0272B9"));
                                                            dialog2.dismiss();
                                                        }
                                                    });
                                                    dialog2.setCancelable(false);
//
//                                                    start_record_btn.setVisibility(View.GONE);
                                                    selectProvider.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            record.setImageDrawable(getDrawable(R.drawable.record_inactive1));

                                                            dialog2 = new Dialog(RecordActivity.this);
                                                            dialog2.setContentView(R.layout.dialog_selected_provider);
                                                            dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                            dialog2.show();
                                                            dialog2.setCancelable(false);

                                                            ListView listView2 = dialog2.findViewById(R.id.selected_provider_list);
                                                            ArrayAdapter<String> adapter2 = new ArrayAdapter<>(RecordActivity.this, R.layout.custom_list_row, txt2_array);
                                                            listView2.setAdapter(adapter2);

                                                            int count2 = adapter2.getCount();
                                                            Log.i("count2 ", String.valueOf(count2));

                                                            listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                @Override
                                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                                    record.setImageDrawable(getDrawable(R.drawable.record_active));

                                                                    txt2.setText(adapter2.getItem(position));
                                                                    selected_pro_name = adapter2.getItem(position);
                                                                    selected_pro_id = selected_pro_id_array.get(position);
                                                                    Log.i("selected_pro ID: ", selected_pro_id);
                                                                    Log.i("selected_pro name: ", selected_pro_name);
                                                                    txt2.setTextColor(Color.parseColor("#0272B9"));
//                                                                    start_record_btn.setVisibility(View.VISIBLE);
                                                                    dialog2.dismiss();
                                                                }
                                                            });
                                                        }
                                                    });
                                                    //------------------------------------------------------------------------------------------

                                                }
                                                //------------------------------------------------------------------

                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    mQueue.add(request1);
                    //------------------------------------------------------------------------------------------------------------------

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request2);
        //------------------------------------------------------------------------------------------------------------------

        int ip = txt1.getText().length();
        int ep = txt2.getText().length();

//        if (ip == 0 && ep == 0) {
//            start_record_btn.setVisibility(View.GONE);
//        }else {
//            start_record_btn.setVisibility(View.VISIBLE);
//        }
        //----------------------------record button-------------------------------------------------

        start_record_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //----------------------------get patient details --------------------------------------------------------------
                String url1 = "http://65.2.3.41:8080/patient?id=" + patient_id;
                Log.i("URL: ", url1);

                JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response1) {
                        try {
                            JSONObject data = response1.getJSONObject("data");
                            type = data.getString("type");
                            Log.i("type", type);
                            recording_count = data.getInt("recording_count");
                            Log.i("recording_count", String.valueOf(recording_count));

                            String free = "free";

                            if (type.equals(free)) {
                                if (recording_count < 10) {

                                    String ip1 = txt1.getText().toString().trim();
                                    String ip2 = txt2.getText().toString().trim();

                                    if (!ip1.isEmpty() && !ip2.isEmpty()) {

                                        if (checkPermission()) {

                                            record.setImageDrawable(getDrawable(R.drawable.record_inactive1));

                                            recordingDialog = new Dialog(RecordActivity.this);
                                            recordingDialog.setContentView(R.layout.dialog_two_part_consent);
                                            recordingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            WindowManager.LayoutParams lp4 = new WindowManager.LayoutParams();
                                            lp4.copyFrom(recordingDialog.getWindow().getAttributes());
                                            lp4.width = WindowManager.LayoutParams.MATCH_PARENT;
                                            lp4.height = WindowManager.LayoutParams.MATCH_PARENT;
                                            recordingDialog.show();
                                            recordingDialog.getWindow().setAttributes(lp4);

                                            Button agreed = recordingDialog.findViewById(R.id.agreed);
                                            Button declined = recordingDialog.findViewById(R.id.declined);
                                            agreed.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    recordingDialog.dismiss();

                                                    record.setImageDrawable(getDrawable(R.drawable.record_inactive1));

                                                    AlertDialog.Builder myAlert = new AlertDialog.Builder(RecordActivity.this);
                                                    myAlert.setTitle("Recording Tip");
                                                    myAlert.setMessage(" position your phone between you and your provider for best recording quality.");
                                                    myAlert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog3 = new Dialog(RecordActivity.this);
                                                            dialog3.setContentView(R.layout.dialog_recording);
                                                            dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                                            WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
                                                            lp2.copyFrom(dialog3.getWindow().getAttributes());
                                                            lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
                                                            lp2.height = WindowManager.LayoutParams.MATCH_PARENT;

                                                            recordBtn = dialog3.findViewById(R.id.record_btn);
                                                            changeStatus = dialog3.findViewById(R.id.change);
                                                            chronometer = dialog3.findViewById(R.id.chronometer);
//                                                            wave = findViewById(R.id.waveImg);
//                                                            animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
//                                                            wave.startAnimation(animation);
                                                            dialog3.show();
                                                            dialog3.getWindow().setAttributes(lp2);
                                                            startRecording();
                                                            startTimer();

                                                            Button doneOnRec = dialog3.findViewById(R.id.doneOnRec);
                                                            doneOnRec.setOnClickListener(new View.OnClickListener() {
                                                                @RequiresApi(api = Build.VERSION_CODES.N)
                                                                @Override
                                                                public void onClick(View v) {
                                                                    pauseRecording();
                                                                    stopTimer();

                                                                    String last = chronometer.getText().toString();
                                                                    Log.i("Last Time", last);

                                                                    String[] due = last.split(":", 8);

                                                                    for (String lastDue : due) {
                                                                        lastDuration.add(lastDue);
                                                                    }
                                                                    String min = (String) lastDuration.get(0);
                                                                    String sec = (String) lastDuration.get(1);

                                                                    Log.i("Last Duration in min", min);
                                                                    Log.i("Last Duration in sec", sec);
                                                                    Log.i("Last Duration all", String.valueOf(lastDuration));

                                                                    int min1 = Integer.parseInt(min);
                                                                    Log.i("hr1", String.valueOf(min1));

                                                                    int sec1 = Integer.parseInt(sec);
                                                                    Log.i("hr1", String.valueOf(sec1));

                                                                    duration = (min1 * 60) + sec1;

                                                                    Log.i("Total Seconds", String.valueOf(duration));

                                                                    savingDialog = new Dialog(RecordActivity.this);
                                                                    savingDialog.setContentView(R.layout.dialog_saving);
                                                                    WindowManager.LayoutParams lp8 = new WindowManager.LayoutParams();
                                                                    lp8.copyFrom(savingDialog.getWindow().getAttributes());
                                                                    lp8.width = WindowManager.LayoutParams.MATCH_PARENT;
                                                                    lp8.height = WindowManager.LayoutParams.MATCH_PARENT;
                                                                    savingDialog.show();
                                                                    savingDialog.getWindow().setAttributes(lp8);
                                                                    savingDialog.setCancelable(false);

                                                                    select = savingDialog.findViewById(R.id.general);

                                                                    category_list.add("General");
                                                                    category_list.add("Diagnosis");
                                                                    category_list.add("Medication");
                                                                    category_list.add("Tests");

                                                                    ListView generalList = savingDialog.findViewById(R.id.general_list);
                                                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(RecordActivity.this, R.layout.custom_list_row, category_list);

                                                                    select.setText(adapter.getItem(0));
                                                                    catagory_name = adapter.getItem(0);
                                                                    generalList.setAdapter(adapter);

                                                                    generalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                        @Override
                                                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                                            select.setText(adapter.getItem(i));
                                                                            selected_category_name = adapter.getItem(i);
                                                                            Log.i("selected category: ", selected_category_name);
                                                                        }
                                                                    });

                                                                    Button save_rec = savingDialog.findViewById(R.id.save_dialog);
                                                                    save_rec.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {

                                                                            if (selected_category_name == null) {
                                                                                selected_category_name = "general";
                                                                            } else {
                                                                                Log.i("selected category save", selected_category_name);
                                                                                savingDialog.setCancelable(false);
                                                                                mediaRecorder.stop();
                                                                                resetTimer();
                                                                                mediaRecorder = null;

                                                                                recordFile = patient_name + "-" + selected_spc_name + "-" + selected_pro_name + "-" + "null" + ".mp3";
                                                                                String recordFileName = patient_name + "-" + selected_spc_name + "-" + selected_pro_name + "-" + selected_category_name + ".mp3";

                                                                                File directory = new File(RecordActivity.this.getExternalFilesDir("/").getAbsolutePath());
                                                                                File from = new File(directory, String.valueOf(recordFile));
                                                                                File to = new File(directory, recordFileName);
                                                                                from.renameTo(to);
                                                                                Log.i("Directory is", directory.toString());
                                                                                Log.i("From path is", from.toString());
                                                                                Log.i("To path is", to.toString());

                                                                                saveRecording(createRecording());

                                                                                AlertDialog.Builder myAlert = new AlertDialog.Builder(RecordActivity.this);
                                                                                myAlert.setTitle("Saved Successfully");
                                                                                myAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                        savingDialog.dismiss();
//                                                            to.delete();
                                                                                        dialog3.dismiss();
                                                                                        Intent i1 = new Intent(RecordActivity.this, SearchActivity.class);
                                                                                        i1.putExtra("id3", patient_id);
                                                                                        i1.putExtra("name", patient_name);
                                                                                        startActivity(i1);
                                                                                        overridePendingTransition(0, 0);
                                                                                        finish();
                                                                                    }
                                                                                });
                                                                                myAlert.setCancelable(false);
                                                                                myAlert.show();
                                                                            }
                                                                        }

                                                                    });
                                                                }
                                                            });

                                                            dialog3.setCancelable(false);
                                                        }
                                                    });
                                                    myAlert.show();
                                                    myAlert.setCancelable(false);
                                                }
                                            });
                                            declined.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    record.setImageDrawable(getDrawable(R.drawable.record_active));
                                                    recordingDialog.dismiss();
                                                }
                                            });
                                        } else {
                                            ActivityCompat.requestPermissions(RecordActivity.this, new String[]{recordPermission}, PERMISSION_CODE);
                                        }

                                    }
                                } else if (recording_count == 10) {

                                    AlertDialog.Builder myAlert = new AlertDialog.Builder(RecordActivity.this);
                                    myAlert.setTitle("This complimentary version of MyDrsOrders allows 10 recordings");
                                    myAlert.setPositiveButton("Enter PSI Code", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent psi = new Intent(RecordActivity.this, PSIPinActivity.class);
                                            psi.putExtra("id", patient_id);
                                            psi.putExtra("name", patient_name);
                                            startActivity(psi);
                                            finish();
                                        }
                                    });
                                    myAlert.setNegativeButton("Buy Subscription", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent(RecordActivity.this, SubscribeActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                    });
                                    myAlert.show();
                                }
                            } else {

                                String ip1 = txt1.getText().toString().trim();
                                String ip2 = txt2.getText().toString().trim();

                                if (!ip1.isEmpty() && !ip2.isEmpty()) {

                                    if (checkPermission()) {
                                        record.setImageDrawable(getDrawable(R.drawable.record_inactive1));
                                        recordingDialog = new Dialog(RecordActivity.this);
                                        recordingDialog.setContentView(R.layout.dialog_two_part_consent);
                                        recordingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        WindowManager.LayoutParams lp4 = new WindowManager.LayoutParams();
                                        lp4.copyFrom(recordingDialog.getWindow().getAttributes());
                                        lp4.width = WindowManager.LayoutParams.MATCH_PARENT;
                                        lp4.height = WindowManager.LayoutParams.MATCH_PARENT;
                                        recordingDialog.show();
                                        recordingDialog.getWindow().setAttributes(lp4);

                                        Button agreed = recordingDialog.findViewById(R.id.agreed);
                                        Button declined = recordingDialog.findViewById(R.id.declined);
                                        agreed.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                recordingDialog.dismiss();
                                                record.setImageDrawable(getDrawable(R.drawable.record_inactive1));
                                                AlertDialog.Builder myAlert = new AlertDialog.Builder(RecordActivity.this);
                                                myAlert.setTitle("Recording Tip");
                                                myAlert.setMessage(" position your phone between you and your provider for best recording quality.");
                                                myAlert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog3 = new Dialog(RecordActivity.this);
                                                        dialog3.setContentView(R.layout.dialog_recording);
                                                        dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                                        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
                                                        lp2.copyFrom(dialog3.getWindow().getAttributes());
                                                        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
                                                        lp2.height = WindowManager.LayoutParams.MATCH_PARENT;

                                                        recordBtn = dialog3.findViewById(R.id.record_btn);
                                                        changeStatus = dialog3.findViewById(R.id.change);
                                                        chronometer = dialog3.findViewById(R.id.chronometer);
                                                        dialog3.show();
                                                        dialog3.getWindow().setAttributes(lp2);
                                                        startRecording();
                                                        startTimer();

                                                        Button doneOnRec = dialog3.findViewById(R.id.doneOnRec);
                                                        doneOnRec.setOnClickListener(new View.OnClickListener() {
                                                            @RequiresApi(api = Build.VERSION_CODES.N)
                                                            @Override
                                                            public void onClick(View v) {
                                                                pauseRecording();
                                                                stopTimer();

                                                                String last = chronometer.getText().toString();
                                                                Log.i("Last Time", last);

                                                                String[] due = last.split(":", 8);

                                                                for (String lastDue : due) {
                                                                    lastDuration.add(lastDue);
                                                                }
                                                                String min = (String) lastDuration.get(0);
                                                                String sec = (String) lastDuration.get(1);

                                                                Log.i("Last Duration in min", min);
                                                                Log.i("Last Duration in sec", sec);
                                                                Log.i("Last Duration all", String.valueOf(lastDuration));

                                                                int min1 = Integer.parseInt(min);
                                                                Log.i("hr1", String.valueOf(min1));

                                                                int sec1 = Integer.parseInt(sec);
                                                                Log.i("hr1", String.valueOf(sec1));

                                                                duration = (min1 * 60) + sec1;

                                                                Log.i("Total Seconds", String.valueOf(duration));

                                                                savingDialog = new Dialog(RecordActivity.this);
                                                                savingDialog.setContentView(R.layout.dialog_saving);
                                                                WindowManager.LayoutParams lp8 = new WindowManager.LayoutParams();
                                                                lp8.copyFrom(savingDialog.getWindow().getAttributes());
                                                                lp8.width = WindowManager.LayoutParams.MATCH_PARENT;
                                                                lp8.height = WindowManager.LayoutParams.MATCH_PARENT;
                                                                savingDialog.show();
                                                                savingDialog.getWindow().setAttributes(lp8);
                                                                savingDialog.setCancelable(false);

                                                                select = savingDialog.findViewById(R.id.general);

                                                                category_list.add("General");
                                                                category_list.add("Diagnosis");
                                                                category_list.add("Medication");
                                                                category_list.add("Tests");

                                                                ListView generalList = savingDialog.findViewById(R.id.general_list);
                                                                ArrayAdapter<String> adapter = new ArrayAdapter<>(RecordActivity.this, R.layout.custom_list_row, category_list);

                                                                select.setText(adapter.getItem(0));
                                                                catagory_name = adapter.getItem(0);
                                                                generalList.setAdapter(adapter);

                                                                generalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                    @Override
                                                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                                        select.setText(adapter.getItem(i));
                                                                        selected_category_name = adapter.getItem(i);
                                                                        Log.i("selected category: ", selected_category_name);
                                                                    }
                                                                });

                                                                Button save_rec = savingDialog.findViewById(R.id.save_dialog);
                                                                save_rec.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {

                                                                        if (selected_category_name == null) {
                                                                            selected_category_name = "general";
                                                                        } else {
                                                                            Log.i("selected category save", selected_category_name);
                                                                            savingDialog.setCancelable(false);
                                                                            mediaRecorder.stop();
                                                                            resetTimer();
                                                                            mediaRecorder = null;

                                                                            recordFile = patient_name + "-" + selected_spc_name + "-" + selected_pro_name + "-" + "null" + ".mp3";
                                                                            String recordFileName = patient_name + "-" + selected_spc_name + "-" + selected_pro_name + "-" + selected_category_name + ".mp3";

                                                                            File directory = new File(RecordActivity.this.getExternalFilesDir("/").getAbsolutePath());
                                                                            File from = new File(directory, String.valueOf(recordFile));
                                                                            File to = new File(directory, recordFileName);
                                                                            from.renameTo(to);
                                                                            Log.i("Directory is", directory.toString());
                                                                            Log.i("From path is", from.toString());
                                                                            Log.i("To path is", to.toString());

                                                                            saveRecording(createRecording());

                                                                            AlertDialog.Builder myAlert = new AlertDialog.Builder(RecordActivity.this);
                                                                            myAlert.setTitle("Saved Successfully");
                                                                            myAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    savingDialog.dismiss();
//                                                            to.delete();
                                                                                    dialog3.dismiss();
                                                                                    Intent i1 = new Intent(RecordActivity.this, SearchActivity.class);
                                                                                    i1.putExtra("id3", patient_id);
                                                                                    i1.putExtra("name", patient_name);
                                                                                    startActivity(i1);
                                                                                    overridePendingTransition(0, 0);
                                                                                    finish();
                                                                                }
                                                                            });
                                                                            myAlert.setCancelable(false);
                                                                            myAlert.show();
                                                                        }
                                                                    }

                                                                });
                                                            }
                                                        });

                                                        dialog3.setCancelable(false);
                                                    }
                                                });
                                                myAlert.show();
                                                myAlert.setCancelable(false);
                                            }
                                        });
                                        declined.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                record.setImageDrawable(getDrawable(R.drawable.record_active));
                                                recordingDialog.dismiss();
                                            }
                                        });
                                    } else {
                                        ActivityCompat.requestPermissions(RecordActivity.this, new String[]{recordPermission}, PERMISSION_CODE);
                                    }

                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                mQueue.add(request1);
                //------------------------------------------------------------------------------------------------------------------
            }
        });
        //------------------------------------------------------------------------------------------

        menu_btn = findViewById(R.id.back_button1);
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(RecordActivity.this);
                dialog.setContentView(R.layout.dialog_menu);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Window window = dialog.getWindow();
                WindowManager.LayoutParams top = window.getAttributes();
                top.gravity = Gravity.TOP;
                window.setAttributes(top);
                dialog.show();

                TextView about = dialog.findViewById(R.id.about);
                TextView psi = dialog.findViewById(R.id.PSI);
                TextView vision = dialog.findViewById(R.id.vision);
                TextView donate = dialog.findViewById(R.id.donate);


                about.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //-------------------------------------- Open Dialog for About -------------------------------------
//                Toast.makeText(this, "You Clicked on About", Toast.LENGTH_SHORT).show();
                        dialogAbout = new Dialog(RecordActivity.this);
                        dialogAbout.setContentView(R.layout.dialog_about);
//                backDialog.getWindow().setLayout();
//                backDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.myowngradient));
                        dialogAbout.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialogAbout.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                        dialogAbout.show();
                        dialogAbout.getWindow().setAttributes(lp);

                        titleButton = dialogAbout.findViewById(R.id.myTitle);
                        titleButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogAbout.dismiss();
                            }
                        });
                        dialogAbout.setCancelable(false);
                    }
                });

                psi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//----------------------------------------- Go to the PSI Site -------------------------------------
                        Intent i1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.patientservicesinc.org/"));
                        startActivity(i1);
                    }
                });

                vision.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//--------------------------------- Open Dialog for Vision and Mission -----------------------------
//                Toast.makeText(this, "You Clicked on Vision and Mission", Toast.LENGTH_SHORT).show();
                        dialogVision = new Dialog(RecordActivity.this);
                        dialogVision.setContentView(R.layout.dialog_vision);
//                backDialog.getWindow().setLayout();
//                backDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.myowngradient));
                        dialogVision.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        WindowManager.LayoutParams lp1 = new WindowManager.LayoutParams();
                        lp1.copyFrom(dialogVision.getWindow().getAttributes());
                        lp1.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp1.height = WindowManager.LayoutParams.MATCH_PARENT;
                        dialogVision.show();
                        dialogVision.getWindow().setAttributes(lp1);

                        titleButtonVision = dialogVision.findViewById(R.id.myTitle1);
                        titleButtonVision.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                        backDialog.setCancelable(false);
                                dialogVision.dismiss();
                            }
                        });

                        dialogVision.setCancelable(false);
                    }
                });

                donate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//-------------------------------------- Go to the Donate Site -------------------------------------
                        Intent i2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.patientservicesinc.org/get-involved/donate/"));
                        startActivity(i2);
                    }
                });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void PausePlay(View view) {

        if (isRecording) {
//            wave.clearAnimation();
            resumeRecording();
            startTimer();
            recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.pause_btn, null));
            changeStatus.setText("Pause Recording");
            isRecording = false;
        } else {
//            wave.startAnimation(animation);
            pauseRecording();
            stopTimer();
            recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.resume_btn, null));
            changeStatus.setText("Resume Recording");
            isRecording = true;
        }

    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {

        Log.i("file uri: ", String.valueOf(fileUri));
        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(this, fileUri);

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("mp3"), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void startRecording() {

        Log.i("selected spc_name: ", selected_spc_name);

        String recordPath = this.getExternalFilesDir("/").getAbsolutePath();
        //---------------------------------file to uri----------------------------------------------

//        String recordPath = getExternalFilesDir("/").getAbsolutePath();
        Log.i("record_path: ", recordPath);
        //------------------------------------------------------------------------------------------

        recordFile = patient_name + "-" + selected_spc_name + "-" + selected_pro_name + "-" + selected_category_name + ".mp3";
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void pauseRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            wave.clearAnimation();
            mediaRecorder.pause();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void resumeRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            wave.startAnimation(animation);
            mediaRecorder.resume();
        }
    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, recordPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (ActivityCompat.checkSelfPermission(this, recordPermission) == PackageManager.PERMISSION_DENIED) {
            return false;
        }
        return checkPermission();
    }

    private void startTimer() {
        if (!isResume) {
            tStart = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
            chronometer.start();
            isResume = true;
        }
    }

    private void stopTimer() {
        if (isResume) {
            tBuff += tMilliSec;
            handler.removeCallbacks(runnable);
            chronometer.stop();
            isResume = false;
        }
    }

    private void resetTimer() {
        if (isResume) {
            tBuff += tMilliSec;
            handler.removeCallbacks(runnable);
            chronometer.stop();
            isResume = false;
        }
        tMilliSec = 0L;
        tStart = 0L;
        tBuff = 0L;
        tUpdate = 0L;
        sec = 0;
        min = 0;
        milliSec = 0;
        chronometer.setText("00:00:00");

    }

    //--------------------------------Record request post ---------------------------------------
    public RecordRequest createRecording() {
        RecordRequest recordRequest = new RecordRequest();
        recordRequest.setPatient_id(patient_id);
        Log.i("patient_id on create: ", patient_id);
        recordRequest.setProvider_id(selected_pro_id);
        Log.i("pro_id on create: ", selected_pro_id);
        recordFile2 = patient_name + "-" + selected_spc_name + "-" + selected_pro_name + "-" + selected_category_name + ".mp3";
        recordRequest.setName(recordFile2);
        Log.i("rec file on create: ", recordFile2);
        recordRequest.setDuration_sec(duration);
        Log.i("duration on create: ", String.valueOf(duration));
        recordRequest.setCategory(selected_category_name.toLowerCase());
        File file = new File(this.getExternalFilesDir("/").getAbsolutePath(), recordFile2);
        Log.i("file saved on create: ", String.valueOf(file));
        return recordRequest;
    }
    //----------------------------------------------------------------------------------------------

    //-----------------------------------save Recording request-------------------------------------
    public void saveRecording(RecordRequest recordRequest) {

        Call<RecordResponse> recordResponseCall = ApiClient.getRecordService().saveRecording(recordRequest);
        recordResponseCall.enqueue(new Callback<RecordResponse>() {
            @Override
            public void onResponse(Call<RecordResponse> call, Response<RecordResponse> response) {
                RecordResponse recordResponse = response.body();
                String id = recordResponse.getData().getId();
                String msg = recordResponse.getMessage();
                Log.i("msg:", msg);
                Log.i("Created rec id: ", id);
                uploadRecording1(id);
            }

            @Override
            public void onFailure(Call<RecordResponse> call, Throwable t) {

            }
        });
    }
    //----------------------------------------------------------------------------------------------

    private void uploadRecording1(String id) {

        recordFile3 = patient_name + "-" + selected_spc_name + "-" + selected_pro_name + "-" + selected_category_name + ".mp3";
        Log.i("recordFile3: ", recordFile3);
        File file = new File(getExternalFilesDir("/").getAbsolutePath(), recordFile3);

        MultipartBody.Part body = prepareFilePart("audio", Uri.fromFile(file));

        Call<UploadResponse> call = ApiClient.getUploadService().uploadRecording(patient_id, id, body);
        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                UploadResponse response1 = response.body();
                String data = response1.getData();
                Log.i("Rec uploaded? : ", data);
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
            }
        });
    }

    public void onAdd(View view) {
        Intent i1 = new Intent(RecordActivity.this, NewProviderActivity.class);
        i1.putExtra("id1", patient_id);
        i1.putExtra("name", patient_name);
        i1.putExtra("bottom_nav", true);
        startActivity(i1);
        overridePendingTransition(0, 0);
    }

    public void onSearch(View view) {
        Intent i2 = new Intent(RecordActivity.this, SearchActivity.class);
        i2.putExtra("id3", patient_id);
        i2.putExtra("name", patient_name);
        startActivity(i2);
        overridePendingTransition(0, 0);
    }
}