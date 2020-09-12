
package com.dicoding.rockman_barbershop.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dicoding.rockman_barbershop.MainActivity;
import com.dicoding.rockman_barbershop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import id.zelory.compressor.Compressor;

public class ProfileEditActivity extends AppCompatActivity {
    public final static String ExtraImage = "extra_image";
    public final static String ExtraName = "extra_name";
    public final static String ExtraPhone = "extra_phone";

    private static final int EDIT_CODE = 1;

    EditText mFullName, mPhone;
    Button mEditBtn;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;

    String eName,ePhone;

    String userId;
    ProgressDialog loading;
    ProgressBar progressBar;

    ImageView mProfileImage;
    Uri mProfileImageUri = null;
    Bitmap compressedImageFile;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        userId = fAuth.getCurrentUser().getUid();

        mProfileImage = findViewById(R.id.edit_profile_image);
        mFullName = findViewById(R.id.edit_nama_profil);
        mPhone = findViewById(R.id.edit_nomor_profil);

        String name = getIntent().getStringExtra(ExtraName);
        String phone = getIntent().getStringExtra(ExtraPhone);
        String image = getIntent().getStringExtra(ExtraImage);

        mFullName.setText(name);
        mPhone.setText(phone);

        RequestOptions placeholderRequest = new RequestOptions();
        placeholderRequest.placeholder(R.drawable.profile_update);

        Glide.with(ProfileEditActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(mProfileImage);

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(ProfileEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED){
                        ChoseImage();
                    }else{
                        requestStoragePermission();
                    }
                }
            }
        });

        mEditBtn = findViewById(R.id.button_edit_profile_confirm);
        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eName = mFullName.getText().toString().trim();
                ePhone = mPhone.getText().toString().trim();
                updateProfile();
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    public void updateProfile(){
        if(eName.isEmpty() || ePhone.isEmpty()) {
            Toast.makeText(ProfileEditActivity.this, "Isi seluruh kolom!", Toast.LENGTH_LONG).show();

        } else {
            //loading
            loading = ProgressDialog.show(ProfileEditActivity.this,
                    null,
                    "Mohon Tunggu",
                    true,
                    false);

            Toast.makeText(ProfileEditActivity.this, "Update data berhasil", Toast.LENGTH_LONG).show();

            if (mProfileImageUri != null){

                compressedImage();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageData = baos.toByteArray();

                StorageReference reference = storageReference.child("foto_users")
                        .child(userId + ".jpg");
                UploadTask image_path = reference.putBytes(imageData);

                image_path
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                getDownloadUrl(reference);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: ", e.getCause());
                            }
                        });
            } else {
                setUser();
            }
        }
}


    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "onSuccess: " +uri);
                setUserUri(uri);
            }
        });
    }

    private void setUser(){

        fStore.collection("users").document(userId)
                .update(
                        "nama_profile", eName,
                        "phone_profile", ePhone
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileEditActivity.this, "Updated succesfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProfileEditActivity.this, MainActivity.class);
                        startActivity(intent);
//                        Intent returnIntent = new Intent();
//                        setResult(RESULT_OK,returnIntent);
//                        finish();
                        loading.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileEditActivity.this, "Profile image failed...", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void setUserUri(Uri uri) {
        String downloadUriString = uri.toString();

        FirebaseUser user = fAuth.getCurrentUser();

        fStore.collection("users").document(userId)
                .update(
                        "nama_profile", eName,
                        "phone_profile", ePhone,
                        "picture_url_profile", downloadUriString
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileEditActivity.this, "Updated succesfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProfileEditActivity.this, MainActivity.class);
                        startActivity(intent);
//                        Intent returnIntent = new Intent();
//                        setResult(RESULT_OK,returnIntent);
//                        finish();
                        loading.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileEditActivity.this, "Profile image failed...", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void compressedImage() {
        File newImageFile = new File(mProfileImageUri.getPath());
        try {
            compressedImageFile = new Compressor(ProfileEditActivity.this)
                    .setMaxHeight(720)
                    .setMaxWidth(720)
                    .setQuality(50)
                    .compressToBitmap(newImageFile);

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(ProfileEditActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        } else {
            ActivityCompat.requestPermissions(ProfileEditActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void ChoseImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512, 512)
                .setAspectRatio(1, 1)
                .start(ProfileEditActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(ProfileEditActivity.this, "Izin diberian", Toast.LENGTH_SHORT).show();
                ChoseImage();
            } else {
                Toast.makeText(ProfileEditActivity.this, "Izin tidak diberian", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProfileImageUri = result.getUri();
                mProfileImage.setImageURI(mProfileImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }

}
