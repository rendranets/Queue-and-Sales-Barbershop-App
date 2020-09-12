package com.dicoding.rockman_barbershop.report;


import android.content.Context;
import android.widget.Toast;

import com.dicoding.rockman_barbershop.adapter.CollectionAdapter;
import com.dicoding.rockman_barbershop.model.EntryTerbaru;
import com.dicoding.rockman_barbershop.model.Produk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class ConstProduk {
    static FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    static CollectionReference collectionReference = mDatabase.collection("Produk");
    static FirebaseAuth fAuth = FirebaseAuth.getInstance();
    CollectionAdapter collectionAdapter;
    static String userIds;

//    public static String data = "";
    public static ArrayList<Produk> terbaruList(Context context) {
        ArrayList<Produk> listItems = new ArrayList<>();
        userIds = fAuth.getCurrentUser().getUid();

//        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
//        ProdukReference collectionReference = mDatabase.collection("Entry");
//        ProdukAdapter collectionAdapter;
//        String userId;

        collectionReference.orderBy("nama_barang", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException
                    e) {
                if (e != null) {
                    return;
                }

                int n = 0;
//                String data = "";
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Produk entry = documentSnapshot.toObject(Produk.class);
                    //              entry.setDocumentId(documentSnapshot.getId());/
                    String nama = entry.getNama_barang();
                    String kode = entry.getKode_barang();
                    int harga = entry.getHarga_barang();
                    int stock = entry.getStock_barang();

                    n++;
                    listItems.add(new Produk(kode,nama,harga,stock));
//                    data += String.valueOf(n) + "." + waktu + " ";
                }
//                Toast.makeText(context, data+userIds, Toast.LENGTH_SHORT).show();

            }
        });
        return listItems;
    }
}
