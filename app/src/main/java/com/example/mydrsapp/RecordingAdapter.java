package com.example.mydrsapp;

import android.annotation.SuppressLint;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class RecordingAdapter extends RecyclerView.Adapter<RecordingAdapter.AudioViewHolder> {


    private ArrayList<RecordingModel> recordingList;
    private LastThreeRec activity;

    public RecordingAdapter(LastThreeRec activity, ArrayList<RecordingModel> recordingList) {
        this.recordingList=recordingList;
        this.activity=activity;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);


        return new AudioViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, @SuppressLint("RecyclerView") int position) {
        RecordingModel obj = recordingList.get(position);
        holder.list_title.setText(obj.getName());
        holder.list_date.setText(obj.getCreated());
        holder.list_duration.setText(String.valueOf(obj.getDuration_sec()));
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getRecordingFiles(obj);
            }
        });

        /*


        String seconds = String.valueOf((dur % 60000) / 1000);

        String minutes = String.valueOf(dur / 60000);
        out = minutes + ":" + seconds;

        if (seconds.length() == 1) {
            holder.list_duration.setText("0" + minutes + ":0" + seconds);
        } else {
            holder.list_duration.setText("0" + minutes + ":" + seconds);
        }

        metaRetriever.release();*/

    }

    @Override
    public int getItemCount() {
        return recordingList.size();
    }

    public void updateData(ArrayList<RecordingModel> recordingList) {
        this.recordingList = recordingList;
        notifyDataSetChanged();
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

}
