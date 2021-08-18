package com.example.jacob.inclass10;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddMessageAsync extends AsyncTask<String, Void, Boolean> {

    private final OkHttpClient httpClient = new OkHttpClient();
    private IAddMessage iAddMessage;

    AddMessageAsync(IAddMessage iAddMessage) {
        this.iAddMessage = iAddMessage;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        String messageBody = strings[0];
        String threadId = strings[1];
        String token = strings[2];
        RequestBody body = new FormBody.Builder()
                .add("message", messageBody)
                .add("thread_id", threadId)
                .build();
        Request request = new Request.Builder()
                .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/message/add")
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
        iAddMessage.messageAdded(aBoolean);
    }

    interface IAddMessage {
        void messageAdded(boolean success);
    }
}
