package com.example.androidapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder> {

    private SharedPreferencesHelper sharedPreferencesHelper;
    private List<String> events;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView eventText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventText = itemView.findViewById(R.id.eventText);
        }

        public TextView getItemText() {
            return eventText;
        }
    }

    public EventsRecyclerViewAdapter(Context context, List<String> events) {
        this.sharedPreferencesHelper = new SharedPreferencesHelper(context);
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getItemText().setText("[Date] - [Time]");

        // Set onClickListener for every item to the same activity
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int pos = holder.getLayoutPosition();
//
//                Intent intent = new Intent(view.getContext(), ProfileActivity.class);
//                intent.putExtra("studentId", sortedProfiles.get(pos).getStudentId());
//                view.getContext().startActivity(intent);
//            }
//        }); // MIGHT NEED THIS
    }

    @Override
    public int getItemCount() {
        if(events.equals("")) return 0;
        else return events.size();
    }
}
