package com.dicoding.rockman_barbershop.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dicoding.rockman_barbershop.Activity.DetailRiwayatTransaksi;
import com.dicoding.rockman_barbershop.Activity.EditStatusTransaksi;
import com.dicoding.rockman_barbershop.R;
import com.dicoding.rockman_barbershop.adapter.RiwayatTransaksiAdapter;
import com.dicoding.rockman_barbershop.model.RiwayatTransaksi;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class RiwayatTransaksiFragment extends Fragment {
    private EditText etStock, etKeterangan, etHarga, etNamaBarang;
     String userId, stringProvinsi, Stipe;
    private Spinner spProvinsi, spKategori;
    public static final String TAG = "TAG";
    private RiwayatTransaksiAdapter riwayatTransaksiAdapter;
    private RecyclerView recyclerView;
    private Query query;
     ProgressDialog loading;
    SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference profileReference = fStore.collection("users");
    private CollectionReference collectionReference = fStore.collection("Transaksi");

    @SuppressLint("StaticFieldLeak")
    public static ImageView fotoEntry;
    public static Uri fotoEntryUrl = null;
    private Bitmap compressedImageFile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add, container, false);

        recyclerView =  v.findViewById(R.id.rv_riwayat_transaksi);
        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();

        userId = fAuth.getCurrentUser().getUid();
        cekProfile();
//        setUpRecyclerView(Stipe);

        swipeRefreshLayout = v.findViewById(R.id.swipe_refresher_riwayat_transaksi);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                riwayatTransaksiAdapter.startListening();
                riwayatTransaksiAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        return v;
    }

    private void setUpRecyclerView(String tipe) {

        if (tipe!= null && tipe.equalsIgnoreCase("admin")){
           query = collectionReference
                    .orderBy("tanggal_transaksi", Query.Direction.DESCENDING);

        } else {
           query = collectionReference
                    .whereEqualTo("user_id", userId)
                    .orderBy("tanggal_transaksi", Query.Direction.DESCENDING);
        }

        FirestoreRecyclerOptions<RiwayatTransaksi> options = new FirestoreRecyclerOptions.Builder<RiwayatTransaksi>()
                .setQuery(query, RiwayatTransaksi.class)
                .build();


        riwayatTransaksiAdapter = new RiwayatTransaksiAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(riwayatTransaksiAdapter);
        loading.dismiss();

        riwayatTransaksiAdapter.setOnItemClickListener(new RiwayatTransaksiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String id = documentSnapshot.getId();
                String id_transaksi = documentSnapshot.getString("kode_transaksi");
                String user = documentSnapshot.getString("user_id");
                String harga = Integer.toString(documentSnapshot.getLong("harga").intValue());
                String tanggal = documentSnapshot.getString("tanggal_transaksi");
                String status = documentSnapshot.getString("status");

                Intent intent = new Intent(getActivity(), DetailRiwayatTransaksi.class);
                intent.putExtra("ExtraRiwayatTransaksiId", user);
                intent.putExtra("ExtraRiwayatTransaksiIdTransaksi", id_transaksi);
                intent.putExtra("ExtraRiwayatTransaksiTipe", Stipe);
                intent.putExtra("ExtraRiwayatTransaksiHarga", harga);
                intent.putExtra("ExtraRiwayatTransaksiTanggal", tanggal);
                intent.putExtra("ExtraRiwayatTransaksiStatus", status);
                startActivity(intent);
            }
        });
        riwayatTransaksiAdapter.startListening();
        loading.dismiss();
    }

    private void cekProfile(){

        loading = ProgressDialog.show(getActivity(),
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
//                            Slencana = documentSnapshot.getLong("total_entry").intValue();
                            Stipe = documentSnapshot.getString("tipe");
//                            progressBar.setVisibility(View.GONE);
                            setUpRecyclerView(Stipe);
                        } else {
                            Toast.makeText(getActivity(), "Document Tidak Ada", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Document Error", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });

    }

}
