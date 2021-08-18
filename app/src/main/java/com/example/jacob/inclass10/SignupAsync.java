package com.example.jacob.inclass10;

/*
In-class Assignment 10
Group 21 - Jacob Stern and Prayas Rode
*/

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupAsync extends AsyncTask<String, Void, SessionInfo> {

    private final OkHttpClient httpClient = new OkHttpClient();
    private ISignup iSignup;

    SignupAsync(ISignup iSignup) {
        this.iSignup = iSignup;
    }

    @Override
    protected SessionInfo doInBackground(String... strings) {
        String firstName = strings[0];
        String lastName = strings[1];
        String email = strings[2];
        String password = strings[3];
        RequestBody body = new FormBody.Builder().add("fname", firstName)
                .add("lname", lastName)
                .add("email", email)
                .add("password", password)
                .build();
        Request request = new Request.Builder().url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/signup")
                .post(body).build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Gson gson = new Gson();
                return gson.fromJson(responseBody, SessionInfo.class);
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(SessionInfo info) {
        iSignup.handleSignup(info);
    }

    interface ISignup {
        void handleSignup(SessionInfo info);
    }
}
