
package com.dicoding.rockman_barbershop.Activity;

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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dicoding.rockman_barbershop.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import id.zelory.compressor.Compressor;

public class EditProdukDipilih extends AppCompatActivity {

    public static final String TAG = "TAG";
    public final static String ExtraId = "extra_Id";
    public final static String ExtraNama = "extra_nama";
    public final static String ExtraKode = "extra_kode";

    public final static String ExtraKeterangan = "extra_keterangan";
//    public final static String ExtraTanggal = "extra_tanggal";
    public final static String ExtraUrl = "extra_url";
    EditText etNamaBarang,etStock,etHarga, etKeterangan;
    ImageView fotoEntry;
    Button mEditCategoryBtn, mCancel;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;

    String userID;
    ProgressDialog loading;
    ProgressBar progressBar;
    
    //    Uri fotoEntryUri = null;
    Uri fotoEntryUri = null;
    Bitmap compressedImageFile;
    private boolean isChanged = false;
    String name, kode, keterangan, url, id;
    int stock, harga;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_produk_dipilih);
        mediaPlayer= MediaPlayer.create(EditProdukDipilih.this, R.raw.edit_barang);

        etNamaBarang = findViewById(R.id.edit_barang);
        etStock = findViewById(R.id.edit_stock);
        etHarga = findViewById(R.id.edit_harga);
        etKeterangan = findViewById(R.id.edit_keterangan);
        fotoEntry = findViewById(R.id.edit_entry_image);
        mEditCategoryBtn = findViewById(R.id.confirm_edit);
        mCancel = findViewById(R.id.cancel_edit);
        
        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

         name = getIntent().getStringExtra(ExtraNama);
         kode = getIntent().getStringExtra(ExtraKode);


         stock = getIntent().getIntExtra("ExtraStock", 0);
         harga = getIntent().getIntExtra("ExtraHarga", 0);
         keterangan = getIntent().getStringExtra(ExtraKeterangan);
         url = getIntent().getStringExtra(ExtraUrl);
         id = getIntent().getStringExtra(ExtraId);
//        Number num = 0;

        String sStock = Integer.toString(stock);
        String sHarga = Integer.toString(harga);

        etNamaBarang.setText(name);
        etStock.setText(sStock);
        etHarga.setText(sHarga);
        etKeterangan.setText(keterangan);

        if (url != null) {
            try {
                loading = ProgressDialog.show(EditProdukDipilih.this,
                        null,
                        "Mohon Tunggu",
                        true,
                        false);

                Glide.with(EditProdukDipilih.this)
                        .load(url)
                        .placeholder(R.drawable.add_image)
                        .into(fotoEntry);
                
                url = null;
                loading.dismiss();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        } if (url == null){

            fotoEntry.setImageURI(fotoEntryUri);


        }


            fotoEntry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(EditProdukDipilih.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED) {
                            ChoseImage();
                        } else {
                            requestStoragePermission();
                        }
                    }
                }
            });

            mEditCategoryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String editName = etNamaBarang.getText().toString().trim();
                    final String editStockString = etStock.getText().toString().trim();
                    final String editHargaString = etHarga.getText().toString().trim();
                    final Number editStock = Integer.parseInt(etStock.getText().toString().trim());
                    final Number editHarga = Integer.parseInt(etHarga.getText().toString().trim());
                    final String editKeterangan = etKeterangan.getText().toString().trim();

                    /** Cek jika ada kolom yang belum di isi **/
                    if (editName.trim().isEmpty() ) {
                        etNamaBarang.setError("Isi kolom nama barang.");
                        return;
                    }if (editStockString.trim().isEmpty() ) {
                        etStock.setError("Isi kolom nama barang.");
                        return;
                    }if (editHargaString.trim().isEmpty() ) {
                        etHarga.setError("Isi kolom nama barang.");
                        return;
                    }if (editKeterangan.trim().isEmpty() ) {
                        etKeterangan.setError("Isi kolom nama barang.");
                        return;
                    } else {

                        loading = ProgressDialog.show(EditProdukDipilih.this,
                                null,
                                "Mohon Tunggu",
                                true,
                                false);
//                progressBar.setVisibility(View.VISIBLE);
//                final String randomName = UUID.randomUUID().toString();
                        /** Menghubungkan Firebase **/

                        if (fotoEntryUri != null) {

//                    setSize(kategori);
                            registWithPhoto(id, editName, editStock, editHarga, editKeterangan);
                        } else {
                            registWithOutPhoto(id, editName, editStock, editHarga, editKeterangan);

                        }
                    }
                }
            });


            mCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etNamaBarang.setText("");
                    etStock.setText("");
                    etHarga.setText("");
                    etKeterangan.setText("");

                }
            });

    }

    private void registWithPhoto(String id, String nama, Number stock, Number harga, String keterangan) {


        File newImageFile = new File(fotoEntryUri.getPath());
        try {
            compressedImageFile = new Compressor(EditProdukDipilih.this)
                    .setMaxHeight(720)
                    .setMaxWidth(1440)
                    .setQuality(50)
                    .compressToBitmap(newImageFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        final String randomName = UUID.randomUUID().toString();

        StorageReference ref = storageReference.child("foto_produk")
                .child(randomName + ".jpg");
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


                    DocumentReference collectionReference = FirebaseFirestore.getInstance()
                            .collection("Produk").document(id);
                    Map<String, Object> mapProduk = new HashMap<>();
                    mapProduk.put("nama_barang", nama);
                    mapProduk.put("stock_barang", stock);
                    mapProduk.put("harga_barang", harga);
                    mapProduk.put("keterangan_barang", keterangan);
                    mapProduk.put("foto_url_barang", downloadUriString);

                    collectionReference.update(mapProduk).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mediaPlayer.start();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(EditProdukDipilih.this,
                                    "Gagal mengubah produk", Toast.LENGTH_SHORT).show();
                        }
                    });

                    loading.dismiss();

                    Intent intent = new Intent(getApplicationContext(), AdminPanel.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProdukDipilih.this, "Error ! " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                  progressBar.setVisibility(View.GONE);
                loading.dismiss();
            }
        });
        //imagepath

    }

    private void registWithOutPhoto(String id, String nama, Number stock, Number harga, String keterangan) {


        DocumentReference collectionReference = FirebaseFirestore.getInstance()
                .collection("Produk").document(id);
        Map<String, Object> mapProduk = new HashMap<>();
        mapProduk.put("nama_barang", nama);
        mapProduk.put("stock_barang", stock);
        mapProduk.put("harga_barang", harga);
        mapProduk.put("keterangan_barang", keterangan);

        collectionReference.update(mapProduk).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                mediaPlayer.start();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(EditProdukDipilih.this,
                        "Gagal mengubah produkZZz", Toast.LENGTH_SHORT).show();

            }
        });
        loading.dismiss();
        Intent intent = new Intent(getApplicationContext(), AdminPanel.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(EditProdukDipilih.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(EditProdukDipilih.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        } else {
            ActivityCompat.requestPermissions(EditProdukDipilih.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void ChoseImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512, 512)
                .setAspectRatio(2, 1)
                .start(EditProdukDipilih.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(EditProdukDipilih.this, "Izin diberian", Toast.LENGTH_SHORT).show();
                ChoseImage();
            } else {
                Toast.makeText(EditProdukDipilih.this, "Izin tidak diberian", Toast.LENGTH_SHORT).show();

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
                fotoEntry.setImageURI(fotoEntryUri);

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
