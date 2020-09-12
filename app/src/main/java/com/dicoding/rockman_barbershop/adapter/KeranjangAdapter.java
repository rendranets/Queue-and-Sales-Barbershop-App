package com.dicoding.rockman_barbershop.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.dicoding.rockman_barbershop.Activity.Keranjang;
import com.dicoding.rockman_barbershop.Interface.ItemClickListener;
import com.dicoding.rockman_barbershop.R;
import com.dicoding.rockman_barbershop.model.Pesanan;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class KeranjangAdapter extends FirestoreRecyclerAdapter<Pesanan, KeranjangAdapter.MyViewHolder> {
    private KeranjangAdapter.OnItemClickListener listener;
    int totals = 0;

    public KeranjangAdapter(@NonNull FirestoreRecyclerOptions<Pesanan> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull KeranjangAdapter.MyViewHolder holder, int position, @NonNull Pesanan model) {


        String total = Integer.toString(model.getHarga_barang());
        String kuantitas = Integer.toString(model.getKuantitas());

        holder.tv_produk.setText(model.getNama_barang());
        holder.tv_total.setText(total);
        holder.tv_kuantitas.setText(kuantitas);
//
//        totals = totals + model.getTotal_harga();
//        String tot = Integer.toString(totals);
//        elistener.onEvent(tot);

//        Glide.with(context)
//                .load(model.getFoto_url_entry())
//                .placeholder(R.drawable.pdbi_opening)
//                .into(holder.iv_produk);


    }

    @NonNull
    @Override
    public KeranjangAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View iteView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_keranjang, parent, false);

        return new KeranjangAdapter.MyViewHolder(iteView);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public Pesanan produkArray;
        LinearLayout linearLayout_keranjang;
        TextView tv_produk, tv_total, tv_kuantitas;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout_keranjang = itemView.findViewById(R.id.ll_keranjang);
            tv_produk = itemView.findViewById(R.id.nama_item_keranjang);
            tv_total = itemView.findViewById(R.id.harga_item_keranjang);
            tv_kuantitas = itemView.findViewById(R.id.total_item_keranjang);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(KeranjangAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }


    @Override
    public void startListening() {
        super.startListening();

    }

//    public interface EventListener {
//        void onEvent(String where);
//    }
}
