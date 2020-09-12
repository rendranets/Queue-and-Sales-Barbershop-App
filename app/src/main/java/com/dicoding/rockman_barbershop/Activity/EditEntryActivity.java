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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import id.zelory.compressor.Compressor;

public class EditEntryActivity extends AppCompatActivity {

    public final static String ExtraId = "extra_id";
    public final static String ExtraJudul = "extra_judul";
    public final static String ExtraKategori = "extra_kategori";
    public final static String ExtraProvinsi = "extra_provinsi";
    public final static String ExtraDaerah = "extra_daerah";
    public final static String ExtraKeterangan = "extra_keterangan";
    public final static String ExtraFotoUrl = "extra_foto_url";

    private String  exKategori, exProvinsi, exDaerah, exKeterangan, exJudul, exId, exFotoUrl;
    private String  sKategori, sProvisi, sDaerah, sKeterangan, sJudul, sId, sFotoUrl;
    private String  stringProvinsi, stringKategori;

    private int spinnerItemKategori = 0, spinnerItemProvinsi = 0;

    private EditText etDaerah, etKeterangan, etJudul;
    private Button simpan, cancel;
    private Spinner spKategori, spProvinsi;

    private ImageView ivFotoEntry;
    public static Uri fotoEntryUri = null;
    private Bitmap compressedImageFile;

    private ProgressDialog loading;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private StorageReference storageReference;

    private String userId;
    private String TAG;
    private String setLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        exJudul = getIntent().getStringExtra(ExtraJudul);
        exKategori = getIntent().getStringExtra(ExtraKategori);
        exProvinsi = getIntent().getStringExtra(ExtraProvinsi);
        exDaerah = getIntent().getStringExtra(ExtraDaerah);
        exKeterangan = getIntent().getStringExtra(ExtraKeterangan);
        exFotoUrl = getIntent().getStringExtra(ExtraFotoUrl);
        exId = getIntent().getStringExtra(ExtraId);

        etJudul         = findViewById(R.id.entry_judul);
        etDaerah        = findViewById(R.id.entry_asal_daerah);
        etKeterangan    = findViewById(R.id.entry_keterangan);
        ivFotoEntry     = findViewById(R.id.entry_image);
        simpan          = findViewById(R.id.produk  );
        cancel          = findViewById(R.id.cancel_entry);

        spProvinsi = (Spinner) findViewById(R.id.entry_provinsi);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.
                createFromResource(EditEntryActivity.this, R.array.provinsi_array,
                        android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProvinsi.setAdapter(adapter1);

        spProvinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                stringProvinsi = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spKategori = (Spinner) findViewById(R.id.entry_kategori);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.
                createFromResource(EditEntryActivity.this, R.array.kategori_array,
                        android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKategori.setAdapter(adapter2);
        spKategori.setSelection(spinnerItemKategori);

        spKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                stringKategori = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        loading = ProgressDialog.show(EditEntryActivity.this,
                null,
                "Mohon Tunggu",
                true,
                false);

        etJudul.setText(exJudul);
        LocationChange();
        spProvinsi.setSelection(spinnerItemProvinsi);
        CategoryChange();
        spKategori.setSelection(spinnerItemKategori);
        etDaerah.setText(exDaerah);
        etKeterangan.setText(exKeterangan);

        RequestOptions placeholderRequest = new RequestOptions();
        placeholderRequest.placeholder(R.drawable.add_image);

        Glide.with(EditEntryActivity.this).setDefaultRequestOptions(placeholderRequest)
                .load(exFotoUrl).into(ivFotoEntry);

        loading.dismiss();

        ivFotoEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(EditEntryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED){
                        ChoseImage();
                    }else{
                        requestStoragePermission();
                    }
                }
            }
        });

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sJudul =  etJudul.getText().toString().trim();
                sKategori =  stringKategori.trim();
                sProvisi =  stringProvinsi.trim();
                sDaerah =  etDaerah.getText().toString().trim();
                sKeterangan =  etKeterangan.getText().toString().trim();
                updateProfile();
            }
        });
    }

    private void LocationChange(){
        if(exProvinsi.equals("Aceh")){
            spinnerItemProvinsi = 0;
        }if(exProvinsi.equals("Sumatera Utara")){
            spinnerItemProvinsi = 1;
        }if(exProvinsi.equals("Sumatera Barat")){
            spinnerItemProvinsi = 2;
        }if(exProvinsi.equals("Riau")){
            spinnerItemProvinsi = 3;
        }if(exProvinsi.equals("Kepulauan Riau")){
            spinnerItemProvinsi = 4;
        }if(exProvinsi.equals("Jambi")){
            spinnerItemProvinsi = 5;
        }if(exProvinsi.equals("Bengkulu")){
            spinnerItemProvinsi = 6;
        }if(exProvinsi.equals("Sumatera Selatan")){
            spinnerItemProvinsi = 7;
        }if(exProvinsi.equals("Kepulauan Bangka Belitung")){
            spinnerItemProvinsi = 8;
        }if(exProvinsi.equals("Lampung")){
            spinnerItemProvinsi = 9;
        }if(exProvinsi.equals("Banten")){
            spinnerItemProvinsi = 10;
        }if(exProvinsi.equals("Jawa Barat")){
            spinnerItemProvinsi = 11;
        }if(exProvinsi.equals("DKI Jakarta")){
            spinnerItemProvinsi = 12;
        }if(exProvinsi.equals("Jawa Tengah")){
            spinnerItemProvinsi = 13;
        }if(exProvinsi.equals("DI Yogyakarta")){
            spinnerItemProvinsi = 14;
        }if(exProvinsi.equals("awa Timur")){
            spinnerItemProvinsi = 15;
        }if(exProvinsi.equals("Bali")){
            spinnerItemProvinsi = 16;
        }if(exProvinsi.equals("Nusa Tenggara Barat")){
            spinnerItemProvinsi = 17;
        }if(exProvinsi.equals("Nusa Tenggara Timur")){
            spinnerItemProvinsi = 18;
        }if(exProvinsi.equals("Kalimantan Utara")){
            spinnerItemProvinsi = 19;
        }if(exProvinsi.equals("Kalimantan Barat")){
            spinnerItemProvinsi = 20;
        }if(exProvinsi.equals("Kalimantan Tengah")){
            spinnerItemProvinsi = 21;
        }if(exProvinsi.equals("Kalimantan Selatan")){
            spinnerItemProvinsi = 22;
        }if(exProvinsi.equals("Kalimantan Timur")){
            spinnerItemProvinsi = 23;
        }if(exProvinsi.equals("Gorontalo")){
            spinnerItemProvinsi = 24;
        }if(exProvinsi.equals("Sulawesi Utara")){
            spinnerItemProvinsi = 25;
        }if(exProvinsi.equals("Sulawesi Barat")){
            spinnerItemProvinsi = 26;
        }if(exProvinsi.equals("Sulawesi Tengah")){
            spinnerItemProvinsi = 27;
        }if(exProvinsi.equals("Sulawesi Selatan")){
            spinnerItemProvinsi = 28;
        }if(exProvinsi.equals("Sulawesi Tenggara")){
            spinnerItemProvinsi = 29;
        }if(exProvinsi.equals("Maluku Utara")){
            spinnerItemProvinsi = 30;
        }if(exProvinsi.equals("aluku")){
            spinnerItemProvinsi = 31;
        }if(exProvinsi.equals("Papua Barat")){
            spinnerItemProvinsi = 32;
        }if(exProvinsi.equals("Papua")){
            spinnerItemProvinsi = 33;
        }
    }

    private void CategoryChange(){
        if(exKategori.equals("Alat Musik")){
            spinnerItemKategori = 0;
        }if(exKategori.equals("Cerita Rakyat")){
            spinnerItemKategori = 1;
        }if(exKategori.equals("Makanan Minuman")){
            spinnerItemKategori = 2;
        }if(exKategori.equals("Motif Kain")){
            spinnerItemKategori = 3;
        }if(exKategori.equals("Musik dan Lagu")){
            spinnerItemKategori = 4;
        }if(exKategori.equals("Naskah Kuno dan Prasati")){
            spinnerItemKategori = 5;
        }if(exKategori.equals("Ornamen")){
            spinnerItemKategori = 6;
        }if(exKategori.equals("Pakaian Tradisional")){
            spinnerItemKategori = 7;
        }if(exKategori.equals("Permainan Tradisional")){
            spinnerItemKategori = 8;
        }if(exKategori.equals("Produk Arsitektur")){
            spinnerItemKategori = 9;
        }if(exKategori.equals("Ritual")){
            spinnerItemKategori = 10;
        }if(exKategori.equals("Seni Pertunjukan")){
            spinnerItemKategori = 11;
        }if(exKategori.equals("Senjata dan Alat Perang")){
            spinnerItemKategori = 12;
        }if(exKategori.equals("Tarian")){
            spinnerItemKategori = 13;
        }if(exKategori.equals("Pengobatan")){
            spinnerItemKategori = 14;
        }
    }

    public void updateProfile(){
        if(sJudul.isEmpty() || sKategori.isEmpty() ||
                sProvisi.isEmpty() || sDaerah.isEmpty() ||
                sKeterangan.isEmpty()
        ){
            Toast.makeText(EditEntryActivity.this, "Isi seluruh kolom!", Toast.LENGTH_LONG).show();

        } else {
            //loading
            loading = ProgressDialog.show(EditEntryActivity.this,
                    null,
                    "Mohon Tunggu",
                    true,
                    false);

            if (fotoEntryUri != null){

                compressedImage();

                final String randomName = UUID.randomUUID().toString();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageData = baos.toByteArray();

                StorageReference reference = storageReference.child("foto_entry")
                        .child(randomName + ".jpg");
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

    private void setUser(){
        fStore.collection("Entry").document(exId)
                .update(
                        "judul_entry", sJudul,
                        "kategori_entry", sKategori,
                        "provinsi_entry", sProvisi,
                        "daerah_entri", sDaerah,
                        "keterangan_entry", sKeterangan
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditEntryActivity.this, "Updated succesfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditEntryActivity.this, MainActivity.class);
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
                        Toast.makeText(EditEntryActivity.this, "Profile image failed...", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void setUserUri(Uri uri) {
        String downloadUriString = uri.toString();

        FirebaseUser user = fAuth.getCurrentUser();

        fStore.collection("Entry").document(exId)
                .update(
                        "judul_entry", sJudul,
                        "kategori_entry", sKategori,
                        "provinsi_entry", sProvisi,
                        "daerah_entri", sDaerah,
                        "keterangan_entry", sKeterangan,
                        "foto_url_entry", downloadUriString
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditEntryActivity.this, "Updated succesfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditEntryActivity.this, MainActivity.class);
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
                        Toast.makeText(EditEntryActivity.this, "Profile image failed...", Toast.LENGTH_SHORT).show();
                    }
                });
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
    
    private void compressedImage() {
        File newImageFile = new File(fotoEntryUri.getPath());
        try {
            compressedImageFile = new Compressor(EditEntryActivity.this)
                    .setMaxHeight(720)
                    .setMaxWidth(720)
                    .setQuality(50)
                    .compressToBitmap(newImageFile);

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(EditEntryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(EditEntryActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        } else {
            ActivityCompat.requestPermissions(EditEntryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void ChoseImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512, 512)
                .setAspectRatio(1, 1)
                .start(EditEntryActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(EditEntryActivity.this, "Izin diberian", Toast.LENGTH_SHORT).show();
                ChoseImage();
            } else {
                Toast.makeText(EditEntryActivity.this, "Izin tidak diberian", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                fotoEntryUri = result.getUri();
                ivFotoEntry.setImageURI(fotoEntryUri);

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
