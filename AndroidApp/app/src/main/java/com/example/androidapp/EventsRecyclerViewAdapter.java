package com.example.androidapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Timestamp;
import java.util.Date;
import java.util.List;

public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder> {

    private SharedPreferencesHelper sharedPreferencesHelper;
    private JSONArray events;
    private int size;

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

    public EventsRecyclerViewAdapter(Context context, JSONObject events) {
        this.sharedPreferencesHelper = new SharedPreferencesHelper(context);
        try {
            this.events = events.getJSONArray("documents");
            this.size = events.getInt("sum");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject event = new JSONObject();
        Date date = new Date();
        try {
            event = events.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            date = new Date((long) event.getDouble("timestamp"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.getItemText().setText(date.toString());

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
        return this.size;
    }
}
