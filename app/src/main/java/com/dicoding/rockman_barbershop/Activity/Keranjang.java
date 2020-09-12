package com.dicoding.rockman_barbershop.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dicoding.rockman_barbershop.Activity.add.TambahAntrian;
import com.dicoding.rockman_barbershop.Interface.iFireStore;
import com.dicoding.rockman_barbershop.MainActivity;
import com.dicoding.rockman_barbershop.R;
import com.dicoding.rockman_barbershop.adapter.CollectionAdapter;
import com.dicoding.rockman_barbershop.adapter.KeranjangAdapter;
import com.dicoding.rockman_barbershop.model.Category;
import com.dicoding.rockman_barbershop.model.Pesanan;
import com.dicoding.rockman_barbershop.model.Produk;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.pdf.PdfWriter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Keranjang extends AppCompatActivity {


    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = mDatabase.collection("Pesanan");
    private CollectionReference transaksiReference = mDatabase.collection("Transaksi");
    private CollectionReference profulRef = mDatabase.collection("users");

    private FirebaseFirestore fStore;
    TextView tvTotalHarga, cek;
    Button btnTotalHarga, cart;
    boolean kosong = false;

    ProgressDialog loading;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    String stringCekStock = null;
    private KeranjangAdapter keranjangAdapter;
    private String userIds, sPhone, sTipe, totalTransaksi, totalPesanan, kodePesanan, kodeTransaksi;
    private int intTotalTransaksi, totalHargaTransaksi, iPhone;
    private int intTotalPesanan, totalHargaPesanan;
    private RecyclerView recyclerView;
    private ImageButton download, keranjang;

    private String statusAntrian, finish = "Finished", finishing = "Finishing up...", check, stringCount, stringLencana, lencana;
    private Boolean succes = false;
    private String formattedDate;
    MediaPlayer mediaPlayer;

    PdfWriter writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keranjang);
        mediaPlayer= MediaPlayer.create(Keranjang.this, R.raw.transaksi_dilakukan);

        userIds = fAuth.getCurrentUser().getUid();

        recyclerView = findViewById(R.id.rv_order);
        cart = findViewById(R.id.button_cart);
        tvTotalHarga = findViewById(R.id.total_harga);
        cek = findViewById(R.id.cek);

        total();

//        keranjang = findViewById(R.id.keranjang);

//        keranjang.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        loadOrderList();
        loading = ProgressDialog.show(Keranjang.this,
                null,
                "Mohon Tunggu",
                true,
                false);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        formattedDate = df.format(c);

//        cekTransaksi();
        setUpRecyclerView();

        keranjangAdapter.startListening();

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loading = ProgressDialog.show(Keranjang.this,
                        null,
                        "Mohon Tunggu",
                        true,
                        false);
                cekTransaksiMenunggu();

                if (statusAntrian!= null && statusAntrian.equalsIgnoreCase("menunggu")){
                    Toast.makeText(Keranjang.this, "Status Transaksi anda masih menunggu", Toast.LENGTH_SHORT).show();

                } else {
                    setProfile();
                }
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Keranjang.this);
                builder.setMessage("Yakin ingin menghapus produk?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                keranjangAdapter.deleteItem(viewHolder.getAdapterPosition());

                                Toast.makeText(Keranjang.this, "Produk dihapus", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Keranjang.this, EditCategoryActivity.class);
                                startActivity(intent);
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }).attachToRecyclerView(recyclerView);


    }



    private void setProfile() {
        DocumentReference documentReference = mDatabase.document("users/" + userIds);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            /** Memasukan data ke TextView **/
                            String Sphone = documentSnapshot.getString("phone_profile");
                            cekTransaksi(Sphone);
                        } else {
                            Toast.makeText(Keranjang.this, "Document Tidak Ada", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Keranjang.this, "Document Error", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }


    private void cekTransaksi(String telp) {
        transaksiReference
                .whereEqualTo("user_id", userIds)
                .whereEqualTo("status", "menunggu")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            intTotalTransaksi = task.getResult().size() + 1;
                            totalTransaksi = Integer.valueOf(intTotalTransaksi).toString();

                            if (intTotalTransaksi < 10) {
                                kodeTransaksi = telp + "-" + totalTransaksi;
                                konfirmasiPesanan(kodeTransaksi);
                            } else if (intTotalTransaksi < 100) {
                                kodeTransaksi = telp + "-" + totalTransaksi;
                                konfirmasiPesanan(kodeTransaksi);
                            } else if (intTotalTransaksi < 1000) {
                                kodeTransaksi = telp + "-" + totalTransaksi;
                                konfirmasiPesanan(kodeTransaksi);
                            } else if (intTotalTransaksi < 10000) {
                                kodeTransaksi = telp + "-" + totalTransaksi;
                                konfirmasiPesanan(kodeTransaksi);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void konfirmasiPesanan(String kode) {

        DocumentReference collectionReference = FirebaseFirestore.getInstance()
                .collection("Transaksi").document(kode);

        Map<String, Object> postMap = new HashMap<>();
        postMap.put("kode_transaksi", kode);
        postMap.put("user_id", userIds);
        postMap.put("tanggal_transaksi", formattedDate);
        postMap.put("status", "menunggu");
        postMap.put("harga", totalHargaTransaksi);
        Toast.makeText(Keranjang.this, userIds, Toast.LENGTH_SHORT).show();
        collectionReference.set(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Query PesananReference = FirebaseFirestore.getInstance()
                        .collection("Pesanan")
                        .whereEqualTo("user_id", userIds)
                        .whereEqualTo("status", "menunggu");
                PesananReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String fotoBarang = document.getString("foto_barang");
                                int hargaBarang = document.getLong("harga_barang").intValue();
                                String kodeBarang = document.getString("kode_barang");
                                String kodePesanan = document.getString("kode_pesanan");
                                String kodeTransaksi = document.getString("kode_transaksi");
                                int kuantitas = document.getLong("kuantitas").intValue();
                                String namaBarang = document.getString("nama_barang");
                                String userID = document.getString("user_id");
                                String status = document.getString("status");
                                int totalHarga = document.getLong("total_harga").intValue();
                                editPesanan(fotoBarang, hargaBarang, kodeBarang, kodePesanan,
                                        kodeTransaksi, kuantitas, namaBarang, status, totalHarga, userID, kode);
                            }

//                            Toast.makeText(Keranjang.this,
//                                    "Berhasil edit produk", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Keranjang.this, "Gagal melakukan transaksi", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void editPesanan(String fotoBarang, int hargaBarang, String kodeBarang, String kodePesanan, String kodeTransaksi, int kuantitas, String namaBarang, String status, int totalHarga, String userID, String kodeEdit) {


        DocumentReference pesananReF = FirebaseFirestore.getInstance()
                .collection("Pesanan").document(kodePesanan);
        Map<String, Object> mapP = new HashMap<>();
        mapP.put("kode_pesanan", kodePesanan);
        mapP.put("kode_barang", kodeBarang);
        mapP.put("nama_barang", namaBarang);
        mapP.put("foto_barang", fotoBarang);
        mapP.put("user_id", userID);
        mapP.put("harga_barang", hargaBarang);
        mapP.put("total_harga", totalHarga);
        mapP.put("kuantitas", kuantitas);
        mapP.put("kode_transaksi", kodeEdit);
        mapP.put("status", "selesai");

        pesananReF.set(mapP).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String kodeTr = kodeEdit;
                editStock(kodeTr);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(Keranjang.this,
                        "Gagal edit kode transaksi produk", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cekTransaksiMenunggu() {
        collectionReference
                .whereEqualTo("kode_user", userIds)
                .whereEqualTo("tanggal_antrian", formattedDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            /** Memasukan data ke TextView **/

                            for (QueryDocumentSnapshot document : task.getResult()) {
//                              Log.d(TAG, document.getId() + " => " + document.getData());
                                statusAntrian = document.getString("status");

                            }
                        }
                    }
                });
    }

    private void total() {
        collectionReference
                .whereEqualTo("user_id", userIds)
                .whereEqualTo("status", "menunggu")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int total = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        int harga = document.getLong("total_harga").intValue();
                        total += harga;
                    }
                    totalHargaTransaksi = total;
                    String totalString = Integer.toString(total);
                    tvTotalHarga.setText(totalString);
                    Log.d("TAG", String.valueOf(total));
                }
            }
        });
    }

    private void editStock(String kodeTransaksi) {


        Query PesananReference = FirebaseFirestore.getInstance()
                .collection("Pesanan")
                .whereEqualTo("user_id", userIds)
                .whereEqualTo("kode_transaksi", kodeTransaksi);
        PesananReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String kodeBarang = document.getString("kode_barang");
                        int kuantitas = document.getLong("kuantitas").intValue();

                        mDatabase.collection("Produk").document(kodeBarang)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot document = task.getResult();
                                if (task.isSuccessful()) {
                                    int stock = document.getLong("stock_barang").intValue();
                                    int total = stock - kuantitas;

                                    mDatabase.collection("Produk").document(kodeBarang)
                                            .update(
                                                    "stock_barang", total
                                            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            mediaPlayer.start();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Keranjang.this, "Stock gagal di perbarui", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });

                    }

                }
            }
        });
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void setUpRecyclerView() {

        Query query = collectionReference
                .whereEqualTo("user_id", userIds)
                .whereEqualTo("status", "menunggu");

        FirestoreRecyclerOptions<Pesanan> options = new FirestoreRecyclerOptions.Builder<Pesanan>()
                .setQuery(query, Pesanan.class)
                .build();

        keranjangAdapter = new KeranjangAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Keranjang.this));
//        recyclerView.padding
        recyclerView.setAdapter(keranjangAdapter);
        loading.dismiss();

        keranjangAdapter.setOnItemClickListener(new KeranjangAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Produk produk = documentSnapshot.toObject(Produk.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();

                Intent intent = new Intent(getApplicationContext(), EditKuantitas.class);
                intent.putExtra("ExtraIdKuantitas", id);
                intent.putExtra("ExtraEditKuantitas", 0);
                intent.putExtra("ExtraHargaKuantitas", 0);
                startActivity(intent);
                finish();

            }
        });
    }

//    @Override
//    public void onFireStoreLoadSuccess(List<Category> categories) {
//
//    }
//
//    @Override
//    public void onFireStoreLoadFailed(String message) {
//        Toast.makeText(Keranjang.this, "Gagal memuat kategori", Toast.LENGTH_SHORT).show();
//    }

//    @Override
//    public void onEvent(String where) {
//        tvTotalHarga.setText(where);
//    }
}