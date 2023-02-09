package com.cagri.tripapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostDesignFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostDesignFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private  FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private StorageReference storageRef = storage.getReference();

    public ImageView showImage;

    public TextView post_description;

    private Uri selectedImage;

    private Map<String, Object> post = new HashMap<>();


    public PostDesignFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostDesignFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostDesignFragment newInstance(String param1, String param2) {
        PostDesignFragment fragment = new PostDesignFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_design, container, false);
        showImage = view.findViewById(R.id.imageView2);
        post_description = view.findViewById(R.id.postDescription);

        view.findViewById(R.id.imageButton).setOnClickListener(view1 -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 200);
        });
        view.findViewById(R.id.button4).setOnClickListener(view1 -> {

            db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if(documentSnapshot.exists()){

                            post.put("post_description", post_description.getText().toString());
                            post.put("like", new ArrayList<>());
                            post.put("save", new ArrayList<>());
                            if(selectedImage != null) {

                                StorageReference profile_pic = storageRef.child("mobile").child(mAuth.getCurrentUser().getUid()).child(LocalDateTime.now().toString());
                                profile_pic.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        profile_pic.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                post.put("post_picture", uri);
                                                db.collection("users").document(mAuth.getCurrentUser().getUid()).update("posts", FieldValue.arrayUnion(post)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(getActivity(), "Post is  shared successfully.", Toast.LENGTH_LONG);
                                                        getFragmentManager().popBackStack();
                                                        getFragmentManager().beginTransaction().detach(PostDesignFragment.this).attach(PostDesignFragment.this).commit();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getActivity(), "Post is not shared successfully.", Toast.LENGTH_LONG);
                                            }
                                        });
                                    }
                                });
                            }
                            else {
                                post.put("post_picture", "");

                                db.collection("users").document(mAuth.getCurrentUser().getUid()).update("posts", FieldValue.arrayUnion(post)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getActivity(), "Post is  shared successfully.", Toast.LENGTH_LONG);
                                        getFragmentManager().popBackStack();
                                        getFragmentManager().beginTransaction().detach(PostDesignFragment.this).attach(PostDesignFragment.this).commit();
                                    }
                                });
                            }
                        }
                    }
                }
            });

        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        selectedImage = data.getData();

        if(selectedImage != null){
            showImage.setImageURI(selectedImage);
            StorageReference image = storageRef.child(selectedImage.toString());
            showImage.setVisibility(View.VISIBLE);
        }

    }
}