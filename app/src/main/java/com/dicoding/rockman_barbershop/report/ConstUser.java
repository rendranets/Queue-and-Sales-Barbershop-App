package com.dicoding.rockman_barbershop.report;


import android.content.Context;

import com.dicoding.rockman_barbershop.adapter.CollectionAdapter;
import com.dicoding.rockman_barbershop.model.User;
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

public class ConstUser {
    static FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    static CollectionReference userReference = mDatabase.collection("users");
    static FirebaseAuth fAuth = FirebaseAuth.getInstance();
    CollectionAdapter collectionAdapter;
    static String userIds;

//    public static String data = "";
    public static ArrayList<User> userList(Context context) {
        ArrayList<User> listItems = new ArrayList<>();
        userIds = fAuth.getCurrentUser().getUid();

//        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
//        UserReference userReference = mDatabase.collection("Entry");
//        UserAdapter collectionAdapter;
//        String userId;

        userReference.orderBy("nama_profile", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException
                    e) {
                if (e != null) {
                    return;
                }

                int n = 0;
//                String data = "";
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    User LUser = documentSnapshot.toObject(User.class);
                    //              User.setDocumentId(documentSnapshot.getId());/
                    String id = LUser.getUser_id();
                    String nama = LUser.getNama_profile();
                    String email = LUser.getEmail_profile();
                    String phone = LUser.getPhone_profile();
                    Timestamp waktu = LUser.getWaktu_Daftar();

                    n++;
                    listItems.add(new User(id,nama,email,phone,waktu));
//                    data += String.valueOf(n) + "." + nama  + " ";
                }
//                Toast.makeText(context, data+userIds, Toast.LENGTH_SHORT).show();

            }
        });
        return listItems;
    }
}
