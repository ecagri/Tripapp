package com.cagri.tripapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private BottomNavigationView bottomNavigationView;

    private ListenerRegistration homeUserListener;
    private View view;

    public HomeFragment() {
        // Required empty public constructor
    }

    public HomeFragment(BottomNavigationView bottomNavigationView){
        this.bottomNavigationView = bottomNavigationView;
    }

    public ListenerRegistration getHomeUserListener() {
        return homeUserListener;
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
        view.setBackgroundResource(R.drawable.splash);
        view.findViewById(R.id.homeScroll).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.showRecent).setVisibility(View.INVISIBLE);
        bottomNavigationView.removeBadge(R.id.Home);

        FirebaseFirestore.getInstance().collection("posts").orderBy("date", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayList<Post> posts = new ArrayList<>();
                    HashMap<String, Map<String, String>> users = new HashMap<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String post_description = document.getString("post_description");
                        String post_picture = document.getString("post_picture");
                        String post_id = document.getString("id");
                        String post_date = document.getString("date");
                        String sender = document.getString("sender");
                        posts.add(new Post("", "", post_description, post_picture, post_date, post_id, sender));
                    }
                    getUserInfo(users, posts, 0);
                }
            }
        });
        view.findViewById(R.id.imageButton2).setOnClickListener(view1 -> {
            PostDesignFragment fragment = new PostDesignFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, fragment, "post_design");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        view.findViewById(R.id.showRecentButton).setOnClickListener(view1 -> {
            getFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment(bottomNavigationView), "home").commit();
        });

        return view;
    }

    private void getUserInfo(HashMap<String, Map<String, String>> users, ArrayList<Post> posts, int iteration){
        if(iteration >= posts.size() - 1) {
            view.findViewById(R.id.homeScroll).setVisibility(View.VISIBLE);
            view.setBackground(null);
            return;
        }
        Post post = posts.get(iteration);

        if(users.containsKey(post.getSender())){
            post.setUsername(users.get(post.getSender()).get("username"));
            post.setProfile_picture(users.get(post.getSender()).get("profile_pic"));
            Post.createPost(view, getFragmentManager(), post);
            getUserInfo(users, posts, iteration + 1);
        }

        else {
            FirebaseFirestore.getInstance().collection("users").document(post.getSender()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        post.setUsername(documentSnapshot.getData().get("username").toString());
                        post.setProfile_picture(documentSnapshot.getData().get("profile_pic").toString());
                        Post.createPost(view, getFragmentManager(), post);
                        Map<String, String> userInfo = new HashMap<>();
                        userInfo.put("username", post.getUsername());
                        userInfo.put("profile_pic", post.getProfile_picture());
                        users.put(post.getSender(), userInfo);
                        getUserInfo(users, posts, iteration + 1);
                    }
                }
            });
        }
    }
}