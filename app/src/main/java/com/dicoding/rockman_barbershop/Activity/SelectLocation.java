package com.dicoding.rockman_barbershop.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.dicoding.rockman_barbershop.MainActivity;
import com.dicoding.rockman_barbershop.R;
import com.dicoding.rockman_barbershop.adapter.LocationAdapter;
import com.dicoding.rockman_barbershop.fragment.HomeFragment;
import com.dicoding.rockman_barbershop.model.Location;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class SelectLocation extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog loading;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = mDatabase.collection("Location");
    private LocationAdapter locationAdapter;
    private String userId;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        recyclerView = findViewById(R.id.rv_collection);
        setUpRecyclerView();

        ImageButton btnBack = (ImageButton) findViewById(R.id.button_back_select_location);
        btnBack.setOnClickListener(this);
    }

    private void setUpRecyclerView() {
        loading = ProgressDialog.show(SelectLocation.this,
                null,
                "Mohon Tunggu",
                true,
                false);

        Query query = collectionReference.orderBy("number", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Location> options = new FirestoreRecyclerOptions.Builder<Location>()
                .setQuery(query, Location.class)
                .build();

        locationAdapter = new LocationAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SelectLocation.this));
        recyclerView.setAdapter(locationAdapter);
        loading.dismiss();

        locationAdapter.setOnItemClickListener(new LocationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Location location = documentSnapshot.toObject(Location.class);
                String idL = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();

//                Toast.makeText(SelectLocation.this, id, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SelectLocation.this, HomeFragment.class);
                intent.putExtra(HomeFragment.ExtraId, idL);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        locationAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        locationAdapter.stopListening();
    }
}
