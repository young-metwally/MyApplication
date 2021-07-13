package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MessageDetailsFragment extends Fragment {

    MessageListFragment.ChatMessage chosenMessage;
    int chosenPosition;

    public MessageDetailsFragment(MessageListFragment.ChatMessage chosenMessage, int chosenPosition) {
        this.chosenMessage = chosenMessage;
        this.chosenPosition = chosenPosition;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View detailsView = inflater.inflate(R.layout.details_layout, container, false);

        TextView messageView = detailsView.findViewById(R.id.messageView);
        TextView sendView = detailsView.findViewById(R.id.sendView);
        TextView timeView = detailsView.findViewById(R.id.timeView);
        TextView idView = detailsView.findViewById(R.id.idView);

        messageView.setText("Message is: " + chosenMessage.getMessage());
        sendView.setText("Send or Receive?: " + chosenMessage.getSendOrReceive());
        timeView.setText("Time send: " + chosenMessage.getTimeSent());
        idView.setText("Database id is: " + chosenMessage.getId());

        Button deleteButton = detailsView.findViewById(R.id.button5);
        deleteButton.setOnClickListener(v -> {
            ChatRoom parentActivity = (ChatRoom) getContext();
            parentActivity.notifyMessageDeleted(chosenMessage, chosenPosition);

            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        });
        Button closeButton = detailsView.findViewById(R.id.button6);
        closeButton.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        });

        return detailsView;
    }

}
