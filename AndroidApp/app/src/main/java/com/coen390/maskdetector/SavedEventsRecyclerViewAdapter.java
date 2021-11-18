package com.coen390.maskdetector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView savedEventNameText, savedEventTimestampText, savedEventDeviceText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            savedEventNameText = itemView.findViewById(R.id.savedEventNameText);
            savedEventTimestampText = itemView.findViewById(R.id.savedEventTimestampText);
            savedEventDeviceText = itemView.findViewById(R.id.savedEventDeviceText);
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

    }

    public SavedEventsRecyclerViewAdapter(Context context) {
        this.context = context;
        this.events = new ArrayList<>();
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
        holder.getEventTimestampText().setText((new Date((long)(events.get(position).getEvent().getTimestamp() * 1000))).toString());
        holder.getEventDeviceText().setText("Device: " + events.get(position).getEvent().getDeviceId());

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

    public void addEvent(SavedEvent event) {
        events.add(event);
    }

    public void deleteEvent(SavedEvent event) {
        for(int i = 0; i < events.size(); i++){
            if(event.getEvent().get$id().equals(events.get(i).getEvent().get$id())){
                events.remove(i);
            }
        }
    }

    public void modifyEvent(SavedEvent event) {
        for(int i = 0; i < events.size(); i++){
            if(event.getEvent().get$id().equals(events.get(i).getEvent().get$id())){
                events.set(i, event);
            }
        }
    }

    public void setEventsList(List<SavedEvent> eventsList) {
        events = eventsList;
    }
}
