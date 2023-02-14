package com.cagri.tripapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cagri.tripapp.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new ProfileFragment());
        ProfileFragment profileFragment = new ProfileFragment();
        HomeFragment homeFragment = new HomeFragment();
        MessagesFragment messagesFragment = new MessagesFragment();
        NotificationsFragment notificationsFragment = new NotificationsFragment();
        SettingsFragment settingsFragment = new SettingsFragment();
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.Home:
                    replaceFragment(homeFragment);
                    break;
                case R.id.Messages:
                    replaceFragment(messagesFragment);
                    break;
                case R.id.Profile:
                    replaceFragment(profileFragment);
                    break;
                case R.id.Notifications:
                    replaceFragment(notificationsFragment);
                    break;
                case R.id.Settings:
                    replaceFragment(settingsFragment);
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout, fragment);
        ft.commit();
    }
}