package com.cagri.tripapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

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

    private ArrayList<String> likes = new ArrayList<>();

    private ArrayList<String> saves = new ArrayList<>();

    LinearLayout linearLayout;

    Uri selectedImage;
    View view;

    public ImageView showImage;

    Context context;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        view = inflater.inflate(R.layout.fragment_home, container, false);
        view.findViewById(R.id.imageButton2).setOnClickListener(view1 -> {
            PostDesignFragment fragment = new PostDesignFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        context=getContext();
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<Post> postArray = new ArrayList<>();
                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        String username = documentSnapshot.getData().get("username").toString();
                        String profile_picture = documentSnapshot.getData().get("profile_pic").toString();
                        ArrayList<String> posts = (ArrayList<String>) documentSnapshot.getData().get("posts");
                        if(posts != null) {
                            for (int i = 0; i < posts.size(); i++) {
                                db.collection("posts").document(posts.get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot document = task.getResult();
                                            String post_description = document.getString("post_description");
                                            String post_picture = document.getString("post_picture");
                                            String post_id = document.getString("id");
                                            String post_date = document.getString("date");
                                            String sender = document.getString("sender");
                                            Post post = new Post(username, profile_picture, post_description, post_picture, post_date, post_id, sender);
                                            Post.createPost(view, getFragmentManager(), post);
                                            postArray.add(new Post(username, profile_picture, post_description, post_picture, post_date, post_id,sender));
                                        }
                                    }
                                });
                            }
                            Collections.sort(postArray, Collections.reverseOrder());
                            for(int i = 0; i < postArray.size(); i++){
                                Post post = postArray.get(i);
                            }
                        }
                    }
                }
            }
        });
        return view;
    }
}