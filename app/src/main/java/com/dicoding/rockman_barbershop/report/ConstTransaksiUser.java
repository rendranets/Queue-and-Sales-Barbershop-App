package com.dicoding.rockman_barbershop.report;


import android.content.Context;

import com.dicoding.rockman_barbershop.adapter.CollectionAdapter;
import com.dicoding.rockman_barbershop.model.RiwayatTransaksi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ConstTransaksiUser {
    static FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    static CollectionReference userReference = mDatabase.collection("Pesanan");
    static FirebaseAuth fAuth = FirebaseAuth.getInstance();
    CollectionAdapter collectionAdapter;
    static String userIds;
    public static String kodeTransaksi;
//    public static String data = "";
    public static ArrayList<RiwayatTransaksi> transaksiUserList(Context context) {
        ArrayList<RiwayatTransaksi> listItems = new ArrayList<>();
        userIds = fAuth.getCurrentUser().getUid();


//        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
//        UserReference userReference = mDatabase.collection("Entry");
//        UserAdapter collectionAdapter;
//        String userId;

        userReference.whereEqualTo("kode_transaksi", kodeTransaksi)
                .orderBy("nama_barang", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException
                    e) {
                if (e != null) {
                    return;
                }

                int n = 0;
//                String data = "";
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    RiwayatTransaksi LUser = documentSnapshot.toObject(RiwayatTransaksi.class);
                    //              User.setDocumentId(documentSnapshot.getId());/
                    String id = LUser.getUser_id();
                    String kode = LUser.getKode_transaksi();
                    String status = LUser.getStatus();
                    String tanggal = LUser.getTanggal_transaksi();
                    String user = LUser.getUser_id();
                    int harga = LUser.getHarga();

                    n++;
                    listItems.add(new RiwayatTransaksi(harga,kode,status,tanggal,user));
//                    data += String.valueOf(n) + "." + nama  + " ";
                }
//                Toast.makeText(context, data+userIds, Toast.LENGTH_SHORT).show();

            }
        });
        return listItems;
    }
}
