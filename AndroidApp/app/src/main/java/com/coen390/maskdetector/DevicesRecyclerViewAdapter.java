package com.coen390.maskdetector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coen390.maskdetector.controllers.AppwriteController;
import com.coen390.maskdetector.models.Device;

import java.util.ArrayList;
import java.util.List;

import io.appwrite.Client;
import io.appwrite.services.Database;

public class DevicesRecyclerViewAdapter extends RecyclerView.Adapter<DevicesRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Device> devices;
    private Client client;
    private Database db;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView deviceIdTextView;
        private final TextView deviceHealthTextView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            deviceIdTextView = (TextView) view.findViewById(R.id.deviceId);
            deviceHealthTextView = (TextView) view.findViewById(R.id.deviceHealth);

        }

        public TextView getDeviceIdTextView() {
            return deviceIdTextView;
        }
        public TextView getDeviceHealthTextView() {
            return deviceHealthTextView;
        }

    }

    /**
     * Initialize the dataset of the Adapter.
     */
    public DevicesRecyclerViewAdapter(Context context) {
        this.context = context;
        this.client = AppwriteController.getClient(context);
        this.db = new Database(this.client);
        this.devices = new ArrayList<>();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.devices_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getDeviceIdTextView().setText(devices.get(position).getDeviceId());
        long timestamp = System.currentTimeMillis() / 1000;
        double healthTimestamp = devices.get(position).getHealthCheckTimestamp();
        boolean healthy = (timestamp - healthTimestamp) < 240;
        viewHolder.getDeviceHealthTextView().setText(healthy ? "ONLINE" : "OFFLINE");
        viewHolder.getDeviceHealthTextView().setTextColor(healthy ? 0xff00ff00 : 0xffff0000);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void setDeviceList(List<Device> deviceList) {
        devices = deviceList;
    }
}
