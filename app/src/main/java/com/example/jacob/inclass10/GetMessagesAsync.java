package com.example.jacob.inclass10;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetMessagesAsync extends AsyncTask<String, Void, MessageList> {

    private final OkHttpClient httpClient = new OkHttpClient();
    private IMessage iMessage;

    GetMessagesAsync(IMessage iMessage) {
        this.iMessage = iMessage;
    }

    @Override
    protected MessageList doInBackground(String... strings) {
        String token = strings[0];
        String threadId = strings[1];
        Request request = new Request.Builder()
                .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/messages/" + threadId)
                .addHeader("Authorization", "BEARER " + token)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Gson gson = new Gson();
                return gson.fromJson(responseBody, MessageList.class);
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(MessageList messageList) {
        iMessage.handleMessages(messageList);
    }

    interface IMessage {
        void handleMessages(MessageList messages);
    }
}
