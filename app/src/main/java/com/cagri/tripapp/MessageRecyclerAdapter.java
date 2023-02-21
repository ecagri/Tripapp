package com.cagri.tripapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerAdapter.MyViewHolder>{

    private ArrayList<Message> messages;
    private View view ;

    public void setView(View view) {
        this.view = view;
    }

    public MessageRecyclerAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_messages_person_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageRecyclerAdapter.MyViewHolder holder, int position) {
        holder.message_text.setText(messages.get(position).getText());
        holder.message_date.setText(messages.get(position).getDate());

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView message_text,message_date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            message_text= itemView.findViewById(R.id.message_text);
            message_date = itemView.findViewById(R.id.message_date);


        }
    }
}
