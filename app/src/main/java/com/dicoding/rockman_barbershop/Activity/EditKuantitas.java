package com.dicoding.rockman_barbershop.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dicoding.rockman_barbershop.Activity.UserAuth.Login;
import com.dicoding.rockman_barbershop.MainActivity;
import com.dicoding.rockman_barbershop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditKuantitas extends AppCompatActivity {


    public static final String TAG = "TAG";

    EditText etKuantitas;
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
    String name, kode, kuantitasString, url, id;
    int kuantitasInt, harga;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kuantitas);

        mediaPlayer= MediaPlayer.create(EditKuantitas.this, R.raw.kuantitas);
        mCancel = findViewById(R.id.cancel_edit_Kuantitas_Produk);
        mEditCategoryBtn = findViewById(R.id.confirm_edit_Kuantitas_Produk);
        etKuantitas = findViewById(R.id.edit_kuantitas_terpilih);

        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        id = getIntent().getStringExtra("ExtraIdKuantitas");


        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Keranjang.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        mEditCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                kuantitasString = etKuantitas.getText().toString();
                kuantitasInt = Integer.parseInt(kuantitasString);

                if (kuantitasInt <= 0){
                    kuantitasInt = 1;
                }

                loading = ProgressDialog.show(EditKuantitas.this,
                        null,
                        "Mohon Tunggu",
                        true,
                        false);
                cekHarga(kuantitasInt);
            }
        });
    }

    public void cekHarga(int kuantitas) {
        DocumentReference documentReference = fStore.collection("Pesanan").document(id);
        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        if (task.isSuccessful()) {
                            /** Memasukan data ke TextView **/

                            harga = document.getLong("harga_barang").intValue();

                            int TotalHarga = harga * kuantitas;

                            DocumentReference collectionReference = FirebaseFirestore.getInstance()
                                    .collection("Pesanan").document(id);
                            Map<String, Object> mapProduk = new HashMap<>();
                            mapProduk.put("kuantitas", kuantitasInt);
                            mapProduk.put("total_harga", TotalHarga);

                            collectionReference.update(mapProduk).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    mediaPlayer.start();


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(EditKuantitas.this,
                                            "Gagal mengubah kuantitas", Toast.LENGTH_SHORT).show();

                                }
                            });

                            loading.dismiss();
                            Intent intent = new Intent(getApplicationContext(), Keranjang.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(EditKuantitas.this, "Document Tidak Ada", Toast.LENGTH_SHORT).show();
                            loading.dismiss();
                        }
                    }
                });
    }
}