package com.example.mydrsapp.ui.adapter.viewallrecordings;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydrsapp.model.RecordingModel;
import com.example.mydrsapp.ui.activity.ViewAllRecActivity;
import com.example.mydrsapp.R;
import com.example.mydrsapp.model.RecordingModel;
import com.example.mydrsapp.ui.activity.ViewAllRecActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
/*
public class ViewAllRecordingsMedicationsAdapter extends RecyclerView.Adapter<ViewAllRecordingsMedicationsAdapter.AudioViewHolder> {
    private File[] allFiles;
    private TimeAgo timeAgo;
    MediaMetadataRetriever metaRetriever;
    ArrayList lastDuration, nameSplit;
    View view;
    Button deleteBtn, shareBtn;

    MediaPlayer mediaPlayer = null;
    boolean isPlaying = false;
    File fileToPlay = null;

    Dialog playerDialog;

    TextView playerPosition, playerDuration, playerDuration2, playerTitle, playerDate;
    SeekBar seekBar;
    ImageButton btRew, btPlay, btFar, btBeginning, btEnd, btBack;

    Handler handler1;
    Runnable runnable1;
    int countMedications;

    private ViewAllRecordingsActivity viewAllRecordingsMedications;
    String id, name;

    public ViewAllRecordingsMedicationsAdapter(File[] allFiles, String id, String name, ViewAllRecordingsActivity viewAllRecordings3) {
        this.allFiles = allFiles;
        viewAllRecordingsMedications = viewAllRecordings3;
        this.id = id;
        this.name = name;
    }

    @NonNull
    @Override
    public ViewAllRecordingsMedicationsAdapter.AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        timeAgo = new TimeAgo();
        metaRetriever = new MediaMetadataRetriever();
        lastDuration = new ArrayList<>();
        nameSplit = new ArrayList<>();

        return new ViewAllRecordingsMedicationsAdapter.AudioViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewAllRecordingsMedicationsAdapter.AudioViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playerDialog = new Dialog(v.getContext());
                playerDialog.setContentView(R.layout.dialog_player);
//                playerDialog.getWindow().setLayout();
//                playerDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.myowngradient));
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

                playerDate.setText(timeAgo.getTimeAgo(allFiles[position].lastModified()));
                playerDialog.setCancelable(false);

                deleteBtn = playerDialog.findViewById(R.id.delete_btn);
                shareBtn = playerDialog.findViewById(R.id.share_btn);

                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder myAlert = new AlertDialog.Builder(v.getContext());
                        myAlert.setTitle("Delete This Recording?");
                        myAlert.setMessage("Are you sure\nyou want to delete this recording?");
                        myAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fileToPlay.delete();
                                dialog.dismiss();
                                Intent intent= new Intent(v.getContext(), ViewAllRecordingsActivity.class);
                                intent.putExtra("id14", id);
                                intent.putExtra("name13", name);
                                viewAllRecordingsMedications.startActivity(intent);
                                viewAllRecordingsMedications.finish();
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
                    fileToPlay = allFiles[position];
                    playAudio(fileToPlay);
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
        });
//--------------------------------------------------------------------------------------------------
        File file = new File(String.valueOf(allFiles[position]));

        Date lastModDate = new Date(file.lastModified());

        Log.i("File last modified:", lastModDate.toString());
        String[] allDate = lastModDate.toString().split(" ", 6);

        for (String sepDate : allDate) {
            Log.i("Sep Date", sepDate);
            lastDuration.add(sepDate);
        }
        String day = (String) lastDuration.get(0);
        String mon = (String) lastDuration.get(1);
        String date = (String) lastDuration.get(2);
        String time = (String) lastDuration.get(3);
        String gmt = (String) lastDuration.get(4);
        String year = (String) lastDuration.get(5);
//--------------------------------------------------------------------------------------------------
        String name = allFiles[position].getName();

        Log.i("File last modified:", name);
        String[] allName = name.split("-", 4);

        for (String sepName : allName) {
            Log.i("Sep Date", sepName);
            nameSplit.add(sepName);
        }
        String patient = (String) nameSplit.get(0);
        String specialty = (String) nameSplit.get(1);
        String providerName = (String) nameSplit.get(2);
        String category = (String) nameSplit.get(3);
//--------------------------------------------------------------------------------------------------
        if (category.equals("Medications.mp3")) {
            holder.list_title.setText(patient + "-" + specialty + "-" + providerName + "-" + category);

            countMedications = countMedications + 1;
            viewAllRecordingsMedications.medicationConstrain.setVisibility(View.VISIBLE);
            viewAllRecordingsMedications.resM.setVisibility(View.GONE);

//        Arrays.sort(allFiles, Comparator.comparingLong(File :: lastModified));
//        holder.list_title.setText(allFiles[position].getName());

//        holder.list_date.setText(timeAgo.getTimeAgo(allFiles[position].lastModified()));
//        holder.list_date.setText(lastModDate.toString()); // for actual date
            String timeDate = timeAgo.getTimeAgo(allFiles[position].lastModified());
            if (timeDate.equals("different")){
//        holder.list_date.setText(lastModDate.toString()); // for actual date
                holder.list_date.setText(day + " " + mon + " " + date + " " + year); // for actual date
            } else {
                holder.list_date.setText(timeAgo.getTimeAgo(allFiles[position].lastModified()));
            }            metaRetriever.setDataSource(allFiles[position].getAbsolutePath());

            String out = "";
            // get mp3 info

            // convert duration to minute:seconds
            String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            Log.v("time", duration);
            long dur = Long.parseLong(duration);
            String seconds = String.valueOf((dur % 60000) / 1000);

            Log.v("seconds", seconds);
            String minutes = String.valueOf(dur / 60000);
            out = minutes + ":" + seconds;
            if (seconds.length() == 1) {
                holder.list_duration.setText("0" + minutes + ":0" + seconds);
            } else {
                holder.list_duration.setText("0" + minutes + ":" + seconds);
            }
            Log.v("minutes", minutes);
            // close object
            metaRetriever.release();

        } else {
//            holder.list_title.setVisibility(View.GONE);
//            holder.list_date.setVisibility(View.GONE);
//            holder.list_duration.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
            view.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }

        if (countMedications == 0){
            viewAllRecordingsMedications.medicationConstrain.setVisibility(View.GONE);
            viewAllRecordingsMedications.resM.setVisibility(View.GONE);
        }
    }

    @SuppressLint("DefaultLocale")
    private String convertFormat(int duration) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    public void pauseAudio() {
        mediaPlayer.pause();
        btPlay.setBackgroundResource(R.drawable.play_player);

        isPlaying = false;
    }

    public void resumeAudio() {
        mediaPlayer.start();
        btPlay.setBackgroundResource(R.drawable.pause_player);
        isPlaying = true;
    }

    private void stopAudio() {
        btPlay.setBackgroundResource(R.drawable.play_player);
        isPlaying = false;
        mediaPlayer.stop();
    }

    private void playAudio(File fileToPlay) {

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        btPlay.setBackgroundResource(R.drawable.pause_player);
        playerTitle.setText(fileToPlay.getName());
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


    @Override
    public int getItemCount() {
//        if (allFiles.length == 0){
//
//            return 0;
//        }else if (allFiles.length >= 1 && allFiles.length <= 3){
//            return allFiles.length;
//        }else {
//            return 3;
//        }
        return allFiles.length;
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder {
        private TextView list_title, list_date, list_duration;
        ConstraintLayout constraintLayout;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            list_title = itemView.findViewById(R.id.list_title);
            list_date = itemView.findViewById(R.id.list_date);
            list_duration = itemView.findViewById(R.id.list_duration);
            constraintLayout = itemView.findViewById(R.id.list_constrain);
        }
    }
}
 */

public class ViewAllRecordingsMedicationsAdapter extends RecyclerView.Adapter<ViewAllRecordingsMedicationsAdapter.AudioViewHolder> {
    Date todayDate, fileDate, currentDate;
    ArrayList nameSplit, catSplit;
    long fDate, tDate, diffTF;
    private ArrayList<RecordingModel> recordingList;
    private ViewAllRecActivity activityMedications;

    public ViewAllRecordingsMedicationsAdapter(ViewAllRecActivity viewAllRecordingsActivity, ArrayList<RecordingModel> recordingList) {
        this.recordingList = recordingList;
        this.activityMedications = viewAllRecordingsActivity;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        nameSplit = new ArrayList<>();
        catSplit = new ArrayList<>();
        return new AudioViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, @SuppressLint("RecyclerView") int position) {
        RecordingModel obj = recordingList.get(position);
//---------------------------------- Set Title -----------------------------------------------------
        String name1 = obj.getName();

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

        holder.list_title.setText(patient + "-" + specialty + "-" + providerName + "-" + categoryName);
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

            if (diffTF==0){
                holder.list_date.setText("Today");
            } else if (diffTF == 1) {
                holder.list_date.setText("Yesterday");
            } else if (diffTF > 1) {
                holder.list_date.setText(dateStr);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
//---------------------------------- Set Duration --------------------------------------------------
        int sec = obj.getDuration_sec();//
        Date d = new Date(sec * 1000L);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss"); // HH for 0-23
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String time = df.format(d);

        holder.list_duration.setText(time);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMedications.getRecordingFilesMedications(obj);
            }
        });
//--------------------------------------------------------------------------------------------------
    }

    @Override
    public int getItemCount() {
        return recordingList.size();
    }

    public void updateDataMedications(ArrayList<RecordingModel> recordingList) {
        this.recordingList = recordingList;
        notifyDataSetChanged();
        activityMedications.resM.setVisibility(View.GONE);
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder {
        private TextView list_title, list_date, list_duration;
        private ConstraintLayout container;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            list_title = itemView.findViewById(R.id.list_title);
            list_date = itemView.findViewById(R.id.list_date);
            list_duration = itemView.findViewById(R.id.list_duration);
            container = itemView.findViewById(R.id.container);
        }
    }
//--------------------------------------------------------------------------------------------------

}