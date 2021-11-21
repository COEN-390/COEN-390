package com.coen390.maskdetector.controllers;

import android.content.Context;
import android.util.Log;

import com.coen390.maskdetector.DevicesActivity;
import com.coen390.maskdetector.DevicesRecyclerViewAdapter;
import com.coen390.maskdetector.models.Device;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.appwrite.Client;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.services.Database;
import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import okhttp3.Response;

public class DevicesController {
    private Context context;
    private Client client;
    private Database db;

    public DevicesController(Context context) {
        this.context = context;
        this.client = AppwriteController.getClient(context);
        this.db = new Database(this.client);
    }

    public void getDeviceList(DevicesRecyclerViewAdapter devicesRecyclerViewAdapter, DevicesActivity devicesActivity) throws AppwriteException {
        db.listDocuments(
                "61896dfa87e44",
                new Continuation<Object>() {
                    @NotNull
                    @Override
                    public CoroutineContext getContext() {
                        return EmptyCoroutineContext.INSTANCE;
                    }

                    @Override
                    public void resumeWith(@NotNull Object o) {
                        String json = "";
                        try {
                            if (o instanceof Result.Failure) {
                                Result.Failure failure = (Result.Failure) o;
                                throw failure.exception;
                            } else {
                                Response response = (Response) o;
                                json = response.body().string();
                                JSONObject payload = new JSONObject(json);
                                JSONArray documentList = payload.getJSONArray("documents");
                                // Initialize the list of devices from JSON
                                List<Device> devices = new ArrayList<>();
                                for (int i = 0; i < documentList.length(); i ++) {
                                    JSONObject currentObject = documentList.getJSONObject(i);
                                    Device newDevice = new Device(currentObject.getString("deviceId"), currentObject.getString("organizationId"), currentObject.getDouble("healthTimestamp"));
                                    devices.add(newDevice);
                                }
                                devicesActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        devicesRecyclerViewAdapter.setDeviceList(devices);
                                        devicesRecyclerViewAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        } catch (Throwable th) {
                            Log.e("ERROR", th.toString());
                        }
                    }
                }
        );
    }
}
