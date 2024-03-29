package com.cagri.tripapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessagesFragment extends Fragment implements RecyclerViewInterface{

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

    private View view;

    ArrayList<User> users;
    RecyclerView recyclerView;
    UserRcyclerAdapter userRcyclerAdapter;


    public MessagesFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessagesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessagesFragment newInstance(String param1, String param2) {
        MessagesFragment fragment = new MessagesFragment();
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
        view =inflater.inflate(R.layout.fragment_messages, container, false);
        viewSettings();
        fillTheArray();
        userRcyclerAdapter.notifyDataSetChanged();

        return view;
    }


    private void viewSettings(){
        recyclerView = view.findViewById(R.id.recycler_view);
        users= new ArrayList<>();
        userRcyclerAdapter = new UserRcyclerAdapter(users,this);
        userRcyclerAdapter.setView(view);
        recyclerView.setAdapter(userRcyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    private void fillTheArray(){

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                      for (QueryDocumentSnapshot document : task.getResult()){
                          String uid= document.get("uid").toString();
                          String username =document.get("username").toString();
                          String profile_picture = document.get("profile_pic").toString();
                          if(profile_picture.equals("")){
                              profile_picture="https://firebasestorage.googleapis.com/v0/b/tripapp-40c61.appspot.com/o/userfoto.png?alt=media&token=0e437092-8727-4b7f-8c5b-aea476540d38";
                          }
                          users.add(new User(profile_picture,username,"sa",uid));

                          }
                      userRcyclerAdapter.notifyDataSetChanged();

                }

            }
        });
    }

    @Override
    public void onItemClick(int position) {

        MessagesPersonFragment fragment = new MessagesPersonFragment(users.get(position).getUid(),mAuth.getCurrentUser().getUid());
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment, "message_person");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}