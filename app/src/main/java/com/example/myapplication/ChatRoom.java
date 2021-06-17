package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatRoom extends AppCompatActivity {

    RecyclerView chatList;
    ArrayList<ChatMessage> messages = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatlayout);

        chatList = findViewById(R.id.myrecycler);
        chatList.setLayoutManager(new LinearLayoutManager(this));

        Button sendButton = findViewById(R.id.button3);
        Button receiveButton = findViewById(R.id.button4);
        EditText editTextMessage = findViewById(R.id.editTextTextPersonName);

        MyChatAdapter adt = new MyChatAdapter();
        chatList.setAdapter(adt);

        sendButton.setOnClickListener(v -> {
            ChatMessage thisMessage = new ChatMessage(editTextMessage.getText().toString(), 1,  new Date() );
            messages.add(thisMessage);
            editTextMessage.setText("");

            adt.notifyItemInserted(messages.size() - 1);
        });
        receiveButton.setOnClickListener(v -> {
            ChatMessage thisMessage = new ChatMessage(editTextMessage.getText().toString(), 2,  new Date() );
            messages.add(thisMessage);
            editTextMessage.setText("");

            adt.notifyItemInserted(messages.size() - 1);
        });

    }

    private class ChatMessage {
        String message;
        int sendOrReceive;
        Date timeSent;

        public ChatMessage(String message, int sendOrReceive, Date timeSent) {
            this.message = message;
            this.sendOrReceive = sendOrReceive;
            this.timeSent = timeSent;
        }

        public String getMessage() {
            return message;
        }

        public int getSendOrReceive() {
            return sendOrReceive;
        }

        public String getTimeSent() {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a", Locale.getDefault());
            String currentDateandTime = sdf.format(timeSent);
            return currentDateandTime;
        }
    }

    private class MyRowViews extends RecyclerView.ViewHolder {

        TextView messageText;
        TextView timeText;

        public MyRowViews(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.message);
            timeText = itemView.findViewById(R.id.time);

        }
    }

    private class MyChatAdapter extends RecyclerView.Adapter<MyRowViews> {

        @Override
        public MyRowViews onCreateViewHolder(ViewGroup parent, int viewType) {
            int layoutID;
            if (viewType == 1) // Send
                layoutID = R.layout.sent_message;
            else
                layoutID = R.layout.receive_message;
            return new MyRowViews(getLayoutInflater().inflate(layoutID, parent, false));
        }

        @Override
        public void onBindViewHolder(MyRowViews holder, int position) {
            holder.messageText.setText(messages.get(position).getMessage());
            holder.timeText.setText(messages.get(position).getTimeSent());
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        @Override
        public int getItemViewType(int position) {
            return messages.get(position).getSendOrReceive();
        }
    }

}
