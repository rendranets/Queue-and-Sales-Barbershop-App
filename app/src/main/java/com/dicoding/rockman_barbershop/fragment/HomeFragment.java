package com.dicoding.rockman_barbershop.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dicoding.rockman_barbershop.Activity.Keranjang;
import com.dicoding.rockman_barbershop.Activity.add.TambahAntrian;
import com.dicoding.rockman_barbershop.MainActivity;
import com.dicoding.rockman_barbershop.R;
import com.dicoding.rockman_barbershop.adapter.HomeAdapter;
import com.dicoding.rockman_barbershop.model.Antrian;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

//

//import com.dicoding.pdbi.adapter.RecentAdapter;

public class HomeFragment extends Fragment {

    public final static String ExtraId = "extra_id";
    public static String idLocation;
    public static String setLocation, nomorAntrianAnda, nomorTotalAntrian, nomorAntrianBerjalan;
    public static TextView set_location, nomor_antrian_anda, nomor_total_antrian, nomor_antrian_berjalan, nama_antrian_berjalan;
    public static String dataCategory = null;
    public int totalAntrian;
    String statusAntrian = "kosong";
    private FirebaseAuth fAuth;
    private String formattedDate;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = mDatabase.collection("Antrian");
    private HomeAdapter homeAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView inputSearch;
    private RecyclerView recyclerView;
    private String TAG, userId, antrianBerjalanId,Stipe;
    FloatingActionButton FBantrian;
    private CardView cvAntrianBerjalan;
    ProgressDialog loading;
    ImageButton keranjang;
    private TextView antrianSaya, antrianTersisa, antrianSaatIni;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        FBantrian = v.findViewById(R.id.main_add_fab);

        fAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = fAuth.getCurrentUser();
        if(mFirebaseUser != null) {
            userId = mFirebaseUser.getUid(); //Do what you need to do with the id
        }

        keranjang = v.findViewById(R.id.image_button_keranjang);
        recyclerView = v.findViewById(R.id.rv_recent_entry);
        set_location = v.findViewById(R.id.set_location);
        nomor_antrian_anda = v.findViewById(R.id.nomor_antrian_anda);
        nomor_total_antrian = v.findViewById(R.id.nomor_sisa_antrian);
        nomor_antrian_berjalan = v.findViewById(R.id.nomor_antrian_berjalan);
        nama_antrian_berjalan = v.findViewById(R.id.nama_antrian_berjalan);
        cvAntrianBerjalan = v.findViewById(R.id.antrian_berjalan);

        swipeRefreshLayout = v.findViewById(R.id.swipe_refresher_home);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                homeAdapter.startListening();
                homeAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        formattedDate = df.format(c);

        setUpRecyclerView();
        setAntrianAnda();
        setTotalAntrian();
        setAntrianBerjalan();

        homeAdapter.startListening();
        cekProfile();

        swipeRefreshLayout = v.findViewById(R.id.swipe_refresher_home);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                homeAdapter.startListening();
                homeAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        cvAntrianBerjalan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading = ProgressDialog.show(getActivity(),
                        null,
                        "Mohon Tunggu",
                        true,
                        false);

                collectionReference.document(antrianBerjalanId)
                        .update(
                                "status", "selesai"
                        )
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "Status Antrian Diubah", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
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
                                Toast.makeText(getActivity(), "Profile image failed...", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        FBantrian.setOnClickListener(v1 -> {
            cekAntrian();
//            if (statusAntrian!= null && statusAntrian.equalsIgnoreCase("Menunggu")){
//                Toast.makeText(getActivity(), "Status Antrian anda masih menunggu", Toast.LENGTH_SHORT).show();
//
//            } else if(statusAntrian!= null && statusAntrian.equalsIgnoreCase("kosong")) {
//                Intent intent = new Intent(getActivity(), TambahAntrian.class);
//                startActivity(intent);
//            }
        });

        keranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Keranjang.class);
                startActivity(intent);
            }
        });
        return v;
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

                            if (Stipe!= null && Stipe.equalsIgnoreCase("admin")){
                                cvAntrianBerjalan.setClickable(true);

                            } else {
                                cvAntrianBerjalan.setClickable(false);

                            }

//                            progressBar.setVisibility(View.GONE);
                            loading.dismiss();
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


    private void cekAntrian() {
        collectionReference
                .whereEqualTo("kode_user", userId)
                .whereEqualTo("tanggal_antrian", formattedDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            /** Memasukan data ke TextView **/
                            String statusAntrian = "belum";
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                              Log.d(TAG, document.getId() + " => " + document.getData());
                                statusAntrian = document.getString("status");

                            }
                            if (statusAntrian.equals("menunggu")){
                                Toast.makeText(getActivity(), "Status Antrian anda masih menunggu", Toast.LENGTH_SHORT).show();
                            }
                            else if (statusAntrian.equals("belum")){
                                    Intent intent = new Intent(getActivity(), TambahAntrian.class);
                                    startActivity(intent);                                }                            }
                    }
                });
    }

    private void setAntrianAnda() {

        collectionReference
                .whereEqualTo("kode_user", userId)
                .whereEqualTo("tanggal_antrian", formattedDate)
                .whereEqualTo("status", "menunggu")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            /** Memasukan data ke TextView **/

                            for (QueryDocumentSnapshot document : task.getResult()) {
//                              Log.d(TAG, document.getId() + " => " + document.getData());
                                nomorAntrianAnda = document.getString("kode_antrian");

                                nomor_antrian_anda.setText(nomorAntrianAnda);
                            }
                    }
                    }
                });
    }

    private void setAntrianBerjalan() {

         collectionReference
                 .whereEqualTo("tanggal_antrian", formattedDate)
                 .whereEqualTo("status", "menunggu")
                 .orderBy("nomor_antrian", Query.Direction.ASCENDING).limit(1)
//                 .orderBy("kode_antrian", Query.Direction.DESCENDING)
//                 .orderBy("nomor_antrian", Query.Direction.ASCENDING)
                 .get()
                 .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            /** Memasukan data ke TextView **/

                            for (QueryDocumentSnapshot document : task.getResult()) {
//                              Log.d(TAG, document.getId() + " => " + document.getData());
                                nomorAntrianBerjalan = document.getString("kode_antrian");
                                String namaAntrianBerjalan;
                                antrianBerjalanId = document.getId();
                                namaAntrianBerjalan = document.getString("nama_user");

                                nomor_antrian_berjalan.setText(nomorAntrianBerjalan);
                                nama_antrian_berjalan.setText(namaAntrianBerjalan);
//                                nomor_total_antrian.setText(nomorAntrianBerjalan);
                            }
                        }
                    }
                });
    }

    private void setTotalAntrian() {

        collectionReference
                .whereEqualTo("status", "menunggu")
                .whereEqualTo("tanggal_antrian", formattedDate)
//                .orderBy("nomor_antrian", Query.Direction.ASCENDING).limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            /** Memasukan data ke TextView **/

                            totalAntrian = task.getResult().size();
                            nomorTotalAntrian = Integer.valueOf(totalAntrian).toString();
                            nomor_total_antrian.setText(nomorTotalAntrian);

                        }
                    }
                });
    }


    public void setUpRecyclerView(){

//        Query query = collectionReference.orderBy("tanggal_entry", Query.Direction.DESCENDING)
//                .orderBy("judul_entry").startAt(data).endAt(data+"\uf8ff");
        Query query = null;

        query = collectionReference.whereEqualTo("tanggal_antrian", formattedDate)
//                .whereEqualTo("status", "menunggu")
                .orderBy("status", Query.Direction.ASCENDING)
                .orderBy("nomor_antrian", Query.Direction.ASCENDING);

            FirestoreRecyclerOptions<Antrian> options = new FirestoreRecyclerOptions.Builder<Antrian>()
                .setQuery(query, Antrian.class)
                .build();

        homeAdapter = new HomeAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(homeAdapter);
//        loading.dismiss();

    }


}
