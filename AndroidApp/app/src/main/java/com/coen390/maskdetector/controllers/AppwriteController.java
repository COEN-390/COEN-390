package com.coen390.maskdetector.controllers;

import android.content.Context;

import io.appwrite.Client;
import io.appwrite.services.Account;
import io.appwrite.services.Database;

public class AppwriteController {
    private static String endpoint = "https://appwrite.orpine.net/v1";
    private static String project = "6137a2ef0d4f5";

    public static Client getClient(Context context) {
        return new Client(context)
                .setEndpoint(endpoint)
                .setProject(project);
    }

}
