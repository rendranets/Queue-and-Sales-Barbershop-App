package com.dicoding.rockman_barbershop.report;


import android.content.Context;

import com.dicoding.rockman_barbershop.adapter.CollectionAdapter;
import com.dicoding.rockman_barbershop.model.Collection;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ConstCollection {
    static FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    static CollectionReference collectionReference = mDatabase.collection("Entry");
    static FirebaseAuth fAuth = FirebaseAuth.getInstance();
    CollectionAdapter collectionAdapter;
    static String userIds;

//    public static String data = "";
    public static ArrayList<Collection> tempList(Context context) {
        ArrayList<Collection> listItems = new ArrayList<>();
        userIds = fAuth.getCurrentUser().getUid();

//        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
//        CollectionReference collectionReference = mDatabase.collection("Entry");
//        CollectionAdapter collectionAdapter;
//        String userId;

        collectionReference.whereEqualTo("upload_by", userIds).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException
                    e) {
                if (e != null) {
                    return;
                }

                int n = 0;
//                String data = "";
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Collection entry = documentSnapshot.toObject(Collection.class);
                    //              entry.setDocumentId(documentSnapshot.getId());/
                    String title = entry.getJudul_entry();
                    String kategori = entry.getKategori_entry();
                    String provinsi = entry.getProvinsi_entry();
                    String daerah = entry.getDaerah_entri() ;

                    n++;
                    listItems.add(new Collection(title,kategori,provinsi,daerah));
//                    data += String.valueOf(n) + "." + title + " ";
                }
//                Toast.makeText(context, data+userIds, Toast.LENGTH_SHORT).show();

            }
        });
        return listItems;
    }
}
