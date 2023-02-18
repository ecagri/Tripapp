package com.cagri.tripapp;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private ImageView showImage;
    private Uri selectedImage;
    private Context context;
    private View view;

    public ProfileFragment() {
        // Required empty public constructor

    }

    private void refresh(){

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        ((LinearLayout) view.findViewById(R.id.container)).removeViews(3, ((LinearLayout) view.findViewById(R.id.container)).getChildCount() - 3);
        showImage = ((ImageView) view.findViewById(R.id.imageButton));
        context=getContext();

        FirebaseFirestore.getInstance().collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    String username = documentSnapshot.getData().get("username").toString();
                    ((TextView)view.findViewById(R.id.textView)).setText("@" + username);
                    String profile_picture = documentSnapshot.getData().get("profile_pic").toString();

                    if(!profile_picture.equals("")) {
                        Glide.get(getContext()).clearMemory();
                        Glide.with(view).load(profile_picture).circleCrop().into(showImage);
                    }
                    else {
                        Glide.with(view).load(R.drawable.baseline_person_24).circleCrop().into(showImage);
                    }

                    ArrayList<Map<String, Object>> followers = (ArrayList<Map<String, Object>>) documentSnapshot.getData().get("followers");
                    ArrayList<Map<String, Object>> followings = (ArrayList<Map<String, Object>>) documentSnapshot.getData().get("followings");
                    ArrayList<String> posts = (ArrayList<String>) documentSnapshot.getData().get("posts");
                    if(followers != null){
                        ((TextView) view.findViewById(R.id.textView9)).setText(followers.size()+"");
                    }
                    if(followings != null){
                        ((TextView) view.findViewById(R.id.textView8)).setText(followings.size()+"");
                    }
                    if(posts != null){
                        ((TextView) view.findViewById(R.id.textView7)).setText(posts.size()+"");
                    }

                    if(((BottomNavigationView) view.findViewById(R.id.segment)).getSelectedItemId() == R.id.Posts){
                        showPosts(username, profile_picture);
                    }
                    else if(((BottomNavigationView) view.findViewById(R.id.segment)).getSelectedItemId() == R.id.Saves){
                        showSaves(documentSnapshot);
                    }
                }
            }
        });

        ((BottomNavigationView) view.findViewById(R.id.segment)).setOnItemSelectedListener(item -> {
            ((LinearLayout) view.findViewById(R.id.container)).removeViews(3, ((LinearLayout) view.findViewById(R.id.container)).getChildCount() - 3);
            FirebaseFirestore.getInstance().collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    switch (item.getItemId()) {
                        case R.id.Posts:
                            String username = documentSnapshot.getData().get("username").toString();
                            String profile_picture = documentSnapshot.getData().get("profile_pic").toString();
                            showPosts(username, profile_picture);
                            break;
                        case R.id.Saves:
                            showSaves(documentSnapshot);
                            break;
                        }
                    }
                });
            return true;
        });


        view.findViewById(R.id.imageButton2).setOnClickListener(view1 -> {
            PostDesignFragment fragment = new PostDesignFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, fragment, "post_design");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        view.findViewById(R.id.imageButton).setOnClickListener(view1 -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 200);
        });
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == RESULT_OK) {
            selectedImage = data.getData();

            if (selectedImage != null) {
                StorageReference profile_pic = storageRef.child(mAuth.getCurrentUser().getUid());
                profile_pic.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        profile_pic.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                FirebaseFirestore.getInstance().collection("users").document(mAuth.getCurrentUser().getUid()).update(
                                        "profile_pic", uri);
                            }
                        });
                    }
                });
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showPosts(String username, String profile_picture){
        FirebaseFirestore.getInstance().collection("posts").whereEqualTo("sender", mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ((LinearLayout) view.findViewById(R.id.container)).removeViews(3, ((LinearLayout) view.findViewById(R.id.container)).getChildCount() - 3);
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String post_description = document.getString("post_description");
                        String post_picture = document.getString("post_picture");
                        String post_id = document.getString("id");
                        String sender = document.getString("sender");
                        String date = document.getString("date");
                        Post post = new Post(username, profile_picture, post_description, post_picture, date, post_id, sender);
                        Post.createPost(view, getFragmentManager(), post);
                    }
                }
            }
        });
    }

    private void showSaves(DocumentSnapshot documentSnapshot){
        ArrayList<String> saves = (ArrayList<String>) documentSnapshot.getData().get("save");
        if (saves != null) {
            for (int i = 0; i < saves.size(); i++) {
                FirebaseFirestore.getInstance().collection("posts").whereEqualTo("id", saves.get(i)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ((LinearLayout) view.findViewById(R.id.container)).removeViews(3, ((LinearLayout) view.findViewById(R.id.container)).getChildCount() - 3);
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String post_description = document.getString("post_description");
                                String post_picture = document.getString("post_picture");
                                String post_id = document.getString("id");
                                String sender = document.getString("sender");
                                String date = document.getString("date");

                                FirebaseFirestore.getInstance().collection("users").document(sender).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot1 = task.getResult();
                                            String username = documentSnapshot1.getData().get("username").toString();
                                            String profile_picture = documentSnapshot1.getData().get("profile_pic").toString();
                                            Post post = new Post(username, profile_picture, post_description, post_picture, date, post_id, sender);

                                            Post.createPost(view, getFragmentManager(), post);
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        }
    }
}