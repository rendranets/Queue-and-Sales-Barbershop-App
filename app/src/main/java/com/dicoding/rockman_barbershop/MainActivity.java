package com.dicoding.rockman_barbershop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dicoding.rockman_barbershop.fragment.RiwayatTransaksiFragment;
import com.dicoding.rockman_barbershop.fragment.AskToLoginFragment;
import com.dicoding.rockman_barbershop.fragment.CollectionFragment;
import com.dicoding.rockman_barbershop.fragment.HomeFragment;
import com.dicoding.rockman_barbershop.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public final static String ExtraId = "extra_id";

    private String none = "";
    private StorageReference mStorageRef;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if(fAuth.getCurrentUser() != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AskToLoginFragment()).commit();
        }


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                switch (item.getItemId()){
                    case R.id.nav_home:
                        if(fAuth.getCurrentUser() != null){
                            selectedFragment = new HomeFragment();
                        }else {
                            selectedFragment = new AskToLoginFragment();
                        }
                        break;
                    case R.id.nav_add:
                        if(fAuth.getCurrentUser() != null){
                            selectedFragment = new RiwayatTransaksiFragment();
                        }else {
                            selectedFragment = new AskToLoginFragment();
                        }
                        break;
                    case R.id.nav_collection:
                        if(fAuth.getCurrentUser() != null){
                            selectedFragment = new CollectionFragment();
                        }else {
                            selectedFragment = new AskToLoginFragment();
                        }
                        break;
                    case R.id.nav_profile :
                        if(fAuth.getCurrentUser() != null){
                            selectedFragment = new ProfileFragment();
                        }else {
                            selectedFragment = new AskToLoginFragment();
                        }
                        break;
                }

                assert selectedFragment != null;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
                return true;
            };

    @Override
    public void onClick(View v) {
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                  RiwayatTransaksiFragment.fotoEntryUrl = result.getUri();
                RiwayatTransaksiFragment.fotoEntry.setImageURI(RiwayatTransaksiFragment.fotoEntryUrl);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

            }
        }
    }

}
