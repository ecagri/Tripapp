package com.cagri.tripapp;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.content.res.ColorStateList;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VisitProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class VisitProfileFragment extends Fragment {

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

    ImageView showImage;
    private Uri selectedImage;

    LinearLayout linearLayout;

    View view;

    Context context ;

    String sender ;
    public VisitProfileFragment() {
        // Required empty public constructor
    }

    public VisitProfileFragment(String sender) {
        // Required empty public constructor
        this.sender=sender;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VisitProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        view= inflater.inflate(R.layout.fragment_visit_profile, container, false);
        showImage = ((ImageView) view.findViewById(R.id.imageButton));

        linearLayout = view.findViewById(R.id.container);

        view.findViewById(R.id.follow_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                TextView follow_button = (TextView) view.findViewById(R.id.follow_button);
                if(follow_button.getText().equals("Follow")){
                    follow_button.setText("Following");
                    follow_button.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
                    TextView followers = (TextView) view.findViewById(R.id.textView9);
                    followers.setText((Integer.valueOf((String) followers.getText()) + 1)+"");


                    db.collection("users").document(mAuth.getCurrentUser().getUid()).update("followings", FieldValue.arrayUnion(sender)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            db.collection("users").document(sender).update("followers", FieldValue.arrayUnion(mAuth.getCurrentUser().getUid())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getActivity(), "You are following this user now!", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    });

                }
                else if(follow_button.getText().equals("Following")){
                    follow_button.setText("Follow");
                    follow_button.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                    TextView followers = (TextView) view.findViewById(R.id.textView9);
                    followers.setText((Integer.valueOf((String) followers.getText()) - 1)+"");
                    db.collection("users").document(mAuth.getCurrentUser().getUid()).update("followings", FieldValue.arrayRemove(sender)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            db.collection("users").document(sender).update("followers", FieldValue.arrayRemove(mAuth.getCurrentUser().getUid())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getActivity(), "You are not following this user anymore!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });

        context = getContext();
        db.collection("users").document(sender).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){

                        String username = documentSnapshot.getData().get("username").toString();
                        ((TextView)view.findViewById(R.id.textView)).setText("@"+ username);
                        String profile_picture = documentSnapshot.getData().get("profile_pic").toString();
                        if(!profile_picture.equals("")) {
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
                            if(followers.contains(mAuth.getCurrentUser().getUid())) {
                                TextView follow_button = (TextView) view.findViewById(R.id.follow_button);
                                follow_button.setText("Following");
                                follow_button.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
                            }
                        }
                        if(followings != null){
                            ((TextView) view.findViewById(R.id.textView8)).setText(followings.size()+"");
                        }
                        if(posts != null){
                            ((TextView) view.findViewById(R.id.textView7)).setText(posts.size()+"");
                        }
                        db.collection("posts").whereEqualTo("sender", sender).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String post_description = document.getString("post_description");
                                        String post_picture = document.getString("post_picture");
                                        String post_id = document.getString("id");
                                        String sender = document.getString("sender");
                                        Post.createPost(view, getActivity(), db, mAuth, getContext(), getFragmentManager(), username, post_description, profile_picture, post_picture, post_id, sender);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

        return view;
    }

    public void createPost(String nameOfUser, String postDescription, String profilePicture, String picPost, String postId){
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
        View space2 = new View(getActivity());
        TextView username = new TextView(getActivity());
        TextView post_text = new TextView(getActivity());

        db.collection("posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                likes = (ArrayList<String>) documentSnapshot.getData().get("like");
                saves = (ArrayList<String>) documentSnapshot.getData().get("save");

                if(likes != null && likes.contains(mAuth.getCurrentUser().getUid())){
                    Glide.with(view).load(R.drawable.baseline_favorite_24).into(fav_button);
                    fav_button.setContentDescription("favved");
                }
                else{
                    Glide.with(view).load(R.drawable.baseline_favorite_border_24).into(fav_button);
                    fav_button.setContentDescription("notfavved");
                }
                if (saves != null && saves.contains(mAuth.getCurrentUser().getUid())) {
                    Glide.with(view).load(R.drawable.baseline_download_done_24).into(save_button);
                    save_button.setContentDescription("saved");
                }
                else{

                    Glide.with(view).load(R.drawable.baseline_download_24).into(save_button);
                    save_button.setContentDescription("notsaved");
                }
            }
        });

        fav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fav_button.getContentDescription() == "notfavved") {
                    Glide.with(view).load(R.drawable.baseline_favorite_24).into(fav_button);
                    fav_button.setContentDescription("favved");
                    db.collection("posts").document(postId).update("like", FieldValue.arrayUnion(mAuth.getCurrentUser().getUid()));

                }
                else {
                    Glide.with(view).load(R.drawable.baseline_favorite_border_24).into(fav_button);
                    fav_button.setContentDescription("notfavved");
                    db.collection("posts").document(postId).update("like", FieldValue.arrayRemove(mAuth.getCurrentUser().getUid()));

                }
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(save_button.getContentDescription() == "notsaved"){
                    Glide.with(view).load(R.drawable.baseline_download_done_24).into(save_button);
                    save_button.setContentDescription("saved");
                    //db.collection("posts").document(postId).update("save", FieldValue.arrayUnion(mAuth.getCurrentUser().getUid()));
                    db.collection("users").document(mAuth.getCurrentUser().getUid()).update("save", FieldValue.arrayUnion(postId));
                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                }
                else{
                    Glide.with(view).load(R.drawable.baseline_download_24).into(save_button);
                    save_button.setContentDescription("notsaved");
                    //db.collection("posts").document(postId).update("save", FieldValue.arrayRemove(mAuth.getCurrentUser().getUid()));
                    db.collection("users").document(mAuth.getCurrentUser().getUid()).update("save", FieldValue.arrayRemove(postId));

                }
            }
        });

        if(!picPost.equals(""))
            Glide.with(view).load(picPost).into(post_pic);
        if(!profilePicture.equals("")) {
            Glide.with(view).load(profilePicture).diskCacheStrategy(DiskCacheStrategy.NONE).circleCrop().into(profile_pic);
        }
        else
            Glide.with(view).load(R.drawable.baseline_person_24).circleCrop().into(profile_pic);

        fav_button.setBackgroundColor(Color.TRANSPARENT);
        save_button.setBackgroundColor(Color.TRANSPARENT);
        username.setText(nameOfUser);
        username.setTextSize(20);
        username.setTextColor(Color.BLACK);
        post_text.setText(postDescription);
        post_text.setTextSize(20);
        line.setBackgroundColor(Color.BLACK);
        space.setBackgroundColor(Color.WHITE);
        space2.setBackgroundColor(Color.WHITE);
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
        linearLayout.addView(space2, MATCH_PARENT, 20);
        linearLayout.addView(ll);
        linearLayout.addView(space, MATCH_PARENT, 20);
        linearLayout.addView(line, MATCH_PARENT, 5);
        linearLayout.setHorizontalGravity(Gravity.RIGHT);
    }
}