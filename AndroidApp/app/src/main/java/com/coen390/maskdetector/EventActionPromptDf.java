package com.coen390.maskdetector;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import com.coen390.maskdetector.controllers.EventsController;
import com.coen390.maskdetector.controllers.SavedEventsController;
import com.coen390.maskdetector.controllers.VideoClipController;
import com.coen390.maskdetector.models.Event;
import com.coen390.maskdetector.models.SavedEvent;

import org.json.JSONException;
import org.json.JSONObject;

public class EventActionPromptDf extends DialogFragment {

    private Button savedEventPromptButton, cancelPromptButton, deleteEventPromptButton, saveVideoButton;
    private EventsController eventsController;
    private SavedEventsController savedEventsController;
    private VideoClipController videoClipController;
    private Event event;
    private SavedEvent savedEvent;
    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.df_event_action_prompt, container);

        savedEventPromptButton = view.findViewById(R.id.buttonSavedEventPrompt);
        cancelPromptButton = view.findViewById(R.id.buttonCancelPrompt);
        deleteEventPromptButton = view.findViewById(R.id.buttonDeleteEventPrompt);
        saveVideoButton = view.findViewById(R.id.buttonVideoView);

        bundle = getArguments();

        setupButtons();

        eventsController = new EventsController(requireActivity().getApplicationContext());
        savedEventsController = new SavedEventsController(requireActivity().getApplicationContext());
        videoClipController = new VideoClipController(requireActivity().getApplicationContext());

        cancelPromptButton.setOnClickListener(v -> dismiss());

        savedEventPromptButton.setOnClickListener(v -> {
            if(!bundle.getBoolean("savedEvent")) {
                if (event.isSaved()) {
                    // If event is saved, go to saved events and highlight it
                    ((EventLogActivity) requireActivity()).goToSavedEventsActivity(event.get$id());
                    dismiss();
                }
            }
            // If the event is saved or this is a saved event, start the DF to save the event or change its name
            FragmentManager fragmentManager = getChildFragmentManager();
            SaveEventDf saveEventDf = new SaveEventDf();
            saveEventDf.setArguments(bundle);
            saveEventDf.show(fragmentManager, "SaveEventDf");
            // Set up a listener to be able to know if the event has been saved
            FragmentResultListener listener = new FragmentResultListener() {
                @Override
                public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                    if (requestKey.equals("saved")) {
                        if(bundle.getBoolean("savedEvent")) dismiss();
                        else setupButtons();
                    }
                }
            };
            fragmentManager.setFragmentResultListener("saved", saveEventDf, listener);
        });

        deleteEventPromptButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getChildFragmentManager();
            DeleteEventPromptDf deleteEventPromptDf = new DeleteEventPromptDf();
            Bundle newBundle = new Bundle();
            newBundle.putAll(bundle);
            if(bundle.getBoolean("savedEvent")) newBundle.putString("message",
                    "Are you sure you want to delete this event from the saved database?");
            else newBundle.putString("message", "Are you sure you want to delete this event?");
            deleteEventPromptDf.setArguments(newBundle);
            deleteEventPromptDf.show(fragmentManager, "DeleteEventPromptDf");
            // Set up a listener to be able to know if the event has been saved
            FragmentResultListener listener = new FragmentResultListener() {
                @Override
                public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                    if(requestKey.equals("delete")){
                        if(bundle.getBoolean("savedEvent")) savedEventsController.deleteSavedEvent(savedEvent);
                        else eventsController.deleteEvent(event);
                        dismiss();
                    }
                }
            };
            fragmentManager.setFragmentResultListener("delete", deleteEventPromptDf, listener );
        });

        saveVideoButton.setOnClickListener(v -> {
            System.out.println(bundle.getString("fileId"));
            videoClipController.downloadFile(bundle.getString("fileId"), bundle.getString("eventId"));
        });

        return view;
    }

    private void setupButtons(){
        if(bundle.getBoolean("savedEvent")){
            try {
                savedEvent = new SavedEvent(new JSONObject(bundle.getString("event")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            savedEventPromptButton.setText("Change Event Name");
            deleteEventPromptButton.setText("Delete Saved Event");
        } else {
            try {
                event = new Event(new JSONObject(bundle.getString("event")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // If event isn't saved, change the button text
            if (!event.isSaved()) {
                savedEventPromptButton.setText("Save event");
            } else {
                savedEventPromptButton.setText("Go To Saved Event");
            }
            deleteEventPromptButton.setText("Delete Event");
        }
    }
}
