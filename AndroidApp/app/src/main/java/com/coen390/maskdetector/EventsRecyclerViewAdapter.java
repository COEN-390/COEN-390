package com.coen390.maskdetector;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coen390.maskdetector.models.Event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder> {

    private List<Event> events;
    private Context context;
    private EventLogActivity mainActivity;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView eventTimestampText, eventDeviceText, eventSavedStateText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTimestampText = itemView.findViewById(R.id.eventTimestampText);
            eventDeviceText = itemView.findViewById(R.id.eventDeviceText);
            eventSavedStateText = itemView.findViewById(R.id.eventSavedStateText);
        }

        public TextView getEventTimestampText() {
            return eventTimestampText;
        }
        public TextView getEventDeviceText(){
            return eventDeviceText;
        }
        public TextView getEventSavedStateText() {
            return eventSavedStateText;
        }
    }

    public EventsRecyclerViewAdapter(Context context, EventLogActivity eventLogActivity) {
        this.context = context;
        this.events = new ArrayList<>();
        this.mainActivity = eventLogActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getEventTimestampText().setText((new Date((long)(events.get(position).getTimestamp() * 1000))).toString());
        holder.getEventDeviceText().setText("Device: " + events.get(position).getDeviceId());
        if(events.get(position).isSaved()) holder.getEventSavedStateText().setText("Saved");
        else holder.getEventSavedStateText().setText("");

        // Set onClickListener for every item to the same activity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: check if user is admin
                int pos = holder.getLayoutPosition();

                EventActionPromptDf eventActionPromptDf = new EventActionPromptDf();
                Bundle bundle = new Bundle();
                bundle.putString("event", events.get(pos).toString());
                bundle.putBoolean("savedEvent", false);
                eventActionPromptDf.setArguments(bundle);
                eventActionPromptDf.show(mainActivity.getSupportFragmentManager(), "EventActionPromptDf");
            }
        }); // MIGHT NEED THIS
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
            if(event.get$id().equals(events.get(i).get$id())){
                events.remove(i);
            }
        }
    }

    public void modifyEvent(Event event) {
        for(int i = 0; i < events.size(); i++){
            if(event.get$id().equals(events.get(i).get$id())){
                events.set(i, event);
            }
        }
    }

    public void setEventsList(List<Event> eventsList) {
        events = eventsList;
    }
}
