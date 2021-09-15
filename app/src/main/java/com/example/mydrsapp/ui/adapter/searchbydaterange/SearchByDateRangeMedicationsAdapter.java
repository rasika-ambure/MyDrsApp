package com.example.mydrsapp.ui.adapter.searchbydaterange;

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

import com.example.mydrsapp.R;
import com.example.mydrsapp.model.RecordingModel;
import com.example.mydrsapp.ui.activity.ResultForSearchByDateActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
/*
public class SearchByDateRangeMedicationsAdapter extends RecyclerView.Adapter<SearchByDateRangeMedicationsAdapter.AudioViewHolder> {

    private File[] allFiles;
    private TimeAgo timeAgo;
    MediaMetadataRetriever metaRetriever;
    ArrayList lastDuration, nameSplit, currentSepDate;
    View view;
    Date currentDate, date1, date2;
    int count, countF;
    String todayDate, fileDate;
    long fileD, todayD, diffFT, startDays, endDays;

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
    String id, name;

    int countMedications;
    private ResultForSearchByDateActivity resultForSearchByDateMedications;

    public SearchByDateRangeMedicationsAdapter(File[] allFiles, long startDays, long endDays, String id, String name, ResultForSearchByDateActivity resultForSearchByDate3) {
        this.allFiles = allFiles;
        this.startDays = startDays;
        this.endDays = endDays;
        resultForSearchByDateMedications = resultForSearchByDate3;
        this.id = id;
        this.name = name;
    }

    @NonNull
    @Override
    public SearchByDateRangeMedicationsAdapter.AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        timeAgo = new TimeAgo();
        metaRetriever = new MediaMetadataRetriever();
        lastDuration = new ArrayList<>();
        nameSplit = new ArrayList<>();
        currentSepDate = new ArrayList<>();

        return new SearchByDateRangeMedicationsAdapter.AudioViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull SearchByDateRangeMedicationsAdapter.AudioViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //--------------------------------------------------------------------------------------------------
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
                                Intent intent= new Intent(v.getContext(), ResultForSearchByDateActivity.class);
                                intent.putExtra("id10", id);
                                intent.putExtra("name11", name);
                                resultForSearchByDateMedications.startActivity(intent);
                                resultForSearchByDateMedications.finish();
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

//-------------------------------------- Today's Date ----------------------------------------------
        currentDate = Calendar.getInstance().getTime();
        Log.i("Current/Today's Date", String.valueOf(currentDate));

        String[] allDateC = currentDate.toString().split(" ", 6);

        for (String sepDateC : allDateC) {
            Log.i("Sep Date", sepDateC);
            currentSepDate.add(sepDateC);
        }
        String dayC = (String) currentSepDate.get(0);
        String monC = (String) currentSepDate.get(1);
        String dateC = (String) currentSepDate.get(2);
        String timeC = (String) currentSepDate.get(3);
        String gmtC = (String) currentSepDate.get(4);
        String yearC = (String) currentSepDate.get(5);

        Log.i("Separated Day Adapter", dateC);
        Log.i("Separated Month Adapter", monC);
        Log.i("Separated Year Adapter", yearC);

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
        Log.i("Today's Date Adapter", todayDate);

//-------------------------- Last Modified Date of File --------------------------------------------
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

        if (mon.equals("Jan")) {
            countF = 1;
        } else if (mon.equals("Feb")) {
            countF = 2;
        } else if (mon.equals("Mar")) {
            countF = 3;
        } else if (mon.equals("Apr")) {
            countF = 4;
        } else if (mon.equals("May")) {
            countF = 5;
        } else if (mon.equals("Jun")) {
            countF = 6;
        } else if (mon.equals("Jul")) {
            countF = 7;
        } else if (mon.equals("Aug")) {
            countF = 8;
        } else if (mon.equals("Sep")) {
            countF = 9;
        } else if (mon.equals("Oct")) {
            countF = 10;
        } else if (mon.equals("Nov")) {
            countF = 11;
        } else if (mon.equals("Dec")) {
            countF = 12;
        }

        fileDate = date + "/" + countF + "/" + year;
        Log.i("Last Mod Date Adapter", fileDate);

//----------------------------- Difference in File and Current Date --------------------------------

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            date1 = simpleDateFormat.parse(fileDate);
            Log.i("Date Parsed Adapter", String.valueOf(date1));
            date2 = simpleDateFormat.parse(todayDate);

            fileD = date1.getTime();
            todayD = date2.getTime();

            diffFT = (todayD - fileD) / 86400000;
            Log.i("Diff File-Today Date", String.valueOf(diffFT));
        } catch (ParseException e) {
            e.printStackTrace();
        }

//----------------------------- Separated File Name ------------------------------------------------
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
        if (startDays >= diffFT && endDays <= diffFT) {
            if (category.equals("Medications.mp3")) {
                holder.list_title.setText(patient + "-" + specialty + "-" + providerName + "-" + category);

                countMedications = countMedications + 1;
                resultForSearchByDateMedications.medicationConstrain.setVisibility(View.VISIBLE);
                resultForSearchByDateMedications.resM.setVisibility(View.GONE);
//        Arrays.sort(allFiles, Comparator.comparingLong(File :: lastModified));
//        holder.list_title.setText(allFiles[position].getName());

//        holder.list_date.setText(timeAgo.getTimeAgo(allFiles[position].lastModified()));
//        holder.list_date.setText(lastModDate.toString()); // for actual date
                String timeDate = timeAgo.getTimeAgo(allFiles[position].lastModified());
                if (timeDate.equals("different")) {
//        holder.list_date.setText(lastModDate.toString()); // for actual date
                    holder.list_date.setText(day + " " + mon + " " + date + " " + year); // for actual date
                } else {
                    holder.list_date.setText(timeAgo.getTimeAgo(allFiles[position].lastModified()));
                }
                metaRetriever.setDataSource(allFiles[position].getAbsolutePath());

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

            if (countMedications == 0) {
                resultForSearchByDateMedications.medicationConstrain.setVisibility(View.GONE);
                resultForSearchByDateMedications.resM.setVisibility(View.GONE);
            }
        } else {
            resultForSearchByDateMedications.medicationConstrain.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
            view.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
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

public class SearchByDateRangeMedicationsAdapter extends RecyclerView.Adapter<SearchByDateRangeMedicationsAdapter.AudioViewHolder> {
    Date todayDate, fileDate, currentDate;
    ArrayList nameSplit, catSplit;
    long fDate, tDate, diffTF;
    private ArrayList<RecordingModel> recordingList;
    private ResultForSearchByDateActivity activityMedications;

    public SearchByDateRangeMedicationsAdapter(ResultForSearchByDateActivity resultForSearchByDateActivity, ArrayList<RecordingModel> recordingList) {
        this.recordingList = recordingList;
        this.activityMedications = resultForSearchByDateActivity;
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
                activityMedications.getRecordingFilesMedicationsDate(obj);
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
