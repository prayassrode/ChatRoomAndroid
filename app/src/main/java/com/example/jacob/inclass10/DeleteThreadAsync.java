package com.example.jacob.inclass10;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DeleteThreadAsync extends AsyncTask<String, Void, Boolean> {

    private final OkHttpClient httpClient = new OkHttpClient();
    private IDeleteThread iDeleteThread;

    DeleteThreadAsync(IDeleteThread iDeleteThread) {
        this.iDeleteThread = iDeleteThread;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        String token = strings[0];
        String threadId = strings[1];
        Request request = new Request.Builder()
                .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/thread/delete/" + threadId)
                .addHeader("Authorization", "BEARER " + token)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        iDeleteThread.handleDeleteResponse(aBoolean);
    }

    interface IDeleteThread {
        void handleDeleteResponse(boolean success);
    }
}
