package com.coen390.maskdetector.ui.eventlog;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coen390.maskdetector.EventsRecyclerViewAdapter;
import com.coen390.maskdetector.R;
import com.coen390.maskdetector.controllers.EventsController;
import com.coen390.maskdetector.databinding.EventLogFragmentBinding;
import com.coen390.maskdetector.databinding.FragmentHomeBinding;
import com.coen390.maskdetector.models.Event;

import java.util.ArrayList;

public class EventLogFragment extends Fragment {

    private EventLogViewModel mViewModel;

    public static EventLogFragment newInstance() {
        return new EventLogFragment();
    }

    private EventLogFragmentBinding binding;

    private EventsRecyclerViewAdapter eventsRecyclerViewAdapter;
    private EventsController eventsController;
    private RecyclerView eventsRecyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = EventLogFragmentBinding.inflate(inflater, container, false);

        eventsController = new EventsController(getContext());
        setupRecyclerView();

        return inflater.inflate(R.layout.event_log_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EventLogViewModel.class);
        // TODO: Use the ViewModel
    }

    private void setupRecyclerView() {
        eventsRecyclerView = binding.eventsRecyclerView;
        eventsRecyclerViewAdapter = new EventsRecyclerViewAdapter(getContext());
        eventsController.getEventsList(eventsRecyclerViewAdapter, this.getActivity(), new ArrayList<Event>());

        // Create layout manager and dividers between items of the view holder
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(eventsRecyclerView.getContext(),
                linearLayoutManager.getOrientation());

        eventsRecyclerView.setLayoutManager(linearLayoutManager);
        eventsRecyclerView.addItemDecoration(dividerItemDecoration);
        eventsRecyclerView.setAdapter(eventsRecyclerViewAdapter);
    }

}