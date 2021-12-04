package com.coen390.maskdetector.controllers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.appwrite.Client;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.services.Storage;
import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import okhttp3.Response;

/**
 * Controller used for Saved Events Monitoring
 */
public class VideoClipController {
    private Context context;
    private Client client;
    private Storage storage;

    public VideoClipController(Context context) {
        this.context = context;
        this.client = AppwriteController.getClient(context);
        this.storage = new Storage(this.client);
    }

    /**
     * Method used to Download the video file pressed on
     * @param fileId
     * @param fileName
     */
    public void downloadFile(String fileId, String fileName) {
        try {
            storage.getFileDownload(
                    fileId,
            new Continuation<Object>() {
                @NotNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NotNull Object o) {
                    try {
                        if (o instanceof Result.Failure) {
                            Result.Failure failure = (Result.Failure) o;
                            throw failure.exception;
                        } else {
                            Response response = (Response) o;
                            byte[] data = response.body().bytes();
                            saveData(data, fileName + ".mp4");
                        }
                    } catch (AppwriteException e) {
                        e.printStackTrace();
                        System.out.println(e.getResponse());
                    } catch (Throwable th) {
                        Log.e("ERROR", th.toString());
                    }
                }
            }
            );
        } catch (AppwriteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to check if the environment is mounted
     * @return
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Method to check if the environment is mounted for Read only
     * @return
     */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Method used to save a file
     * @param data
     * @param fileName
     */
    private void saveData(byte[] data, String fileName){

        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        file = new File(file, "MaskDetector");
        file.mkdir();
        file = new File(file , fileName);

        try {
            FileOutputStream fileOutput = new FileOutputStream(file);
            fileOutput.write(data);
            fileOutput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            boolean success = false;
            try {
                // try creating a file
                success = file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (success){
                // try saving again
                saveData(data, fileName);
            } else {
                throw new IllegalStateException("Failed to save video");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // open the video in the media player
        openFile(file);
    }

    /**
     * Method used to open a file
     * @param file
     */
    public void openFile(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        intent.setDataAndType(uri, "video/mp4");
        context.startActivity(intent);
    }

}
