package com.example.jacob.inclass10;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddThreadAsync extends AsyncTask<String, Void, Boolean> {

    private final OkHttpClient httpClient = new OkHttpClient();
    private IAddThread iAddThread;

    AddThreadAsync(IAddThread iAddThread) {
        this.iAddThread = iAddThread;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        String token = strings[0];
        String title = strings[1];
        RequestBody body = new FormBody.Builder()
                .add("title", title)
                .build();
        Request request = new Request.Builder()
                .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/thread/add")
                .post(body)
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
        iAddThread.threadAdded(aBoolean);
    }

    interface IAddThread {
        void threadAdded(boolean success);
    }
}
