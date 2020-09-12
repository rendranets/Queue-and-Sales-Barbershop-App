package com.dicoding.rockman_barbershop.Activity.add;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dicoding.rockman_barbershop.Activity.TambahProduk;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class TambahAntrian extends AppCompatActivity {
    private TextView fullName, nomorAntrian;
    private String Sname, nomorAntrianUser, stringAntrianMenunggu;

    private String formattedDate;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;

    private Button BTNtambah, BTNcencel ;
    private String userId;
    private ProgressDialog loading;
    private ProgressBar progressBar;

    private int antrianMenunggu;
    private CircleImageView profileImage;
    private Uri profileImageUri = null;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_antrian);

        mediaPlayer= MediaPlayer.create(TambahAntrian.this, R.raw.antrian);

        BTNtambah = findViewById(R.id.button_tambah_antrian);
        BTNcencel = findViewById(R.id.button_batal_antrian);
        nomorAntrian = findViewById(R.id.tv_nomor_antrian);
        fullName = findViewById(R.id.tv_nama_antrian);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        formattedDate = df.format(c);

        setProfile();
        setAntrian();


        BTNtambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tambahAntrian();
            }
        });

        BTNcencel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }


    private void tambahAntrian(){

        loading = ProgressDialog.show(this,
                null,
                "Mohon Tunggu",
                true,
                false);

        CollectionReference collectionReference = FirebaseFirestore.getInstance()
                .collection("Antrian");

        Map<String, Object> postMap = new HashMap<>();
        postMap.put("kode_antrian", nomorAntrianUser);
        postMap.put("kode_user", userId);
        postMap.put("nama_user", Sname);
        postMap.put("status", "menunggu");
        postMap.put("tanggal_antrian", formattedDate);
        postMap.put("nomor_antrian", antrianMenunggu);

        collectionReference.add(postMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        mediaPlayer.start();
                        loading.dismiss();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TambahAntrian.this, "Antrian berhasil di tambah", Toast.LENGTH_SHORT).show();
                        loading.dismiss();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
    }


    private void setProfile(){

        loading = ProgressDialog.show(this,
                null,
                "Mohon Tunggu",
                true,
                false);

        DocumentReference documentReference = fStore.document("users/" + userId);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            /** Memasukan data ke TextView **/

                            Sname = documentSnapshot.getString("nama_profile");
                            fullName.setText(Sname);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TambahAntrian.this, "Tambah Antrian  Error", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
//        setAntrian();

    }

    private void setAntrian() {
        CollectionReference collectionReference = (CollectionReference) fStore
                .collection("Antrian");

        collectionReference
//                .whereEqualTo("status", "menunggu")
                .whereEqualTo("tanggal_antrian", formattedDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            antrianMenunggu = task.getResult().size() + 1 ;
                            stringAntrianMenunggu = Integer.valueOf(antrianMenunggu).toString();

                            if (antrianMenunggu < 10) {
                                nomorAntrianUser = "00" + stringAntrianMenunggu ;
                            }
                            else if (antrianMenunggu <100){
                                nomorAntrianUser = "0"+ stringAntrianMenunggu;
                            }
                            else if (antrianMenunggu <1000){
                                nomorAntrianUser = stringAntrianMenunggu;
                            }
                            nomorAntrian.setText(nomorAntrianUser);


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        loading.dismiss();
    }


}