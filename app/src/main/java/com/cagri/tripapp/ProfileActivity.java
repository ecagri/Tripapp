package com.cagri.tripapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.cagri.tripapp.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private ListenerRegistration profileListener;

    private ListenerRegistration homePostListener;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NotificationsFragment notificationsFragment = new NotificationsFragment(findViewById(R.id.bottomNavigationView));

        addProfileListener();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ProfileFragment(), "profile").commit();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if(profileListener != null){
                profileListener.remove();
            }
            if(homePostListener != null){
                homePostListener.remove();
            }
            switch (item.getItemId()){
                case R.id.Home:
                    addHomeListeners();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment(findViewById(R.id.bottomNavigationView)), "home").commit();
                    getSupportFragmentManager().executePendingTransactions();
                    break;
                case R.id.Messages:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new MessagesFragment()).commit();
                    break;
                case R.id.Profile:
                    addProfileListener();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ProfileFragment(), "profile").commit();
                    break;
                case R.id.Notifications:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, notificationsFragment).commit();
                    break;
                case R.id.Settings:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SettingsFragment()).commit();
                    break;
            }
            return true;
        });
    }

    private void addProfileListener(){
        profileListener = FirebaseFirestore.getInstance().collection("users").whereEqualTo("uid", mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                if (snapshots.getDocumentChanges().get(0).getType() == DocumentChange.Type.MODIFIED) {
                    if(getSupportFragmentManager().findFragmentByTag("profile") != null && getSupportFragmentManager().findFragmentByTag("post_design") == null) {
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frameLayout, new ProfileFragment(), "profile").commit();
                    }
                }
            }
        });
    }

    private void addHomeListeners(){
        homePostListener = db.collection("posts").whereEqualTo("sender", mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.REMOVED) {
                        if(getSupportFragmentManager().findFragmentByTag("home") != null && getSupportFragmentManager().findFragmentByTag("post_design") == null){
                            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment(findViewById(R.id.bottomNavigationView)), "home").commit();
                        }
                    }
                }
            }
        });
    }
}