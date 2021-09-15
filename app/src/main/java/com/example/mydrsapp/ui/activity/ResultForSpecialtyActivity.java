package com.example.mydrsapp.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mydrsapp.R;
import com.example.mydrsapp.model.DeleteResponse;
import com.example.mydrsapp.model.RecordingModel;
import com.example.mydrsapp.ui.adapter.searchbyspecialist.SearchBySpecialtyDiagnosisAdapter;
import com.example.mydrsapp.ui.adapter.searchbyspecialist.SearchBySpecialtyGeneralAdapter;
import com.example.mydrsapp.ui.adapter.searchbyspecialist.SearchBySpecialtyMedicationsAdapter;
import com.example.mydrsapp.ui.adapter.searchbyspecialist.SearchBySpecialtyTestsAdapter;
import com.example.mydrsapp.utils.ApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;

public class ResultForSpecialtyActivity extends AppCompatActivity {

//--------------------------------- For Offline Recordings -----------------------------------------

    Button deleteBtn, shareBtn;
    ArrayList nameSplit, catSplit;
    MediaPlayer mediaPlayer = null;
    boolean isPlaying = false;

    Dialog playerDialog;

    TextView playerPosition, playerDuration, playerDuration2, playerTitle, playerDate, noRecordings;
    SeekBar seekBar;
    ImageButton btRew, btPlay, btFar, btBeginning, btEnd, btBack;

    Handler handler1;
    Runnable runnable1;

    ArrayList<RecordingModel> newListGeneral, newListDiagnosis, newListMedications, newListTests;

    ArrayList<String> arrayList, arrayList2;
    private RequestQueue mQueue;
    private ArrayList<RecordingModel> recordingList = new ArrayList<>();

    Date todayDate, fileDate, currentDate;
    long fDate, tDate, diffTF;
//--------------------------------------------------------------------------------------------------

//--------------------------------- For Online Recordings ------------------------------------------
    public ConstraintLayout generalConstrain, diagnosisConstrain, medicationConstrain, testsConstrain;
    ImageView downBtnG, downBtnD, downBtnM, downBtnT;
//    private boolean isVisible = false;
    private RecyclerView audioListG, audioListD, audioListM, audioListT;
//    private File[] allFiles;
//    private TimeAgo timeAgo;
    public ScrollView resG, resD, resM, resT;
    private boolean isGenExpanded = false;

    private SearchBySpecialtyGeneralAdapter generalAdapter;
    private SearchBySpecialtyDiagnosisAdapter diagnosisAdapter;
    private SearchBySpecialtyMedicationsAdapter medicationsAdapter;
    private SearchBySpecialtyTestsAdapter testsAdapter;
    String specialtyName, provider, patientId, patientName;
//--------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_for_specialty);

//        String path = this.getExternalFilesDir("/").getAbsolutePath();
//        File directory = new File(path);
//        allFiles = directory.listFiles();
//        timeAgo = new TimeAgo();

        noRecordings = findViewById(R.id.no_recordings_specialty);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            specialtyName = bundle.getString("specialty");
            Log.i("Patient Spec Name LS3R", specialtyName);
            provider = bundle.getString("provider");
            Log.i("Patient Prov Name LS3R", provider);
            patientId = bundle.getString("id11");
            Log.i("Patient ID SBS", patientId);
            patientName = bundle.getString("name10");
            Log.i("Patient Name SBS", patientName);
        }
        Log.i("Outside LS3R Spec Name:", specialtyName);
        Log.i("Outside LS3R Prov Name:", provider);
        Log.i("Outside SBS ID", patientId);
        Log.i("Outside SBS Name", patientName);

        generalConstrain = findViewById(R.id.general_constrain_RFS);
        downBtnG = findViewById(R.id.down_arrow_RFS);
        audioListG = findViewById(R.id.audio_general_RFS);
        resG = findViewById(R.id.visible_or_invisible_general_RFS);
        resG.setVisibility(View.VISIBLE);
        downBtnG.setBackgroundResource(R.drawable.ic_down);

        diagnosisConstrain = findViewById(R.id.diagnosis_constrain_RFS);
        downBtnD = findViewById(R.id.down_arrow1_RFS);
        audioListD = findViewById(R.id.audio_diagnosis_RFS);
        resD = findViewById(R.id.visible_or_invisible1_diagnosis_RFS);
        resD.setVisibility(View.VISIBLE);
        downBtnD.setBackgroundResource(R.drawable.ic_down);

        medicationConstrain = findViewById(R.id.medication_constrain_RFS);
        downBtnM = findViewById(R.id.down_arrow2_medication_RFS);
        audioListM = findViewById(R.id.audio_medications_RFS);
        resM = findViewById(R.id.visible_or_invisible2_medications_RFS);
        resM.setVisibility(View.VISIBLE);
        downBtnM.setBackgroundResource(R.drawable.ic_down);

        testsConstrain = findViewById(R.id.tests_constrain_RFS);
        downBtnT = findViewById(R.id.down_arrow3_tests_RFS);
        audioListT = findViewById(R.id.audio_tests_RFS);
        resT = findViewById(R.id.visible_or_invisible3_tests_RFS);
        resT.setVisibility(View.VISIBLE);
        downBtnT.setBackgroundResource(R.drawable.ic_down);
/*
//--------------------------------- For Online Recordings ------------------------------------------
        generalAdapter = new SearchBySpecialtyGeneralAdapter(allFiles, specialtyName, provider, id, name, this);
        LinearLayoutManager linearLayoutManagerG = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerG.setReverseLayout(true);
        linearLayoutManagerG.setStackFromEnd(true);
        audioListG.setLayoutManager(linearLayoutManagerG);
        audioListG.setHasFixedSize(true);
//        audioListG.setLayoutManager(new LinearLayoutManager(this));
        audioListG.setAdapter(generalAdapter);

        diagnosisAdapter = new SearchBySpecialtyDiagnosisAdapter(allFiles, specialtyName, provider, id, name, this);
        LinearLayoutManager linearLayoutManagerD = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerD.setReverseLayout(true);
        linearLayoutManagerD.setStackFromEnd(true);
        audioListD.setLayoutManager(linearLayoutManagerD);
        audioListD.setHasFixedSize(true);
//        audioListD.setLayoutManager(new LinearLayoutManager(this));
        audioListD.setAdapter(diagnosisAdapter);

        medicationsAdapter = new SearchBySpecialtyMedicationsAdapter(allFiles, specialtyName, provider, id, name, this);
        LinearLayoutManager linearLayoutManagerM = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerM.setReverseLayout(true);
        linearLayoutManagerM.setStackFromEnd(true);
        audioListM.setLayoutManager(linearLayoutManagerM);
        audioListM.setHasFixedSize(true);
//        audioListM.setLayoutManager(new LinearLayoutManager(this));
        audioListM.setAdapter(medicationsAdapter);

        testsAdapter = new SearchBySpecialtyTestsAdapter(allFiles, specialtyName, provider, id, name, this);
        LinearLayoutManager linearLayoutManagerT = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerT.setReverseLayout(true);
        linearLayoutManagerT.setStackFromEnd(true);
        audioListT.setLayoutManager(linearLayoutManagerT);
        audioListT.setHasFixedSize(true);
//        audioListT.setLayoutManager(new LinearLayoutManager(this));
        audioListT.setAdapter(testsAdapter);
//--------------------------------------------------------------------------------------------------
 */

//        Log.i("String of Adapter", String.valueOf(audioList.getAdapter().getItemCount()));
//        if (audioList.getAdapter().getItemCount() == 0) {
//            AlertDialog.Builder myAlert = new AlertDialog.Builder(ResultForSpecialty.this);
//            myAlert.setTitle("No Recordings Found");
//            myAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    Intent i1 = new Intent(ResultForSpecialty.this, Share.class);
//                    i1.putExtra("id6", id);
//                    i1.putExtra("name5", name);
//                    startActivity(i1);
//                    overridePendingTransition(0, 0);
//                    finish();
//                }
//            });
//            myAlert.setCancelable(false);
//            myAlert.show();
//        }


//--------------------------------- For Online Recordings ------------------------------------------

        arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        nameSplit = new ArrayList<>();
        catSplit = new ArrayList<>();
        mQueue = Volley.newRequestQueue(ResultForSpecialtyActivity.this);

        LinearLayoutManager linearLayoutManagerG = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerG.setReverseLayout(true);
        audioListG.setHasFixedSize(true);
        audioListG.setLayoutManager(linearLayoutManagerG);
        generalAdapter = new SearchBySpecialtyGeneralAdapter(this, recordingList);
        audioListG.setAdapter(generalAdapter);

        LinearLayoutManager linearLayoutManagerD = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerD.setReverseLayout(true);
        audioListD.setHasFixedSize(true);
        audioListD.setLayoutManager(linearLayoutManagerD);
        diagnosisAdapter = new SearchBySpecialtyDiagnosisAdapter(this, recordingList);
        audioListD.setAdapter(diagnosisAdapter);

        LinearLayoutManager linearLayoutManagerM = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerM.setReverseLayout(true);
        audioListM.setHasFixedSize(true);
        audioListM.setLayoutManager(linearLayoutManagerM);
        medicationsAdapter = new SearchBySpecialtyMedicationsAdapter(this, recordingList);
        audioListM.setAdapter(medicationsAdapter);

        LinearLayoutManager linearLayoutManagerT = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerT.setReverseLayout(true);
        audioListT.setHasFixedSize(true);
        audioListT.setLayoutManager(linearLayoutManagerT);
        testsAdapter = new SearchBySpecialtyTestsAdapter(this, recordingList);
        audioListT.setAdapter(testsAdapter);

//----------------------------get all rec file names------------------------------------------------
        String url1 = "http://65.2.3.41:8080/recording?patient_id=" + patientId;
        Log.i("URL: ", url1);

        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response1) {
                try {
                    recordingList = new ArrayList<>();
                    JSONObject dataObj = response1.getJSONObject("data");
                    JSONArray recordList = dataObj.getJSONArray("recordings");
                    for (int i = 0; i < recordList.length(); i++) {
                        JSONObject obj = recordList.getJSONObject(i);
                        RecordingModel model = new RecordingModel();
                        model.setId(obj.getString("id"));
                        model.setPatient_id(obj.getString("patient_id"));
                        model.setProvider_id(obj.getString("provider_id"));
                        model.setDuration_sec(obj.getInt("duration_sec"));
                        model.setCategory(obj.getString("category"));
                        model.setName(obj.getString("name"));
                        model.setCreated(obj.getString("created"));
                        recordingList.add(model);
                    }

                    newListGeneral = new ArrayList<>();
                    for (int i=0; i<recordingList.size(); i++){
                        String category = recordingList.get(i).getCategory();
                        if (category.equals("general")){
                            newListGeneral.add(recordingList.get(i));
                        }
                    }
                    generalAdapter.updateDataGeneral(newListGeneral);

                    newListDiagnosis = new ArrayList<>();
                    for (int i=0; i<recordingList.size(); i++){
                        String category = recordingList.get(i).getCategory();
                        if (category.equals("diagnosis")){
                            newListDiagnosis.add(recordingList.get(i));
                        }
                    }
                    diagnosisAdapter.updateDataDiagnosis(newListDiagnosis);

                    newListMedications = new ArrayList<>();
                    for (int i=0; i<recordingList.size(); i++){
                        String category = recordingList.get(i).getCategory();
                        if (category.equals("medication")){
                            newListMedications.add(recordingList.get(i));
                        }
                    }
                    medicationsAdapter.updateDataMedications(newListMedications);

                    newListTests = new ArrayList<>();
                    for (int i=0; i<recordingList.size(); i++){
                        String category = recordingList.get(i).getCategory();
                        if (category.equals("tests")){
                            newListTests.add(recordingList.get(i));
                        }
                    }
                    testsAdapter.updateDataTests(newListTests);

                    if (generalAdapter.getItemCount()==0){
                        generalConstrain.setVisibility(View.GONE);
                    }
                    if (diagnosisAdapter.getItemCount()==0){
                        diagnosisConstrain.setVisibility(View.GONE);
                    }
                    if (medicationsAdapter.getItemCount()==0){
                        medicationConstrain.setVisibility(View.GONE);
                    }
                    if (testsAdapter.getItemCount()==0){
                        testsConstrain.setVisibility(View.GONE);
                    }
                    if (generalAdapter.getItemCount()==0 && diagnosisAdapter.getItemCount()==0 && medicationsAdapter.getItemCount()==0 && testsAdapter.getItemCount()==0){
                        noRecordings.setVisibility(View.VISIBLE);
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
//--------------------------------------------------------------------------------------------------
    }

    public void back_spec(View view) {
        Intent i1 = new Intent(ResultForSpecialtyActivity.this, SearchBySpecialistActivity.class);
        i1.putExtra("id2", patientId);
        i1.putExtra("name", patientName);
        startActivity(i1);
        overridePendingTransition(0, 0);
        finish();
    }

    public void categoryRFS(View view) {
        switch (view.getId()) {
            case R.id.general_constrain_RFS:
                if (isGenExpanded) {
                    resG.setVisibility(View.GONE);
                    downBtnG.setBackgroundResource(R.drawable.ic_down);
//                    downArrow1.setBackgroundResource(R.drawable.ic_up);
//                    downArrow2.setBackgroundResource(R.drawable.ic_up);
//                    downArrow3.setBackgroundResource(R.drawable.ic_up);
                    isGenExpanded = false;
                } else {
                    resG.setVisibility(View.VISIBLE);
                    resD.setVisibility(View.GONE);
                    resM.setVisibility(View.GONE);
                    resT.setVisibility(View.GONE);
                    downBtnG.setBackgroundResource(R.drawable.ic_up);
                    downBtnD.setBackgroundResource(R.drawable.ic_down);
                    downBtnM.setBackgroundResource(R.drawable.ic_down);
                    downBtnT.setBackgroundResource(R.drawable.ic_down);
                    isGenExpanded = true;
                }
                break;

            case R.id.diagnosis_constrain_RFS:
                if (isGenExpanded) {
                    resD.setVisibility(View.GONE);
                    downBtnD.setBackgroundResource(R.drawable.ic_down);
//                    downArrow.setBackgroundResource(R.drawable.ic_up);
//                    downArrow2.setBackgroundResource(R.drawable.ic_up);
//                    downArrow3.setBackgroundResource(R.drawable.ic_up);
                    isGenExpanded = false;
                } else {

                    resD.setVisibility(View.VISIBLE);
                    resG.setVisibility(View.GONE);
                    resM.setVisibility(View.GONE);
                    resT.setVisibility(View.GONE);
                    downBtnD.setBackgroundResource(R.drawable.ic_up);
                    downBtnG.setBackgroundResource(R.drawable.ic_down);
                    downBtnM.setBackgroundResource(R.drawable.ic_down);
                    downBtnT.setBackgroundResource(R.drawable.ic_down);
                    isGenExpanded = true;
                }
                break;

            case R.id.medication_constrain_RFS:
                if (isGenExpanded) {
                    resM.setVisibility(View.GONE);
                    downBtnM.setBackgroundResource(R.drawable.ic_down);
//                    downArrow1.setBackgroundResource(R.drawable.ic_up);
//                    downArrow.setBackgroundResource(R.drawable.ic_up);
//                    downArrow3.setBackgroundResource(R.drawable.ic_up);
                    isGenExpanded = false;
                } else {
                    resM.setVisibility(View.VISIBLE);
                    resG.setVisibility(View.GONE);
                    resD.setVisibility(View.GONE);
                    resT.setVisibility(View.GONE);
                    downBtnM.setBackgroundResource(R.drawable.ic_up);
                    downBtnG.setBackgroundResource(R.drawable.ic_down);
                    downBtnD.setBackgroundResource(R.drawable.ic_down);
                    downBtnT.setBackgroundResource(R.drawable.ic_down);
                    isGenExpanded = true;
                }
                break;

            case R.id.tests_constrain_RFS:
                if (isGenExpanded) {
                    resT.setVisibility(View.GONE);
                    downBtnT.setBackgroundResource(R.drawable.ic_down);
//                    downArrow1.setBackgroundResource(R.drawable.ic_up);
//                    downArrow2.setBackgroundResource(R.drawable.ic_up);
//                    downArrow.setBackgroundResource(R.drawable.ic_up);
                    isGenExpanded = false;
                } else {
                    resT.setVisibility(View.VISIBLE);
                    resG.setVisibility(View.GONE);
                    resD.setVisibility(View.GONE);
                    resM.setVisibility(View.GONE);
                    downBtnT.setBackgroundResource(R.drawable.ic_up);
                    downBtnG.setBackgroundResource(R.drawable.ic_down);
                    downBtnD.setBackgroundResource(R.drawable.ic_down);
                    downBtnM.setBackgroundResource(R.drawable.ic_down);
                    isGenExpanded = true;
                }
                break;
        }
    }

    //--------------------------------- For Online Recordings ------------------------------------------

    public void onClickListener(RecordingModel obj) {
        String wavUrl = "http://65.2.3.41:8080/public/recordings/" + obj.getPatient_id() + "/" + obj.getId() + ".wav";
        Log.i("wavUrl...............", wavUrl);

        playerDialog = new Dialog(ResultForSpecialtyActivity.this);
        playerDialog.setContentView(R.layout.dialog_player);
        playerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        WindowManager.LayoutParams lp8 = new WindowManager.LayoutParams();
        lp8.copyFrom(playerDialog.getWindow().getAttributes());
        lp8.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp8.height = WindowManager.LayoutParams.MATCH_PARENT;
        playerDialog.show();
        playerDialog.getWindow().setAttributes(lp8);

        playerPosition = playerDialog.findViewById(R.id.player_position);
        playerDuration = playerDialog.findViewById(R.id.player_duration);
        seekBar = playerDialog.findViewById(R.id.seekBar);
        btRew = playerDialog.findViewById(R.id.bt_rew);
        btPlay = playerDialog.findViewById(R.id.bt_play);
        btFar = playerDialog.findViewById(R.id.bt_ff);
        playerDuration2 = playerDialog.findViewById(R.id.duration_player);
        playerDate = playerDialog.findViewById(R.id.date_player);
        playerTitle = playerDialog.findViewById(R.id.file_name);
        btBeginning = playerDialog.findViewById(R.id.to_beginning);
        btEnd = playerDialog.findViewById(R.id.to_end);
        btBack = playerDialog.findViewById(R.id.back_button1);

//------------------------------ Set Last Modified -------------------------------------------------
        DateFormat dateFormat = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        }
        Date date = null;//You will get date object relative to server/client timezone wherever it is parsed
        try {
            date = dateFormat.parse(obj.getCreated());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); //If you need time just put specific format for time like 'HH:mm:ss'
        String dateStr = formatter.format(date);
        Log.i("File Date", dateStr);

        todayDate = Calendar.getInstance().getTime();
        Log.i("Current/Today's Date", String.valueOf(todayDate));
        String formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(todayDate);
        Log.i("output date ", formattedDate);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            fileDate = simpleDateFormat.parse(dateStr);
            currentDate = simpleDateFormat.parse(formattedDate);

            fDate = fileDate.getTime();
            tDate = currentDate.getTime();

            diffTF = (tDate - fDate) / 86400000;
            Log.i("Diff Start-Today Date", String.valueOf(diffTF));

            if (diffTF == 0) {
                playerDate.setText("Today");
            } else if (diffTF == 1) {
                playerDate.setText("Yesterday");
            } else if (diffTF > 1) {
                playerDate.setText(dateStr);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
//--------------------------------------------------------------------------------------------------
        playerDialog.setCancelable(false);

        deleteBtn = playerDialog.findViewById(R.id.delete_rec);
        shareBtn = playerDialog.findViewById(R.id.share_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseAudio();
                AlertDialog.Builder myAlert = new AlertDialog.Builder(ResultForSpecialtyActivity.this);
                myAlert.setTitle("Delete This Recording?");
                myAlert.setMessage("Are you sure\nyou want to delete this recording?");
                myAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //fileToPlay.delete();
                        dialog.dismiss();
                        DeleteRecording1(obj.getPatient_id(), obj.getId());
                        playerDialog.dismiss();
                        Intent i1 = new Intent(ResultForSpecialtyActivity.this, ResultForSpecialtyActivity.class);
                        i1.putExtra("id14", patientId);
                        i1.putExtra("name13", patientName);
                        i1.putExtra("specialty", specialtyName);
                        i1.putExtra("provider", provider);
                        startActivity(i1);
                        overridePendingTransition(0, 0);
                        finish();
                    }
                });
                myAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                myAlert.setCancelable(false);
                myAlert.show();
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        if (isPlaying) {
            stopAudio();
//            playAudio(fileToPlay);
        } else {
            playAudio(wavUrl, obj.getName());
        }
        int duration = mediaPlayer.getDuration();
        String sDuration = convertFormat(duration);
        playerDuration.setText(sDuration);
        playerDuration2.setText(sDuration);

        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    pauseAudio();
                } else {
//                    if (fileToPlay != null) {
                    resumeAudio();
//                    }
                }
            }
        });

        btFar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();

                if (mediaPlayer.isPlaying() && duration != currentPosition) {
                    currentPosition = currentPosition + 10000;
                    playerPosition.setText(convertFormat(currentPosition));
                    mediaPlayer.seekTo(currentPosition);
                }
            }
        });

        btRew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                if (mediaPlayer.isPlaying() && currentPosition > 10000) {
                    currentPosition = currentPosition - 10000;
                    playerPosition.setText(convertFormat(currentPosition));
                    mediaPlayer.seekTo(currentPosition);
                }
            }
        });

        btBeginning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.seekTo(0);
            }
        });

        btEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.seekTo(mediaPlayer.getDuration());
                pauseAudio();
            }
        });

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAudio();
                playerDialog.dismiss();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                playerPosition.setText(convertFormat(mediaPlayer.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                int
//                resumeAudio(fileToPlay);
            }
        });

    }


    private void pauseAudio() {
        mediaPlayer.pause();
        btPlay.setBackgroundResource(R.drawable.play_player);

        isPlaying = false;
    }

    private void resumeAudio() {
        mediaPlayer.start();
        btPlay.setBackgroundResource(R.drawable.pause_player);
        isPlaying = true;
    }

    private void stopAudio() {
        btPlay.setBackgroundResource(R.drawable.play_player);
        isPlaying = false;
        mediaPlayer.stop();
    }

    private void playAudio(String fileToPlay, String name) {

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(fileToPlay);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        btPlay.setBackgroundResource(R.drawable.pause_player);
//---------------------------------- Set Title -----------------------------------------------------
        String name1 = name;

        Log.i("File Name:", name1);
        String[] allName = name1.split("-", 4);

        for (String sepName : allName) {
            Log.i("Sep Name", sepName);
            nameSplit.add(sepName);
        }
        String patient = (String) nameSplit.get(0);
        String specialty = (String) nameSplit.get(1);
        String providerName = (String) nameSplit.get(2);
        String category = (String) nameSplit.get(3);

        String name2 = category;

        String[] catName = name2.split(".mp3", 2);

        for (String sepCat : catName) {
            Log.i("Sep Cat", sepCat);
            catSplit.add(sepCat);
        }
        String categoryName = (String) catSplit.get(0);
        Log.i("Category Sep", categoryName);

        playerTitle.setText(patient + "-" + specialty + "-" + providerName + "-" + categoryName);
//------------------------------ Set Last Modified -------------------------------------------------
        isPlaying = true;

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.seekTo(0);
                pauseAudio();
            }
        });

        seekBar.setMax(mediaPlayer.getDuration());
        handler1 = new Handler();

        runnable1 = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                handler1.postDelayed(this, 500);
            }
        };
        handler1.postDelayed(runnable1, 0);
    }

    @SuppressLint("DefaultLocale")
    private String convertFormat(int duration) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    public void getRecordingFilesGeneralSpecialty(RecordingModel obj) {
        onClickListener(obj);
    }
    public void getRecordingFilesDiagnosisSpecialty(RecordingModel obj) {
        onClickListener(obj);
    }
    public void getRecordingFilesMedicationsSpecialty(RecordingModel obj) {
        onClickListener(obj);
    }
    public void getRecordingFilesTestsSpecialty(RecordingModel obj) {
        onClickListener(obj);
    }

    private void DeleteRecording1(String patient_id1, String id1) {
        Call<DeleteResponse> call = ApiClient.getDeleteService().DeleteRecording(patient_id1,id1);
        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, retrofit2.Response<DeleteResponse> response) {
                DeleteResponse response1 = response.body();
                Boolean data = response1.getData();
                Log.i("Rec Deleted? : ", String.valueOf(data));
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
            }
        });
    }

//--------------------------------------------------------------------------------------------------
}