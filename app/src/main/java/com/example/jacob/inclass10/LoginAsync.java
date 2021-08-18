package com.example.jacob.inclass10;

/*
In-class Assignment 10
Group 21 - Jacob Stern and Prayas Rode
*/

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginAsync extends AsyncTask<String, Void, SessionInfo> {

    private final OkHttpClient httpClient = new OkHttpClient();
    private ILogin iLogin;

    LoginAsync(ILogin iLogin) {
        this.iLogin = iLogin;
    }

    @Override
    protected SessionInfo doInBackground(String... strings) {
        SessionInfo info;
        String email = strings[0];
        String password = strings[1];
        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/login")
                .post(body)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Gson gson = new Gson();
                info = gson.fromJson(responseBody, SessionInfo.class);
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
        return info;
    }

    @Override
    protected void onPostExecute(SessionInfo sessionInfo) {
        iLogin.handleLogin(sessionInfo);
    }

    interface ILogin {
        void handleLogin(SessionInfo info);
    }
}
