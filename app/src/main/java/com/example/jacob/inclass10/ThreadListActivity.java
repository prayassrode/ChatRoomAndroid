package com.example.jacob.inclass10;

/*
In-class Assignment 10
Group 21 - Jacob Stern and Prayas Rode
*/

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ThreadListActivity extends AppCompatActivity implements GetThreadsAsync.IThreads {

    public final static String THREAD_KEY = "thread";

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_list);
        ImageButton logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key),
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.session_token), null);
                editor.putString(getString(R.string.user_id), null);
                editor.putString(getString(R.string.user_first_name), null);
                editor.putString(getString(R.string.user_last_name), null);
                editor.apply();
                Intent intent = new Intent(ThreadListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        GetThreadsAsync getThreadsAsync = new GetThreadsAsync(this);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        token = sharedPreferences.getString(getString(R.string.session_token), null);
        String firstName = sharedPreferences.getString(getString(R.string.user_first_name), null);
        String lastName = sharedPreferences.getString(getString(R.string.user_last_name), null);
        TextView user = findViewById(R.id.userName);
        user.setText(firstName + " " + lastName);
        ImageButton createButton = findViewById(R.id.addThreadButton);
        class Listener implements View.OnClickListener, AddThreadAsync.IAddThread {

            @Override
            public void onClick(View view) {
                EditText threadNameField = findViewById(R.id.threadNameField);
                String title = threadNameField.getText().toString();
                if (title.length() == 0) {
                    Toast.makeText(ThreadListActivity.this, "Please enter a thread title", Toast.LENGTH_SHORT).show();
                } else {
                    AddThreadAsync async = new AddThreadAsync(this);
                    async.execute(token, title);
                }
            }

            @Override
            public void threadAdded(boolean success) {
                if (success) {
                    Toast.makeText(ThreadListActivity.this, "Thread added", Toast.LENGTH_SHORT).show();
                    EditText threadNameField = findViewById(R.id.threadNameField);
                    threadNameField.setText(null);
                    GetThreadsAsync async = new GetThreadsAsync(ThreadListActivity.this);
                    async.execute(token);
                } else {
                    Toast.makeText(ThreadListActivity.this, "Could not add thread", Toast.LENGTH_SHORT).show();
                }
            }
        }
        createButton.setOnClickListener(new Listener());
        getThreadsAsync.execute(token);
    }

    @Override
    public void handleThreads(final ThreadList threads) {
        if (threads == null || threads.threads == null || threads.threads.length == 0) {
            Toast.makeText(this, "No threads found", Toast.LENGTH_SHORT).show();
        } else {
            ListView threadList = findViewById(R.id.threadList);
            ThreadAdapter adapter = new ThreadAdapter(this, R.layout.thread_list_layout, threads.threads);
            threadList.setAdapter(adapter);
            threadList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Intent intent = new Intent(ThreadListActivity.this, ChatroomActivity.class);
                    intent.putExtra(THREAD_KEY, threads.threads[position]);
                    startActivity(intent);
                }
            });
        }
    }

    private class ThreadAdapter extends ArrayAdapter<Thread> {

        ThreadAdapter(@NonNull Context context, int resource, @NonNull Thread[] objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final Thread thread = getItem(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.thread_list_layout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title = convertView.findViewById(R.id.threadTitle);
                viewHolder.deleteThreadButton = convertView.findViewById(R.id.deleteThreadButton);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.title.setText(thread.title);
            final SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE);
            String userId = preferences.getString(getString(R.string.user_id), null);
            if (thread.user_id.equals(userId)) {
                viewHolder.deleteThreadButton.setVisibility(View.VISIBLE);
                class Listener implements View.OnClickListener, DeleteThreadAsync.IDeleteThread {

                    @Override
                    public void onClick(View view) {
                        DeleteThreadAsync async = new DeleteThreadAsync(this);
                        async.execute(token, thread.id);
                    }

                    @Override
                    public void handleDeleteResponse(boolean success) {
                        if (success) {
                            Toast.makeText(ThreadListActivity.this, "Thread deleted", Toast.LENGTH_SHORT).show();
                            GetThreadsAsync async = new GetThreadsAsync(ThreadListActivity.this);
                            async.execute(token);
                        } else {
                            Toast.makeText(ThreadListActivity.this, "Could not delete thread", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                viewHolder.deleteThreadButton.setOnClickListener(new Listener());
            }
            return convertView;
        }
    }

    private class ViewHolder {
        TextView title;
        ImageButton deleteThreadButton;
    }
}
