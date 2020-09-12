package com.dicoding.rockman_barbershop.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.dicoding.rockman_barbershop.Activity.DetailBarangActivity;
import com.dicoding.rockman_barbershop.R;
import com.dicoding.rockman_barbershop.adapter.CollectionAdapter;
import com.dicoding.rockman_barbershop.model.Produk;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.itextpdf.text.pdf.PdfWriter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class CollectionFragment extends Fragment {

    ProgressDialog loading;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = mDatabase.collection("Produk");
    private CollectionReference profileReference = mDatabase.collection("users");
    private CollectionAdapter collectionAdapter;
    private String userId;
    private RecyclerView recyclerView;
    private ImageButton download, keranjang;

    private String finish = "Finished", finishing = "Finishing up...", check, stringCount,  stringLencana, lencana;
    private Boolean succes = false;
    private String formattedDate;

    PdfWriter writer;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@NonNull ViewGroup container,@NonNull Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_collection, container, false);

        userId = fAuth.getCurrentUser().getUid();

        recyclerView = v.findViewById(R.id.rv_collection);



        loading = ProgressDialog.show(getActivity(),
                null,
                "Mohon Tunggu",
                true,
                false);

        setUpRecyclerView();

        collectionAdapter.startListening();

        swipeRefreshLayout = v.findViewById(R.id.swipe_refresher_collection);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                collectionAdapter.startListening();
                collectionAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return v;
    }

    private void setUpRecyclerView() {

        Query query = collectionReference.orderBy("tanggal_barang", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Produk> options = new FirestoreRecyclerOptions.Builder<Produk>()
                .setQuery(query, Produk.class)
                .build();

        collectionAdapter = new CollectionAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
//        recyclerView.padding
        recyclerView.setAdapter(collectionAdapter);
        loading.dismiss();

        collectionAdapter.setOnItemClickListener(new CollectionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Produk produk = documentSnapshot.toObject(Produk.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();

                //Toast.makeText(getActivity(), id, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DetailBarangActivity.class);
                intent.putExtra(DetailBarangActivity.ExtraId, id);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        collectionAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        collectionAdapter.stopListening();
    }
}
