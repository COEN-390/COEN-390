package com.coen390.maskdetector;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DeleteEventPromptDf extends DialogFragment {

    private TextView deleteEventMessage;
    private Button deleteEventButton, cancelDeleteEventButton;
    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.df_delete_event_prompt, container);

        deleteEventMessage = view.findViewById(R.id.deleteEventMessage);
        deleteEventButton = view.findViewById(R.id.buttonDeleteEvent);
        cancelDeleteEventButton = view.findViewById(R.id.buttonCancelDeleteEvent);

        bundle = getArguments();
        deleteEventMessage.setText(bundle.getString("message"));

        cancelDeleteEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        deleteEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send the user's response back to the listener
                getParentFragmentManager().setFragmentResult("delete", bundle);
                dismiss();
            }
        });

        return view;
    }
}
