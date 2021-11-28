package com.coen390.maskdetector;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coen390.maskdetector.controllers.SharedPreferencesHelper;
import com.coen390.maskdetector.models.SavedEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SavedEventsRecyclerViewAdapter extends RecyclerView.Adapter<SavedEventsRecyclerViewAdapter.ViewHolder> {

    private SharedPreferencesHelper sharedPreferencesHelper;
    private List<SavedEvent> events;
    private Context context;
    private String highlightId;
    private SavedEventsActivity savedEventsActivity;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView savedEventNameText, savedEventTimestampText, savedEventDeviceText;
        private FrameLayout frameLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            savedEventNameText = itemView.findViewById(R.id.savedEventNameText);
            savedEventTimestampText = itemView.findViewById(R.id.savedEventTimestampText);
            savedEventDeviceText = itemView.findViewById(R.id.savedEventDeviceText);
            frameLayout = itemView.findViewById(R.id.savedEventLayout);
        }

        public TextView getSavedEventNameText() {
            return savedEventNameText;
        }
        public TextView getEventTimestampText() {
            return savedEventTimestampText;
        }
        public TextView getEventDeviceText(){
            return savedEventDeviceText;
        }
        public FrameLayout getFrameLayout() {
            return frameLayout;
        }

    }

    public SavedEventsRecyclerViewAdapter(Context context, SavedEventsActivity savedEventsActivity, String highlightId) {
        this.context = context;
        this.events = new ArrayList<>();
        this.highlightId = highlightId;
        this.savedEventsActivity = savedEventsActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_events_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getSavedEventNameText().setText(events.get(position).getName());
        holder.getEventTimestampText().setText((new Date((long)(events.get(position).getTimestamp() * 1000))).toString());
        holder.getEventDeviceText().setText("Device: " + events.get(position).getDeviceId());

        if(events.get(position).getEventId().equals(highlightId)){
            holder.getFrameLayout().setBackgroundColor(Color.GREEN);
        } else holder.getFrameLayout().setBackgroundColor(Color.WHITE);


        // Set onClickListener for every item to the same activity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: check if user is admin
                int pos = holder.getLayoutPosition();

                EventActionPromptDf eventActionPromptDf = new EventActionPromptDf();
                Bundle bundle = new Bundle();
                bundle.putString("event", events.get(position).toString());
                bundle.putBoolean("savedEvent", true);
                bundle.putString("fileId", events.get(position).getFileId());
                bundle.putString("eventId", events.get(position).getEventId());
                eventActionPromptDf.setArguments(bundle);
                eventActionPromptDf.show(savedEventsActivity.getSupportFragmentManager(), "EventActionPromptDf");
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void addEvent(SavedEvent event) {
        events.add(event);
    }

    public void deleteEvent(SavedEvent event) {
        for(int i = 0; i < events.size(); i++){
            if(event.get$id().equals(events.get(i).get$id())){
                events.remove(i);
                break;
            }
        }
    }

    public void modifyEvent(SavedEvent event) {
        for(int i = 0; i < events.size(); i++){
            if(event.get$id().equals(events.get(i).get$id())){
                events.set(i, event);
                break;
            }
        }
    }

    public void setEventsList(List<SavedEvent> eventsList) {
        events = eventsList;
        for(int i = 0; i < events.size(); i++){
            if(events.get(i).getEventId().equals(highlightId)){
                savedEventsActivity.setHighlightedPosition(i);
            }
            notifyItemInserted(i);
        }
    }
}
