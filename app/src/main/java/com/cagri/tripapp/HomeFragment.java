package com.cagri.tripapp;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
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
    LinearLayout linearLayout;

    Uri selectedImage;
    View view;

    public ImageView showImage;

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

        db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        String username = documentSnapshot.getData().get("username").toString();
                        String profile_picture = documentSnapshot.getData().get("profile_pic").toString();
                        ArrayList<Map<String, Object>> posts = (ArrayList<Map<String, Object>>) documentSnapshot.getData().get("posts");

                        for(int i = 0; i < posts.size(); i++){
                            String post_description = posts.get(i).get("post_description").toString();
                            String post_picture = posts.get(i).get("post_picture").toString();
                            createPost(username, post_description, profile_picture, post_picture);
                        }
                    }
                }
            }
        });

        return view;
    }

    public void createPost(String nameOfUser, String postDescription, String profilePicture, String picPost){
        linearLayout = view.findViewById(R.id.container);
        LinearLayout ll = new LinearLayout(getActivity());
        LinearLayout ll2 = new LinearLayout(getActivity());
        LinearLayout ll3 = new LinearLayout(getActivity());
        ImageView profile_pic = new ImageView(getActivity());
        ImageView post_pic = new ImageView(getActivity());
        ImageButton fav_button = new ImageButton(getActivity());
        ImageButton save_button = new ImageButton(getActivity());
        CardView frame = new CardView(getActivity());
        View line = new View(getActivity());
        View space = new View(getActivity());
        TextView username = new TextView(getActivity());
        TextView post_text = new TextView(getActivity());

        Glide.with(view).load(picPost).into(post_pic);
        Glide.with(view).load(profilePicture).circleCrop().into(profile_pic);
        Glide.with(view).load(R.drawable.baseline_favorite_border_24).into(fav_button);
        Glide.with(view).load(R.drawable.baseline_download_24).into(save_button);

        fav_button.setBackgroundColor(Color.TRANSPARENT);
        fav_button.setContentDescription("notfavved");
        save_button.setBackgroundColor(Color.TRANSPARENT);
        save_button.setContentDescription("notsaved");

        fav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fav_button.getContentDescription() == "notfavved") {
                    Glide.with(view).load(R.drawable.baseline_favorite_24).into(fav_button);
                    fav_button.setContentDescription("favved");
                }
                else {
                    Glide.with(view).load(R.drawable.baseline_favorite_border_24).into(fav_button);
                    fav_button.setContentDescription("notfavved");
                }
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(save_button.getContentDescription() == "notsaved"){
                    Glide.with(view).load(R.drawable.baseline_download_done_24).into(save_button);
                    save_button.setContentDescription("saved");
                }
                else{
                    Glide.with(view).load(R.drawable.baseline_download_24).into(save_button);
                    save_button.setContentDescription("notsaved");
                }
            }
        });

        username.setText(nameOfUser);
        username.setTextSize(20);
        //username.setTypeface(username.getTypeface(), Typeface.BOLD);
        username.setTextColor(Color.BLACK);
        post_text.setText(postDescription);
        post_text.setTextSize(20);
        line.setBackgroundColor(Color.BLACK);
        space.setBackgroundColor(Color.WHITE);
        post_pic.setScaleType(ImageView.ScaleType.FIT_XY);

        frame.addView(post_pic, MATCH_PARENT, WRAP_CONTENT);
        frame.setRadius(20);


        ll3.addView(username, WRAP_CONTENT, 100);
        if(!postDescription.equals(""))
            ll3.addView(post_text);
        ll3.addView(frame, MATCH_PARENT, WRAP_CONTENT);
        ll3.addView(ll2);
        ll.addView(profile_pic, 100, 100);
        ll.addView(ll3, MATCH_PARENT, WRAP_CONTENT);
        ll2.addView(fav_button, 100, 100);
        ll2.addView(save_button, 100, 100);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll2.setOrientation(LinearLayout.HORIZONTAL);
        ll3.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(ll);
        linearLayout.addView(line, MATCH_PARENT, 5);
        linearLayout.addView(space, MATCH_PARENT, 20);
        linearLayout.setHorizontalGravity(Gravity.RIGHT);
    }
}