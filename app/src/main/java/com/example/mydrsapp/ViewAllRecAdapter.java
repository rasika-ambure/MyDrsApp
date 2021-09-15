package com.example.mydrsapp;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewAllRecAdapter extends RecyclerView.Adapter<ViewAllRecAdapter.AudioViewHolder2> {

    private ArrayList<RecordingModel> recordingList;
    private ViewAllRec activity;

    public ViewAllRecAdapter(ViewAllRec activity, ArrayList<RecordingModel> recordingList) {
        this.recordingList=recordingList;
        this.activity=activity;
    }

    @NonNull
    public AudioViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        return new AudioViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull AudioViewHolder2 holder, int position) {
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
    }

    @Override
    public int getItemCount() {
        return recordingList.size();
    }

    public void updateData(ArrayList<RecordingModel> recordingList) {
        this.recordingList = recordingList;
        notifyDataSetChanged();
    }


    public class AudioViewHolder2 extends RecyclerView.ViewHolder {
        private TextView list_title, list_date, list_duration;
        private ConstraintLayout container;

        public AudioViewHolder2(@NonNull View itemView) {
            super(itemView);

            list_title = itemView.findViewById(R.id.list_title);
            list_date = itemView.findViewById(R.id.list_date);
            list_duration = itemView.findViewById(R.id.list_duration);
            container = itemView.findViewById(R.id.container);
        }

    }

}
