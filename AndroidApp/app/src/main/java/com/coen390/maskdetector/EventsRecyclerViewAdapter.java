package com.coen390.maskdetector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coen390.maskdetector.controllers.SharedPreferencesHelper;
import com.coen390.maskdetector.models.Device;
import com.coen390.maskdetector.models.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder> {

    private SharedPreferencesHelper sharedPreferencesHelper;
    private List<Event> events;
    private Context context;

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

    public EventsRecyclerViewAdapter(Context context) {
        this.context = context;
        this.events = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getItemText().setText(events.get(position).getTimestamp().toString());

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
        return events.size();
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public void deleteEvent(Event event) {
        for(int i = 0; i < events.size(); i++){
            if(event.getId().equals(events.get(i).getId())){
                events.remove(i);
            }
        }
    }

    public void modifyEvent(Event event) {
        for(int i = 0; i < events.size(); i++){
            if(event.getId().equals(events.get(i).getId())){
                events.set(i, event);
            }
        }
    }

    public void setEventsList(List<Event> eventsList) {
        events = eventsList;
    }
}
