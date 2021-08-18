package com.example.jacob.inclass10;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetThreadsAsync extends AsyncTask<String, Void, ThreadList> {

    private final OkHttpClient httpClient = new OkHttpClient();
    private IThreads iThreads;

    GetThreadsAsync(IThreads iThreads) {
        this.iThreads = iThreads;
    }

    @Override
    protected ThreadList doInBackground(String... strings) {
        String token = strings[0];
        Request request = new Request.Builder()
                .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/thread")
                .addHeader("Authorization", "BEARER " + token)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Gson gson = new Gson();
                return gson.fromJson(responseBody, ThreadList.class);
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(ThreadList threadList) {
        iThreads.handleThreads(threadList);
    }

    interface IThreads {
        void handleThreads(ThreadList threads);
    }
}
