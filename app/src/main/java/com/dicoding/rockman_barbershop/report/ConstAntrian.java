package com.dicoding.rockman_barbershop.report;


import android.content.Context;

import com.dicoding.rockman_barbershop.adapter.CollectionAdapter;
import com.dicoding.rockman_barbershop.model.Antrian;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;

public class ConstAntrian {
    static FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    static CollectionReference userReference = mDatabase.collection("Antrian");
    static FirebaseAuth fAuth = FirebaseAuth.getInstance();
    CollectionAdapter collectionAdapter;
    static String userIds;

//    public static String data = "";
    public static ArrayList<Antrian> antrianList(Context context) {
        ArrayList<Antrian> listItems = new ArrayList<>();
        userIds = fAuth.getCurrentUser().getUid();

//        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
//        UserReference userReference = mDatabase.collection("Entry");
//        UserAdapter collectionAdapter;
//        String userId;

        userReference.orderBy("nomor_antrian", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException
                    e) {
                if (e != null) {
                    return;
                }

                int n = 0;
//                String data = "";
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Antrian LUser = documentSnapshot.toObject(Antrian.class);
                    //              User.setDocumentId(documentSnapshot.getId());/
                    String id = LUser.getKode_antrian();
                    String nama = LUser.getNama_user();
                    String email = LUser.getStatus();
                    String phone = LUser.getTanggal_antrian();

                    n++;
                    listItems.add(new Antrian(id,nama,email,phone));
//                    data += String.valueOf(n) + "." + nama  + " ";
                }
//                Toast.makeText(context, data+userIds, Toast.LENGTH_SHORT).show();

            }
        });
        return listItems;
    }
}
