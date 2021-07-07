package com.example.myapplication;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatRoom extends AppCompatActivity {

    RecyclerView chatList;
    ArrayList<ChatMessage> messages = new ArrayList<>();
    MyChatAdapter adt;
    SQLiteDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatlayout);

        chatList = findViewById(R.id.myrecycler);
        chatList.setLayoutManager(new LinearLayoutManager(this));

        Button sendButton = findViewById(R.id.button3);
        Button receiveButton = findViewById(R.id.button4);
        EditText editTextMessage = findViewById(R.id.editTextTextPersonName);

        adt = new MyChatAdapter();

        MyOpenHelper opener = new MyOpenHelper( this );
        db = opener.getWritableDatabase();

        // Reading from the database
        Cursor results = db.rawQuery("SELECT * FROM " + MyOpenHelper.TABLE_NAME + ";", null);

        int idIndex = results.getColumnIndex("_id");
        int messageIndex = results.getColumnIndex(MyOpenHelper.COL_MESSAGE);
        int sendOrReceiveIndex = results.getColumnIndex(MyOpenHelper.COL_SEND_OR_RECEIVE);
        int timeSentIndex = results.getColumnIndex(MyOpenHelper.COL_TIME_SENT);

        while (results.moveToNext()) {
            long id = results.getLong(idIndex);
            String message = results.getString(messageIndex);
            int sentOrReceive = results.getInt(sendOrReceiveIndex);
            String time = results.getString(timeSentIndex);
            messages.add(new ChatMessage(id, message, sentOrReceive, time));
        }
        chatList.setAdapter(adt);

        sendButton.setOnClickListener(v -> {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a", Locale.getDefault());
            String date = sdf.format(new Date());
            ChatMessage thisMessage = new ChatMessage(editTextMessage.getText().toString(), 1, date);
            messages.add(thisMessage);
            editTextMessage.setText("");

            // Add to database
            ContentValues newRow = new ContentValues();
            newRow.put(MyOpenHelper.COL_MESSAGE, thisMessage.getMessage());
            newRow.put(MyOpenHelper.COL_SEND_OR_RECEIVE, thisMessage.getSendOrReceive());
            newRow.put(MyOpenHelper.COL_TIME_SENT, thisMessage.getTimeSent());
            long newId = db.insert(MyOpenHelper.TABLE_NAME, MyOpenHelper.COL_MESSAGE, newRow);
            thisMessage.setId(newId);

            adt.notifyItemInserted(messages.size() - 1);
        });
        receiveButton.setOnClickListener(v -> {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a", Locale.getDefault());
            String date = sdf.format(new Date());
            ChatMessage thisMessage = new ChatMessage(editTextMessage.getText().toString(), 2, date);
            messages.add(thisMessage);
            editTextMessage.setText("");

            // Add to database
            ContentValues newRow = new ContentValues();
            newRow.put(MyOpenHelper.COL_MESSAGE, thisMessage.getMessage());
            newRow.put(MyOpenHelper.COL_SEND_OR_RECEIVE, thisMessage.getSendOrReceive());
            newRow.put(MyOpenHelper.COL_TIME_SENT, thisMessage.getTimeSent());
            long newId = db.insert(MyOpenHelper.TABLE_NAME, MyOpenHelper.COL_MESSAGE, newRow);
            thisMessage.setId(newId);

            adt.notifyItemInserted(messages.size() - 1);
        });


    }

    private class ChatMessage {
        long id;
        String message;
        int sendOrReceive;
        String timeSent;

        public ChatMessage(String message, int sendOrReceive, String timeSent) {
            this.message = message;
            this.sendOrReceive = sendOrReceive;
            this.timeSent = timeSent;
        }

        public ChatMessage(long id, String message, int sendOrReceive, String timeSent) {
            this.id = id;
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
            return timeSent;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    private class MyRowViews extends RecyclerView.ViewHolder {

        TextView messageText;
        TextView timeText;
        int position = -1;

        public MyRowViews(View itemView) {
            super(itemView);

            itemView.setOnClickListener(v -> {

                new AlertDialog.Builder(ChatRoom.this)
                        .setMessage("Do you want to delete the message: " + messageText.getText())
                        .setTitle("Question:")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", (dialog, which) -> {

                            position = getAdapterPosition();

                            ChatMessage removedMessage = messages.get(position);
                            Snackbar.make(messageText, "You deleted message #" + position, Snackbar.LENGTH_LONG)
                                    .setAction("Undo", clk -> {
                                        messages.add(position, removedMessage);
                                        adt.notifyItemInserted(position);

                                        db.execSQL("INSERT INTO " + MyOpenHelper.TABLE_NAME + " VALUES('" +
                                                removedMessage.getId() + "','" +
                                                removedMessage.getMessage() + "','" +
                                                removedMessage.getSendOrReceive() + "','" +
                                                removedMessage.getTimeSent() + "');");
                                    })
                                    .show();


                            messages.remove(position);
                            adt.notifyItemRemoved(position);

                            // Delete from database
                            db.delete(MyOpenHelper.TABLE_NAME, "_id=?", new String[]{Long.toString(removedMessage.getId())});
                        })
                        .create().show();


            });

            messageText = itemView.findViewById(R.id.message);
            timeText = itemView.findViewById(R.id.time);

        }

        public void setPosition(int position) {
            this.position = position;
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
            holder.setPosition(position);
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
