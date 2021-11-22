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

public class VideoClipController {
    private Context context;
    private Client client;
    private Storage storage;

    public VideoClipController(Context context) {
        this.context = context;
        this.client = AppwriteController.getClient(context);
        this.storage = new Storage(this.client);
    }

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
//                            File file = new File(context.getFilesDir(), fileName);
//                            try (FileOutputStream fos = context.openFileOutput(fileName + ".mp4", Context.MODE_PRIVATE)) {
//                                fos.write(data);
//                            }
//                            MediaPlayer mp = new MediaPlayer();
//                            mp.setDataSource(context.getFilesDir() + "/" + fileName + ".mp4");
//                            mp.prepare();
//                            mp.start();
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

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private void saveData(byte[] data, String fileName){

        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        root = new File(root, "MaskDetector");
        root.mkdir();
        root = new File(root , fileName);

        try {
            FileOutputStream fileOutput = new FileOutputStream(root);
            fileOutput.write(data);
            fileOutput.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            boolean bool = false;
            try {
                // try to create the file
                bool = root.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (bool){
                // call the method again
                saveData(data, fileName);
            }else {
                throw new IllegalStateException("Failed to create image file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        openFile(root);
    }

    public void openFile(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        intent.setDataAndType(uri, "video/mp4");
        context.startActivity(intent);
    }

}
