package com.dicoding.rockman_barbershop.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dicoding.rockman_barbershop.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import id.zelory.compressor.Compressor;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class TambahProduk extends AppCompatActivity {
    public static final String TAG = "TAG";
    @SuppressLint("StaticFieldLeak")
    public static ImageView fotoEntry;
    public static Uri fotoEntryUrl = null;
    private EditText etStock, etKeterangan, etHarga, etNamaBarang;
    private String userId, stringProvinsi, stringTotalProduk, kodeBarang;
    private Spinner spProvinsi, spKategori;
    private ProgressDialog loading;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference profileReference = fStore.collection("users");
    private CollectionReference collectionReference = fStore.collection("Location");
    private Bitmap compressedImageFile;
    private int totalProduk;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_produk);

        etNamaBarang = findViewById(R.id.add_barang);
        etStock = findViewById(R.id.add_stock);
        etHarga = findViewById(R.id.add_harga);
        etKeterangan = findViewById(R.id.add_keterangan);
        fotoEntry = findViewById(R.id.add_entry_image);
        mediaPlayer= MediaPlayer.create(TambahProduk.this, R.raw.menambah_barang);

        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        fotoEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(TambahProduk.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        ChoseImage();
                    } else {
                        requestStoragePermission();
                    }
                }
            }
        });

        findViewById(R.id.button_add).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                setAntrian();

                String addNamaBarang = etNamaBarang.getText().toString();

                final String editStockString = etStock.getText().toString().trim();
                final String editHargaString = etHarga.getText().toString().trim();

                Number addHarga = Integer.parseInt(etHarga.getText().toString());
                Number addStock = Integer.parseInt(etStock.getText().toString());
                String addKeterangan = etKeterangan.getText().toString();
                String addBy = userId;

                if (addNamaBarang.trim().isEmpty() ||
                        editHargaString.trim().isEmpty() || fotoEntryUrl == null ||
                        editStockString.trim().isEmpty() || addKeterangan.trim().isEmpty()) {
                    Toast.makeText(TambahProduk.this, "Isi seluruh data yang ada, termasuk foto!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                        loading = ProgressDialog.show(TambahProduk.this,
                                null,
                                "Mohon Tunggu",
                                true,
                                false);

                    compressedImage();

                    final String randomName = UUID.randomUUID().toString();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageData = baos.toByteArray();

                    StorageReference ref = storageReference.child("foto_barang")
                            .child(randomName + ".jpg");
                    UploadTask image_path = ref.putBytes(imageData);

                    Task<Uri> urlTask = image_path.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {

                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                String downloadUriString = downloadUri.toString();

                                DocumentReference collectionReference = FirebaseFirestore.getInstance()
                                        .collection("Produk").document(kodeBarang);
                                Map<String, Object> postMap = new HashMap<>();
                                postMap.put("nama_barang", addNamaBarang);
                                postMap.put("stock_barang", addStock);
                                postMap.put("harga_barang", addHarga);
                                postMap.put("keterangan_barang", addKeterangan);
                                postMap.put("kode_barang", kodeBarang);
                                postMap.put("foto_url_barang", downloadUriString);
                                postMap.put("tanggal_barang", FieldValue.serverTimestamp());

                                collectionReference.set(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        etNamaBarang.setText("");
                                        etStock.setText("");
                                        etKeterangan.setText("");
                                        etHarga.setText("");
                                        Toast.makeText(TambahProduk.this, "Produk berhasil di tambah", Toast.LENGTH_SHORT).show();
                                        loading.dismiss();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TambahProduk.this, "Produk gagal di tambah", Toast.LENGTH_SHORT).show();
                            loading.dismiss();
                        }
                    });
                }
            }
        }
        );

        findViewById(R.id.cancel_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etNamaBarang.setText("");
                spKategori.setSelected(false);
                spProvinsi.setSelected(false);
                etStock.setText("");
                etKeterangan.setText("");
                fotoEntry.setImageResource(R.drawable.add_image);
            }
        });
    }

    private void setAntrian() {
        CollectionReference collectionReference = (CollectionReference) fStore
                .collection("Produk");

        collectionReference
//                .whereEqualTo("status", "menunggu")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            totalProduk = task.getResult().size() + 1 ;
                            stringTotalProduk = Integer.valueOf(totalProduk).toString();

                            if (totalProduk < 10) {
                                kodeBarang = "BR00" + stringTotalProduk ;
                            }
                            else if (totalProduk <100){
                                kodeBarang = "BR0"+ stringTotalProduk;
                            }
                            else if (totalProduk <1000){
                                kodeBarang = "BR"+stringTotalProduk;
                            }
                            


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }



    private void compressedImage() {
        File newImageFile = new File(fotoEntryUrl.getPath());
        try {
            compressedImageFile = new Compressor(TambahProduk.this)
                    .setMaxHeight(720)
                    .setMaxWidth(720)
                    .setQuality(50)
                    .compressToBitmap(newImageFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(TambahProduk.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(TambahProduk.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            ActivityCompat.requestPermissions(TambahProduk.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void ChoseImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512, 512)
                .setAspectRatio(1, 1)
                .start(TambahProduk.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(TambahProduk.this, "Izin diberian", Toast.LENGTH_SHORT).show();
                ChoseImage();
            } else {
                Toast.makeText(TambahProduk.this, "Izin tidak diberian", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                fotoEntryUrl = result.getUri();
                fotoEntry.setImageURI(fotoEntryUrl);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();


            }
        }
    }
}
