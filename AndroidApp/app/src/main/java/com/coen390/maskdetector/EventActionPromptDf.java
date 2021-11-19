package com.coen390.maskdetector;

import android.content.Context;
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
import com.coen390.maskdetector.models.Event;
import com.google.android.gms.common.util.Hex;

import org.json.JSONException;
import org.json.JSONObject;

public class EventActionPromptDf extends DialogFragment {

    private Button savedEventPromptButton, cancelPromptButton, deleteEventPromptButton, deleteSavedEventPromptButton;
    private EventsController eventsController;
    private SavedEventsController savedEventsController;
    private Event event;
    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.df_event_action_prompt, container);

        savedEventPromptButton = view.findViewById(R.id.buttonSavedEventPrompt);
        cancelPromptButton = view.findViewById(R.id.buttonCancelPrompt);
        deleteEventPromptButton = view.findViewById(R.id.buttonDeleteEventPrompt);
        deleteSavedEventPromptButton = view.findViewById(R.id.buttonDeleteSavedEventPrompt);

        bundle = getArguments();

        setupButtons();

        cancelPromptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        savedEventPromptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(event.isSaved()){
                    // If event is saved, go to saved events and highlight it
                    ((MainActivity) requireActivity()).goToSavedEventsActivity(event.get$id());
                    dismiss();
                }
                else{
                    // Start the DF to save the event
                    FragmentManager fragmentManager = getChildFragmentManager();
                    SaveEventDf saveEventDf = new SaveEventDf();
                    saveEventDf.setArguments(bundle);
                    saveEventDf.show(fragmentManager, "SaveEventDf");
                    // Set up a listener to be able to know if the event has been saved
                    FragmentResultListener listener = new FragmentResultListener() {
                        @Override
                        public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                            if(requestKey.equals("saved")){
                                setupButtons();
                            }
                        }
                    };
                    fragmentManager.setFragmentResultListener("saved", saveEventDf, listener );
                }
            }
        });

        return view;
    }

    private void setupButtons(){
        try {
            event = new Event(new JSONObject(bundle.getString("event")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // If event isn't saved, disable the deletion of saved event
        if (!event.isSaved()) {
            deleteSavedEventPromptButton.setEnabled(false);
            savedEventPromptButton.setText("Save event");
        } else {
            deleteSavedEventPromptButton.setEnabled(true);
            savedEventPromptButton.setText("Go To Saved Event");
        }
    }
}
