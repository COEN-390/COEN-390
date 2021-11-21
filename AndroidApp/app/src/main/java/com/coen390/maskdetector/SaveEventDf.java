package com.coen390.maskdetector;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.coen390.maskdetector.controllers.EventsController;
import com.coen390.maskdetector.controllers.SavedEventsController;
import com.coen390.maskdetector.models.Event;

import org.json.JSONException;
import org.json.JSONObject;

public class SaveEventDf extends DialogFragment {

    private EditText editSaveEventName;
    private Button saveEventButton, cancelSaveEventButton;
    private SavedEventsController savedEventsController;
    private EventsController eventsController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.df_save_event, container);

        editSaveEventName = view.findViewById(R.id.editSaveEventName);

        saveEventButton = view.findViewById(R.id.buttonSaveEvent);
        cancelSaveEventButton = view.findViewById(R.id.buttonCancelSaveEvent);

        savedEventsController = new SavedEventsController(requireActivity().getApplicationContext());
        eventsController = new EventsController(requireActivity().getApplicationContext());

        saveEventButton.setOnClickListener(view1 -> {
            // Temporary save text
            String name = editSaveEventName.getText().toString();

            if(name.equals("")){
                Toast.makeText(getContext(), "Invalid name", Toast.LENGTH_LONG).show();
                return;
            }

            // If name is good, proceed to send it to the database
            Bundle bundle = getArguments();
            try {
                Event event = new Event(new JSONObject(bundle.getString("event")));
                event.setSaved(true);
                savedEventsController.createSavedEvent(name, event);
                eventsController.updateEvent(event);
                // Send the event back to the listener
                bundle.clear();
                bundle.putString("event", event.toString());
                getParentFragmentManager().setFragmentResult("saved", bundle);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(getContext(), "Event saved", Toast.LENGTH_LONG).show();
            dismiss();
        });

        cancelSaveEventButton.setOnClickListener(view12 -> dismiss());

        return view;
    }
}
