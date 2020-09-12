package com.dicoding.rockman_barbershop.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.dicoding.rockman_barbershop.Activity.UserAuth.Register;
import com.dicoding.rockman_barbershop.MainActivity;
import com.dicoding.rockman_barbershop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class EditStatusTransaksi extends AppCompatActivity {


    public static final String TAG = "TAG";
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = mDatabase.collection("Transaksi");
    EditText etKuantitas;
    ImageView fotoEntry;
    Button mEditCategoryBtn, mCancel;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;

    String userID;
    ProgressDialog loading;

    //    Uri fotoEntryUri = null;
    Uri fotoEntryUri = null;
    Bitmap compressedImageFile;
    private boolean isChanged = false;
    String name, kode, kuantitasString, status, id, statusTransaksi;
    int kuantitasInt, harga;
    Spinner status_transaksi;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_status_transaksi);

        mCancel = findViewById(R.id.cancel_edit_status);
        mEditCategoryBtn = findViewById(R.id.confirm_edit_Kuantitas_Produk);
        mediaPlayer= MediaPlayer.create(EditStatusTransaksi.this, R.raw.edit_status);

        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        id = getIntent().getStringExtra("ExtraStatusTransaksiIdTransaksi");

        status_transaksi = (Spinner) findViewById(R.id.spinnter_status_transaksi);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.
        createFromResource(this, R.array.status_array,
                        android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status_transaksi.setAdapter(adapter1);
        status_transaksi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String status = parent.getItemAtPosition(position).toString();
                mEditCategoryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        setStatus(status);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });


    }

    public void setStatus(String statusTransaksi) {

        loading = ProgressDialog.show(EditStatusTransaksi.this,
                null,
                "Mohon Tunggu",
                true,
                false);

        collectionReference.document(id)
                .update(
                        "status", statusTransaksi
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mediaPlayer.start();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditStatusTransaksi.this, "Status Transaksi Gagal Diubah", Toast.LENGTH_SHORT).show();
                    }
                });

                            loading.dismiss();


                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
    }
}