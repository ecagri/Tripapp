package com.cagri.tripapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UserRcyclerAdapter extends RecyclerView.Adapter<UserRcyclerAdapter.MyViewHolder> {

    private ArrayList<User> users;
    private  View view ;

    public void setView(View view) {
        this.view = view;
    }

    public UserRcyclerAdapter(ArrayList<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_messages_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.message_pname.setText(users.get(position).getUsername()) ;
        holder.message_pmessage.setText(users.get(position).getLast_message()) ;
        //holder.message_pp.set(users.get(position).getProfile_picture());
        Glide.with(view).load(users.get(position).getProfile_picture()).into(holder.message_pp);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView message_pmessage,message_pname;
        ImageView message_pp;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            message_pname= itemView.findViewById(R.id.message_pname);
            message_pmessage = itemView.findViewById(R.id.message_pmessage);
            message_pp=itemView.findViewById(R.id.message_pp);
        }
    }
}
