package com.cagri.tripapp;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private BottomNavigationView bottomNavigationView;
    private View view;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    public NotificationsFragment(BottomNavigationView bottomNavigationView){
        this.bottomNavigationView = bottomNavigationView;

        FirebaseFirestore.getInstance().collection("users").document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                ArrayList<Map<String, Object>> followers = (ArrayList<Map<String, Object>>) documentSnapshot.get("followers");

                if(followers != null){
                    for(int i = 0; i < followers.size(); i++){
                        if((boolean) followers.get(i).get("seen") == false){
                            bottomNavigationView.getOrCreateBadge(R.id.Notifications);

                        }
                    }
                }
            }
        });
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
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
        view = inflater.inflate(R.layout.fragment_notifications, container, false);

        bottomNavigationView.removeBadge(R.id.Notifications);

        FirebaseFirestore.getInstance().collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    ArrayList<Map<String, Object>> followers = (ArrayList<Map<String, Object>>) documentSnapshot.getData().get("followers");

                    if(followers != null){
                        ((LinearLayout) view.findViewById(R.id.container)).removeAllViews();

                        for(int i = 0; i < followers.size(); i++){

                            FirebaseFirestore.getInstance().collection("users").document(followers.get(i).get("uid").toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot documentSnapshot1 = task.getResult();
                                        String username = documentSnapshot1.getString("username");
                                        String profile_picture = documentSnapshot1.getString("profile_pic");
                                        String sender = documentSnapshot1.getString("uid");
                                        createNotification(profile_picture, username + " is following you now!", sender);
                                    }
                                }
                            });
                            Map<String, Object> follower = followers.get(i);
                            follower.put("seen", true);
                            followers.set(i, follower);
                            FirebaseFirestore.getInstance().collection("users").document(mAuth.getCurrentUser().getUid()).update("followers", followers).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                        }
                    }
                }
            }
        });

        return view;
    }

    private void createNotification(String picture, String notificationContent, String sender){
        LinearLayout linearLayout = view.findViewById(R.id.container);
        CardView notif_view = new CardView(view.getContext());
        LinearLayout notification = new LinearLayout(view.getContext());
        ImageView notif_pic = new ImageView(view.getContext());
        TextView notif_text = new TextView(view.getContext());
        View space = new View(view.getContext());
        if(picture.equals("notif_bell")){
            Glide.with(view).load(R.drawable.baseline_notifications_24).circleCrop().into(notif_pic);
        }
        else {
            if(!picture.equals(""))
                Glide.with(view).load(picture).circleCrop().into(notif_pic);
            else
                Glide.with(view).load(R.drawable.baseline_person_24).circleCrop().into(notif_pic);

            notif_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!sender.equals(mAuth.getCurrentUser().getUid()) ){
                        VisitProfileFragment fragment = new VisitProfileFragment(sender);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frameLayout, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                    }else {
                        ProfileFragment fragment = new ProfileFragment();
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frameLayout, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                }
            });
        }
        notif_text.setText(notificationContent);
        notif_text.setTextSize(25);
        notif_text.setTextColor(Color.BLACK);
        if(getContext() != null)
            notif_view.setBackground(getResources().getDrawable(R.drawable.black_border));
        notif_view.setRadius(20);
        space.setBackgroundColor(Color.WHITE);
        notification.setOrientation(LinearLayout.HORIZONTAL);

        notification.addView(notif_pic, 150, 150);
        notification.addView(notif_text, MATCH_PARENT, WRAP_CONTENT);
        notif_view.addView(notification, MATCH_PARENT, WRAP_CONTENT);
        linearLayout.addView(notif_view);
        linearLayout.addView(space, MATCH_PARENT, 20);

    }
}