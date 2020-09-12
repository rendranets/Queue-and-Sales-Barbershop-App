package com.dicoding.rockman_barbershop.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dicoding.rockman_barbershop.R;
import com.dicoding.rockman_barbershop.adapter.EditProdukAdapter;
import com.dicoding.rockman_barbershop.model.Produk;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class EditCategoryActivity extends AppCompatActivity {

    ProgressDialog loading;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = mDatabase.collection("Produk");
    private EditProdukAdapter editProdukAdapter;
    private String userId;
    private RecyclerView recyclerView;
    private ImageButton add;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        userId = fAuth.getCurrentUser().getUid();

        recyclerView = findViewById(R.id.rv_category);
        add = findViewById(R.id.add_Kategory_edit);

        setUpRecyclerView();

        add.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), TambahProduk.class);
            startActivity(intent);
        });

        swipeRefreshLayout = findViewById(R.id.swipe_refresher_produk);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                editProdukAdapter.startListening();
                editProdukAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(EditCategoryActivity.this);
                builder.setMessage("Yakin ingin menghapus produk?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                editProdukAdapter.deleteItem(viewHolder.getAdapterPosition());

                                Toast.makeText(EditCategoryActivity.this, "Produk dihapus", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(EditCategoryActivity.this, EditCategoryActivity.class);
                                startActivity(intent);
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }).attachToRecyclerView(recyclerView);


    }

    private void setUpRecyclerView() {
        loading = ProgressDialog.show(EditCategoryActivity.this,
                null,
                "Mohon Tunggu",
                true,
                false);

        Query querys = collectionReference.orderBy("kode_barang", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Produk> options = new FirestoreRecyclerOptions.Builder<Produk>()
                .setQuery(querys, Produk.class)
                .build();


        editProdukAdapter = new EditProdukAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(EditCategoryActivity.this));
        recyclerView.setAdapter(editProdukAdapter);
        loading.dismiss();

        editProdukAdapter.setOnItemClickListener(new EditProdukAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Produk categoryEdit = documentSnapshot.toObject(Produk.class);
                String id = documentSnapshot.getId();
                String namaBarang = documentSnapshot.getString("nama_barang");
                String kodeBarang = documentSnapshot.getString("kode_barang");
                int stockBarang = documentSnapshot.getLong("stock_barang").intValue();
                int hargaBarang = documentSnapshot.getLong("harga_barang").intValue();
                String keteranganBarang = documentSnapshot.getString("keterangan_barang");
                String url = documentSnapshot.getString("foto_url_barang");

                Intent intent = new Intent(getApplicationContext(), EditProdukDipilih.class);
                intent.putExtra(EditProdukDipilih.ExtraId, id);
                intent.putExtra(EditProdukDipilih.ExtraNama, namaBarang);
                intent.putExtra(EditProdukDipilih.ExtraKode, kodeBarang);
                intent.putExtra("ExtraStock", stockBarang);
                intent.putExtra("ExtraHarga", hargaBarang);
                intent.putExtra(EditProdukDipilih.ExtraKeterangan, keteranganBarang);
//                intent.putExtra(EditProdukDipilih.ExtraTanggal, tanggalBarang);
                intent.putExtra(EditProdukDipilih.ExtraUrl, url);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        editProdukAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        editProdukAdapter.stopListening();
    }

}
