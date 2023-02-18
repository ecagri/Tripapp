package com.cagri.tripapp;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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

    public static void createPost(View view, FragmentManager fragmentManager, Post post){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        LinearLayout linearLayout = view.findViewById(R.id.container);
        LinearLayout ll = new LinearLayout(view.getContext());
        LinearLayout ll2 = new LinearLayout(view.getContext());
        LinearLayout ll3 = new LinearLayout(view.getContext());
        ImageView profile_pic = new ImageView(view.getContext());
        ImageView post_pic = new ImageView(view.getContext());
        ImageButton fav_button = new ImageButton(view.getContext());
        ImageButton save_button = new ImageButton(view.getContext());

        CardView frame = new CardView(view.getContext());
        View line = new View(view.getContext());
        View space = new View(view.getContext());
        View space2 = new View(view.getContext());
        TextView username = new TextView(view.getContext());
        TextView post_text = new TextView(view.getContext());


        db.collection("posts").document(post.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                if(save != null && save.contains(post.getId())){
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
                    db.collection("posts").document(post.getId()).update("like", FieldValue.arrayUnion(mAuth.getCurrentUser().getUid()));
                    Toast.makeText(view.getContext(), "Liked", Toast.LENGTH_SHORT).show();

                }
                else {
                    Glide.with(view).load(R.drawable.baseline_favorite_border_24).into(fav_button);
                    fav_button.setContentDescription("notfavved");
                    db.collection("posts").document(post.getId()).update("like", FieldValue.arrayRemove(mAuth.getCurrentUser().getUid()));

                }
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(save_button.getContentDescription() == "notsaved"){
                    Glide.with(view).load(R.drawable.baseline_download_done_24).into(save_button);
                    save_button.setContentDescription("saved");
                    db.collection("users").document(mAuth.getCurrentUser().getUid()).update("save", FieldValue.arrayUnion(post.getId()));
                    Toast.makeText(view.getContext(), "Saved", Toast.LENGTH_SHORT).show();
                }
                else{
                    Glide.with(view).load(R.drawable.baseline_download_24).into(save_button);
                    save_button.setContentDescription("notsaved");
                    db.collection("users").document(mAuth.getCurrentUser().getUid()).update("save", FieldValue.arrayRemove(post.getId()));

                }
            }
        });

        if(!post.getPost_picture().equals(""))
            Glide.with(view).load(post.getPost_picture()).into(post_pic);
        if(!post.getProfile_picture().equals("")) {
            Glide.with(view).load(post.getProfile_picture()).diskCacheStrategy(DiskCacheStrategy.NONE).circleCrop().into(profile_pic);
        }
        else
            Glide.with(view).load(R.drawable.baseline_person_24).circleCrop().into(profile_pic);

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!post.getSender().equals(mAuth.getCurrentUser().getUid()) ){
                    VisitProfileFragment fragment = new VisitProfileFragment(post.getSender());
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

        post_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(post.getPost_picture()), "image/*");
                view.getContext().startActivity(intent);
            }
        });

        fav_button.setBackgroundColor(Color.TRANSPARENT);
        save_button.setBackgroundColor(Color.TRANSPARENT);
        username.setText(post.getUsername());
        username.setTextSize(20);
        username.setTextColor(Color.BLACK);
        post_text.setText(post.getDescription());
        post_text.setTextSize(20);
        line.setBackgroundColor(Color.BLACK);
        space.setBackgroundColor(Color.WHITE);
        space2.setBackgroundColor(Color.WHITE);
        post_pic.setScaleType(ImageView.ScaleType.FIT_XY);
        frame.addView(post_pic, MATCH_PARENT, WRAP_CONTENT);
        frame.setRadius(20);
        ll3.addView(username, WRAP_CONTENT, 100);
        if(!post.getDescription().equals(""))
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


        if(post.getSender().equals(mAuth.getCurrentUser().getUid()) ){
            ImageButton delete_button = new ImageButton(view.getContext());
            Glide.with(view).load(R.drawable.baseline_delete_24).into(delete_button);
            delete_button.setBackgroundColor(Color.TRANSPARENT);
            ll2.addView(delete_button,100,100);

            delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE: {
                                    db.collection("posts").document(post.getId()).delete();
                                    db.collection("users").document(post.sender).update("posts", FieldValue.arrayRemove(post.getId()));
                                }
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("Are you sure you want to delete this post?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();

                }
            });
        }

    }
}
