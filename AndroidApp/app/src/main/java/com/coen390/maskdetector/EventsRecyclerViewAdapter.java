package com.coen390.maskdetector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coen390.maskdetector.controllers.SharedPreferencesHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

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

    public EventsRecyclerViewAdapter(Context context, JSONArray events) {
        this.sharedPreferencesHelper = new SharedPreferencesHelper(context);
        this.events = events;
        this.size = sharedPreferencesHelper.getEventsSize();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject event = events.getJSONObject(position);
            Date date = new Date((long) event.getDouble("timestamp"));
            holder.getItemText().setText(date.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    // Used to refresh the data stored so that the notification of changed data set takes the new data
    public void updateList(){
        events = sharedPreferencesHelper.getEvents();
        size = sharedPreferencesHelper.getEventsSize();
    }
}
