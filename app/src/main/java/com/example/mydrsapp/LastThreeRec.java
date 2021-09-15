package com.example.mydrsapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mydrsapp.databinding.ActivityLastThreeRecBinding;
import com.google.android.gms.common.util.IOUtils;

import org.intellij.lang.annotations.JdkConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class LastThreeRec extends AppCompatActivity {

    private File[] allFiles;
    private TimeAgo timeAgo;

    private RecordingAdapter audioListAdapter;
    Button deleteBtn, shareBtn;

    MediaPlayer mediaPlayer = null;
    boolean isPlaying = false;

    Dialog playerDialog;

    TextView playerPosition, playerDuration, playerDuration2, playerTitle, playerDate;
    SeekBar seekBar;
    ImageButton btRew, btPlay, btFar, btBeginning, btEnd, btBack;

    Handler handler1;
    Runnable runnable1;

    String id, name;

    ArrayList<String> arrayList, arrayList2;
    String patient_id, patient_name;
    private RequestQueue mQueue;
    private RecyclerView lastRecordingView;
    private ArrayList<RecordingModel> recordingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_three_rec);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            patient_id = bundle.getString("id2");
            Log.i("Patient ID On L3", patient_id);
            patient_name = bundle.getString("name");
            Log.i("patient name on L3", patient_name);
        }
        arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        mQueue = Volley.newRequestQueue(LastThreeRec.this);
        lastRecordingView = findViewById(R.id.listForRec);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        lastRecordingView.setLayoutManager(linearLayoutManager);
        lastRecordingView.setHasFixedSize(true);

        audioListAdapter = new RecordingAdapter(this, recordingList);
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
                    ArrayList<RecordingModel> newList = new ArrayList<>(recordingList.subList(recordingList.size() - 3, recordingList.size()));
                    audioListAdapter.updateData(newList);
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
        //------------------------------------------------------------------------------------------


        //------------------------------------------------------------------------------------------

        timeAgo = new TimeAgo();

        String path = this.getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles = directory.listFiles();
        //------------------------------------------------------------------------------------------
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void onClickListener(RecordingModel obj) {
        String wavUrl = "http://65.2.3.41:8080/public/recordings/" + obj.getPatient_id() + "/" + obj.getId() + ".wav";
        String file_path = obj.getName();
        Log.i("wavUrl...............", wavUrl);
        playerDialog = new Dialog(LastThreeRec.this);
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

        //playerDate.setText(timeAgo.getTimeAgo(file.lastModified()));
        playerDate.setText(obj.getCreated());
        playerDialog.setCancelable(false);

        deleteBtn = playerDialog.findViewById(R.id.delete_rec);
        shareBtn = playerDialog.findViewById(R.id.share_btn);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //------------------------------------------------------------------------------------------
                String file_name = obj.getName();
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(wavUrl);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setVisibleInDownloadsUi(false);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_AUDIOBOOKS, file_name);
                downloadManager.enqueue(request);
                //------------------------------------------------------------------------------------------

                String sharePath = Environment.getExternalStorageDirectory().getPath()+File.separator + file_name;
                Uri uri1 = Uri.parse(sharePath);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("audio/mp3");
                share.putExtra(Intent.EXTRA_STREAM, uri1);
                startActivity(Intent.createChooser(share, file_name));

            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseAudio();
                AlertDialog.Builder myAlert = new AlertDialog.Builder(LastThreeRec.this);
                myAlert.setTitle("Delete This Recording?");
                myAlert.setMessage("Are you sure\nyou want to delete this recording?");
                myAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //fileToPlay.delete();
                        dialog.dismiss();
                        playerDialog.dismiss();
                        DeleteRecording1(obj.getPatient_id(), obj.getId());
                        Intent i = new Intent(LastThreeRec.this, LastThreeRec.class);
                        i.putExtra("id2", patient_id);
                        i.putExtra("name", patient_name);
                        startActivity(i);
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
        playerTitle.setText(name);
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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void getRecordingFiles(RecordingModel obj) {

        onClickListener(obj);
    }
}