package com.dicoding.rockman_barbershop.Activity.UserAuth;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dicoding.rockman_barbershop.Activity.DetailBarangActivity;
import com.dicoding.rockman_barbershop.MainActivity;
import com.dicoding.rockman_barbershop.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import id.zelory.compressor.Compressor;

public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText mFullName,mEmail,mPassword,mPassword2,mPhone;
    Button mRegisterBtn;
    TextView mLoginBtn;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;

    String userID;
    ProgressDialog loading;
    ProgressBar progressBar;

    ImageView mProfileImage;
    Uri mProfileImageUri = null;
    Bitmap compressedImageFile;
    private boolean isChanged = false;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName   = findViewById(R.id.regist_nama);
        mEmail      = findViewById(R.id.regist_email);
        mPassword   = findViewById(R.id.regist_password);
        mPassword2  = findViewById(R.id.confirm_regist_password);
        mPhone      = findViewById(R.id.regist_nomor);
        mRegisterBtn= findViewById(R.id.button_regisstrasi);
        mLoginBtn   = findViewById(R.id.TextRegist);
        mProfileImage= findViewById(R.id.add_profile_image);
        mediaPlayer= MediaPlayer.create(Register.this, R.raw.daftar);

        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(Register.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED){
                        ChoseImage();
                    }else{
                        requestStoragePermission();
                    }
                }
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString().trim();
                final String password = mPassword.getText().toString().trim();
                final String password2 = mPassword2.getText().toString().trim();
                final String fullName = mFullName.getText().toString();
                final String phone = mPhone.getText().toString();

                /** Cek jika ada kolom yang belum di isi **/
                if (TextUtils.isEmpty(fullName)) {
                    mFullName.setError("Isi kolom nama terlebih dahulu.");
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    mPhone.setError("Isi kolom nomor telpon terlebih dahulu.");
                    return;

                }
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Isi kolom email terlebih dahulu.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Isi kolom Password terlebih dahulu.");
                    return;
                }

                if (password.length() < 6) {
                    mPassword.setError("Password harus >= 6 karakter");
                    return;
                }

                if (!password.equals(password2)) {
                    mPassword.setError("Password tidak sama");
                    return;
                } else {

                    loading = ProgressDialog.show(Register.this,
                            null,
                            "Mohon Tunggu",
                            true,
                            false);
//                progressBar.setVisibility(View.VISIBLE);
//                final String randomName = UUID.randomUUID().toString();
                    /** Menghubungkan Firebase **/

                    if (mProfileImageUri != null){
                        registWithPhoto(email,password,fullName,phone);

                    } else {
                        registWithOutPhoto(email,password,fullName,phone);

                    }
                }
                }
            });


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();            }
        });
    }

    private void registWithPhoto(String email, String password, String fullName, String phone) {
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //COMPRESS PHOTO
                userID = fAuth.getCurrentUser().getUid();

                File newImageFile = new File(mProfileImageUri.getPath());
                try {
                    compressedImageFile = new Compressor(Register.this)
                            .setMaxHeight(720)
                            .setMaxWidth(720)
                            .setQuality(50)
                            .compressToBitmap(newImageFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageData = baos.toByteArray();

                StorageReference ref = storageReference.child("foto_users")
                        .child(userID + ".jpg");
                UploadTask image_path = ref.putBytes(imageData);

                Task<Uri> urlTask = image_path.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            Uri downloadUri = task.getResult();

                            String downloadUriString = downloadUri.toString();

                            Toast.makeText(Register.this, "Pendaftara berhasil.", Toast.LENGTH_SHORT).show();
//                                    userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("nama_profile", fullName);
                            user.put("email_profile", email);
                            user.put("phone_profile", phone);
                            user.put("tipe", "pengguna");
                            user.put("picture_url_profile", downloadUriString);
//                                            user.put("picture_thumb_profile",downloadthumbUri);
                            user.put("user_id", userID);
                            user.put("waktu_daftar", FieldValue.serverTimestamp());

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mediaPlayer.start();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                  progressBar.setVisibility(View.GONE);
                        loading.dismiss();
                    }
                });
                //imagepath
            }
        });

    }

    private void registWithOutPhoto(String email, String password, String fullName, String phone) {
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //COMPRESS PHOTO
                userID = fAuth.getCurrentUser().getUid();

                        if (task.isSuccessful()) {

                            Toast.makeText(Register.this, "Pendaftara berhasil.", Toast.LENGTH_SHORT).show();
//                                    userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("nama_profile", fullName);
                            user.put("email_profile", email);
                            user.put("phone_profile", phone);
                                user.put("tipe", "pengguna");
                            user.put("total_entry", 0);
                            user.put("picture_url_profile", null);
//                                            user.put("picture_thumb_profile",downloadthumbUri);
                            user.put("user_id", userID);
                            user.put("waktu_daftar", FieldValue.serverTimestamp());

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mediaPlayer.start();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "Error ! " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                  progressBar.setVisibility(View.GONE);
                        loading.dismiss();
                    }
                });
                //imagepath


    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Register.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(Register.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        } else {
            ActivityCompat.requestPermissions(Register.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void ChoseImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512, 512)
                .setAspectRatio(1, 1)
                .start(Register.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(Register.this, "Izin diberian", Toast.LENGTH_SHORT).show();
                ChoseImage();
            } else {
                Toast.makeText(Register.this, "Izin tidak diberian", Toast.LENGTH_SHORT).show();

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

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();


            }
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
}
