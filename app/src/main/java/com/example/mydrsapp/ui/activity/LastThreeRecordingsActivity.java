package com.example.mydrsapp.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mydrsapp.model.RecordingModel;
import com.example.mydrsapp.R;
import com.example.mydrsapp.model.DeleteResponse;
import com.example.mydrsapp.model.RecordingModel;
import com.example.mydrsapp.ui.adapter.lastthreerecordings.LastThreeRecordingsAdapter;
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

public class LastThreeRecordingsActivity extends AppCompatActivity {

//--------------------------------- For Offline Recordings -----------------------------------------
//    private RecyclerView audioList;
//    private File[] allFiles;
//    private TimeAgo timeAgo;
//
//    private LastThreeRecordingsAdapter audioListAdapter;
//--------------------------------------------------------------------------------------------------

//--------------------------------- For Online Recordings ------------------------------------------

    private LastThreeRecordingsAdapter audioListAdapter;
    Button deleteBtn, shareBtn;
    ArrayList nameSplit, catSplit;
    MediaPlayer mediaPlayer = null;
    boolean isPlaying = false;
    DownloadManager manager;

    Dialog playerDialog;

    TextView playerPosition, playerDuration, playerDuration2, playerTitle, playerDate, noRecordings;
    SeekBar seekBar;
    ImageButton btRew, btPlay, btFar, btBeginning, btEnd, btBack;

    Handler handler1;
    Runnable runnable1;

    ArrayList<RecordingModel> newListGeneral;

    ArrayList<String> arrayList, arrayList2;
    String patient_id, patient_name;
    private RequestQueue mQueue;
    private RecyclerView lastRecordingView;
    private ArrayList<RecordingModel> recordingList = new ArrayList<>();

    Date todayDate, fileDate, currentDate;
    long fDate, tDate, diffTF;
//--------------------------------------------------------------------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_three_recordings);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setNavigationBarColor(Color.parseColor("#0272B9"));
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }

        noRecordings = findViewById(R.id.no_recordings_three);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            patient_id = bundle.getString("id2");
            Log.i("Patient ID On L3", patient_id);
            patient_name = bundle.getString("name");
            Log.i("patient name on L3", patient_name);
        }
        Log.i("Outside LS3R ID", patient_id);
        Log.i("Outside LS3R Name", patient_name);

//--------------------------------- For Offline Recordings------------------------------------------
//        audioList = findViewById(R.id.audio_list_view);
////        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
////        linearLayoutManager.setReverseLayout(true);
////        linearLayoutManager.setStackFromEnd(true);
//        timeAgo = new TimeAgo();

//        String path = this.getExternalFilesDir("/").getAbsolutePath();
//        File directory = new File(path);
//        allFiles = directory.listFiles();
//
//
////        Arrays.sort(allFiles, Comparator.comparingLong(File::lastModified));
//
//        audioListAdapter = new LastThreeRecordingsAdapter(allFiles, id, name, this);
//
////        audioList.setLayoutManager(linearLayoutManager);
//        audioList.setHasFixedSize(true);
//        audioList.setLayoutManager(new LinearLayoutManager(this));
//
//        audioList.setAdapter(audioListAdapter);
//
//        Log.i("String of Adapter", String.valueOf(audioList.getAdapter().getItemCount()));
//        if (audioList.getAdapter().getItemCount() == 0){
//            AlertDialog.Builder myAlert = new AlertDialog.Builder(LastThreeRecordingsActivity.this);
//            myAlert.setTitle("No Recordings Found");
//            myAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    Intent i1 = new Intent(LastThreeRecordingsActivity.this, SearchActivity.class);
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
//--------------------------------------------------------------------------------------------------

//--------------------------------- For Online Recordings ------------------------------------------

        arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        nameSplit = new ArrayList<>();
        catSplit = new ArrayList<>();
        mQueue = Volley.newRequestQueue(LastThreeRecordingsActivity.this);
        lastRecordingView = findViewById(R.id.audio_list_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        lastRecordingView.setLayoutManager(linearLayoutManager);
        lastRecordingView.setHasFixedSize(true);

        audioListAdapter = new LastThreeRecordingsAdapter(this, recordingList);
        lastRecordingView.setAdapter(audioListAdapter);

        //----------------------------get all rec file names----------------------------------------
        String url1 = "http://65.2.3.41:8080/recording?patient_id=" + patient_id;
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

//                    newListGeneral = new ArrayList<>();
//                    for (int i=0; i<recordingList.size(); i++){
//                        String category = recordingList.get(i).getCategory();
//                        if (category.equals("general")){
//                            newListGeneral.add(recordingList.get(i));
//                        }
//                    }
//                    audioListAdapter.updateData(newListGeneral);

                    if (recordingList.size()==0){
                        noRecordings.setVisibility(View.VISIBLE);
                        lastRecordingView.setVisibility(View.GONE);
                    }else if (recordingList.size()==1){
                        ArrayList<RecordingModel> newList = new ArrayList<>(recordingList.subList(recordingList.size() - 1, recordingList.size()));
                        audioListAdapter.updateData(newList);
                    }else if (recordingList.size()==2){
                        ArrayList<RecordingModel> newList = new ArrayList<>(recordingList.subList(recordingList.size() - 2, recordingList.size()));
                        audioListAdapter.updateData(newList);
                    }else if (recordingList.size()>=3){
                        ArrayList<RecordingModel> newList = new ArrayList<>(recordingList.subList(recordingList.size() - 3, recordingList.size()));
                        audioListAdapter.updateData(newList);
                    }if (audioListAdapter.getItemCount()==0){
                        noRecordings.setVisibility(View.VISIBLE);
                        lastRecordingView.setVisibility(View.GONE);
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

//--------------------------------- For Online Recordings ------------------------------------------

    public void onClickListener(RecordingModel obj) {
        String wavUrl = "http://65.2.3.41:8080/public/recordings/" + obj.getPatient_id() + "/" + obj.getId() + ".wav";
        Log.i("wavUrl...............", wavUrl);

        playerDialog = new Dialog(LastThreeRecordingsActivity.this);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
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
                AlertDialog.Builder myAlert = new AlertDialog.Builder(LastThreeRecordingsActivity.this);
                myAlert.setTitle("Delete This Recording?");
                myAlert.setMessage("Are you sure\nyou want to delete this recording?");
                myAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //fileToPlay.delete();
                        dialog.dismiss();
                        DeleteRecording1(obj.getPatient_id(), obj.getId());
                        playerDialog.dismiss();
                        Intent i1 = new Intent(LastThreeRecordingsActivity.this, LastThreeRecordingsActivity.class);
                        i1.putExtra("id2", patient_id);
                        i1.putExtra("name", patient_name);
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
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                //------------------------------Download audio file from url------------------------------
                String fileName = obj.getName();
                manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(wavUrl);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_AUDIOBOOKS,fileName);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                manager.enqueue(request);
                //----------------------------------------------------------------------------------------

                String sharePath = Environment.DIRECTORY_AUDIOBOOKS+fileName;
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("audio/mp3");
                share.putExtra(Intent.EXTRA_STREAM, sharePath);
                startActivity(Intent.createChooser(share, fileName));
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

    public void getRecordingFiles(RecordingModel obj) {
        onClickListener(obj);
    }

    private void DeleteRecording1(String patient_id1, String id1) {
        Call<DeleteResponse> call = ApiClient.getDeleteService().DeleteRecording(patient_id1, id1);
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

    public void back_to_navigate(View view) {
        Intent i1 = new Intent(LastThreeRecordingsActivity.this, SearchActivity.class);
        i1.putExtra("id3", patient_id);
        i1.putExtra("name", patient_name);
        startActivity(i1);
        overridePendingTransition(0, 0);
        finish();
    }

}