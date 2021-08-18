package com.example.jacob.inclass10;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ChatroomActivity extends AppCompatActivity implements GetMessagesAsync.IMessage {

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String token;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (getIntent() != null && getIntent().getExtras() != null) {
            thread = (Thread) getIntent().getExtras().getSerializable(ThreadListActivity.THREAD_KEY);
            TextView threadTitle = findViewById(R.id.threadTitle);
            threadTitle.setText(thread.title);
            ImageButton homeButton = findViewById(R.id.homeButton);
            homeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            GetMessagesAsync async = new GetMessagesAsync(this);
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            token = sharedPreferences.getString(getString(R.string.session_token), null);
            async.execute(token, thread.id);
            class Listener implements View.OnClickListener, AddMessageAsync.IAddMessage {

                @Override
                public void onClick(View view) {
                    EditText messageField = findViewById(R.id.messageField);
                    String messageBody = messageField.getText().toString();
                    if (messageBody.length() == 0) {
                        Toast.makeText(ChatroomActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                    } else {
                        AddMessageAsync addMessageAsync = new AddMessageAsync(this);
                        addMessageAsync.execute(messageBody, thread.id, token);
                    }
                }

                @Override
                public void messageAdded(boolean success) {
                    if (success) {
                        Toast.makeText(ChatroomActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                        EditText messageField = findViewById(R.id.messageField);
                        messageField.setText(null);
                        GetMessagesAsync getMessagesAsync = new GetMessagesAsync(ChatroomActivity.this);
                        getMessagesAsync.execute(token, thread.id);
                    } else {
                        Toast.makeText(ChatroomActivity.this, "Could not send message", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            ImageButton sendButton = findViewById(R.id.sendButton);
            sendButton.setOnClickListener(new Listener());
        }
    }

    @Override
    public void handleMessages(MessageList messages) {
        if (messages == null || messages.messages == null || messages.messages.length == 0) {
            Toast.makeText(this, "No messages found", Toast.LENGTH_SHORT).show();
        } else {
            ListView messageList = findViewById(R.id.chatList);
            MessageAdapter adapter = new MessageAdapter(this, R.layout.message_layout, messages.messages);
            messageList.setAdapter(adapter);
        }
    }

    private class MessageAdapter extends ArrayAdapter<Message> {

        MessageAdapter(@NonNull Context context, int resource, @NonNull Message[] objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final Message message = getItem(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_layout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.messageBody = convertView.findViewById(R.id.messageBody);
                viewHolder.userName = convertView.findViewById(R.id.userName);
                viewHolder.timestamp = convertView.findViewById(R.id.timestamp);
                viewHolder.deleteMessageButton = convertView.findViewById(R.id.deleteMessageButton);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE);
            String userId = preferences.getString(getString(R.string.user_id), null);
            if (message.user_id.equals(userId)) {
                viewHolder.deleteMessageButton.setVisibility(View.VISIBLE);
                class Listener implements View.OnClickListener, DeleteMessageAsync.IDeleteMessage {

                    @Override
                    public void onClick(View view) {
                        DeleteMessageAsync async = new DeleteMessageAsync(this);
                        async.execute(token, message.id);
                    }

                    @Override
                    public void handleDeleteResponse(boolean success) {
                        if (success) {
                            Toast.makeText(ChatroomActivity.this, "Message deleted", Toast.LENGTH_SHORT).show();
                            GetMessagesAsync async = new GetMessagesAsync(ChatroomActivity.this);
                            async.execute(token, thread.id);
                        } else {
                            Toast.makeText(ChatroomActivity.this, "Could not delete message", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                viewHolder.deleteMessageButton.setOnClickListener(new Listener());
                viewHolder.userName.setText("Me");
            } else {
                viewHolder.userName.setText(message.user_fname + " " + message.user_lname);
            }
            viewHolder.messageBody.setText(message.message);
            try {
                Date date = formatter.parse(message.created_at);
                PrettyTime prettyTime = new PrettyTime();
                viewHolder.timestamp.setText(prettyTime.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }

    private class ViewHolder {
        TextView messageBody, userName, timestamp;
        ImageButton deleteMessageButton;
    }
}
