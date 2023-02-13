package com.cagri.tripapp;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Post implements Comparable{
    private String username;
    private String profile_picture;
    private String description;
    private String post_picture;
    private String date;
    private String id;
    private String sender;

    public Post(String username, String profile_picture, String description, String post_picture, String date, String id,String sender){
        this.username = username;
        this.profile_picture = profile_picture;
        this.description = description;
        this.post_picture = post_picture;
        this.date = date;
        this.id = id;
        this.sender=sender;
    }

    public String getUsername() {
        return username;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getPost_picture() {
        return post_picture;
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
    @Override
    public int compareTo(Object o) {
        Post post = (Post) o;
        return this.date.compareTo(post.getDate());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Post post = (Post) obj;
        return this.id.equals(((Post) obj).getId());
    }

    public static void createPost(View view, Activity activity, FirebaseFirestore db, FirebaseAuth mAuth, Context context, FragmentManager fragmentManager, String nameOfUser, String postDescription, String profilePicture, String picPost, String postId, String sender){
        LinearLayout linearLayout = view.findViewById(R.id.container);
        LinearLayout ll = new LinearLayout(activity);
        LinearLayout ll2 = new LinearLayout(activity);
        LinearLayout ll3 = new LinearLayout(activity);
        ImageView profile_pic = new ImageView(activity);
        ImageView post_pic = new ImageView(activity);
        ImageButton fav_button = new ImageButton(activity);
        ImageButton save_button = new ImageButton(activity);
        CardView frame = new CardView(activity);
        View line = new View(activity);
        View space = new View(activity);
        View space2 = new View(activity);
        TextView username = new TextView(activity);
        TextView post_text = new TextView(activity);

        db.collection("posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                ArrayList<String> likes = (ArrayList<String>) documentSnapshot.getData().get("like");

                if(likes != null && likes.contains(mAuth.getCurrentUser().getUid())){
                    Glide.with(view).load(R.drawable.baseline_favorite_24).into(fav_button);
                    fav_button.setContentDescription("favved");
                }
                else{
                    Glide.with(view).load(R.drawable.baseline_favorite_border_24).into(fav_button);
                    fav_button.setContentDescription("notfavved");
                }
            }
        });

        db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                ArrayList<String> save = (ArrayList<String>) documentSnapshot.getData().get("save");
                if(save != null && save.contains(postId)){
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
                    Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();

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
                    db.collection("users").document(mAuth.getCurrentUser().getUid()).update("save", FieldValue.arrayUnion(postId));
                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                }
                else{
                    Glide.with(view).load(R.drawable.baseline_download_24).into(save_button);
                    save_button.setContentDescription("notsaved");
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

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!sender.equals(mAuth.getCurrentUser().getUid()) ){
                    VisitProfileFragment fragment = new VisitProfileFragment(sender);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                }else {
                    ProfileFragment fragment = new ProfileFragment();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

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
