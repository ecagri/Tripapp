package com.cagri.tripapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessagesPersonFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private StorageReference storageRef = storage.getReference();

    View view;

    Context context ;

    MessageRecyclerAdapter messageRecyclerAdapter;

    ArrayList<Message> messages;

    RecyclerView recyclerView;
    String sender ;
    String reciever;
    public MessagesPersonFragment(String sender,String reciever){
        this.sender = sender;
        this.reciever=reciever;
    }

    public static VisitProfileFragment newInstance(String param1, String param2) {
        VisitProfileFragment fragment = new VisitProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_messages_person, container, false);
        context=getContext();

        ImageButton message_send = view.findViewById(R.id.message_send);
        TextInputLayout message_text = view.findViewById(R.id.message_input);
        RecyclerView message_recyclerView = view.findViewById(R.id.message_recyclerView);

        viewSettings();
        fillTheArray();
        messageRecyclerAdapter.notifyDataSetChanged();

        message_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=message_text.getEditText().getText().toString();
                Map<String, Object> message = new HashMap<>();
                message.put("sender",reciever);
                message.put("reciever",sender);
                message.put("text",text);
                message.put("date", LocalDateTime.now().toString());
                message.put("seen",false);
                message.put("liked",false);

                db.collection("users").document(reciever).update("messages", FieldValue.arrayUnion(message)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "mesajınız gönderildi", Toast.LENGTH_SHORT).show();
                    }
                });

                db.collection("users").document(sender).update("messages", FieldValue.arrayUnion(message)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "mesajınız alındı", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        return view;
    }


    private void viewSettings(){
        recyclerView = view.findViewById(R.id.message_recyclerView);
        messages= new ArrayList<>();
        messageRecyclerAdapter = new MessageRecyclerAdapter(messages);
        messageRecyclerAdapter.setView(view);
        recyclerView.setAdapter(messageRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    private void fillTheArray(){
        db.collection("users").document(reciever).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                ArrayList<Map<String,Object>> msg= (ArrayList<Map<String,java.lang.Object>>)  value.getData().get("messages");
                for(Map<String,Object> m : msg){
                    if(m.get("sender").equals(sender) && m.get("reciever").equals(reciever)){
                        messages.add(new Message("->"+m.get("text").toString()));
                    }
                    if(m.get("sender").equals(reciever) && m.get("reciever").equals(sender)){
                        messages.add(new Message("you:"+m.get("text").toString()));
                    }
                    if(m.get("sender").equals(sender) && m.get("sender").equals(reciever)){
                        messages.clear();
                    }

                }

            }
        });
    }
}
