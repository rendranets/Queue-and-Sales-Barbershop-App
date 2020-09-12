package com.dicoding.rockman_barbershop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dicoding.rockman_barbershop.R;
import com.dicoding.rockman_barbershop.model.RiwayatPesanan;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DetailRiwayatTransaksiAdapter extends FirestoreRecyclerAdapter<RiwayatPesanan, DetailRiwayatTransaksiAdapter.MyViewHolder> {
    private DetailRiwayatTransaksiAdapter.OnItemClickListener listener;
    int totals = 0;

    public DetailRiwayatTransaksiAdapter(@NonNull FirestoreRecyclerOptions<RiwayatPesanan> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull DetailRiwayatTransaksiAdapter.MyViewHolder holder, int position, @NonNull RiwayatPesanan model) {


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
    public DetailRiwayatTransaksiAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View iteView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail_riwayat_keranjang, parent, false);

        return new DetailRiwayatTransaksiAdapter.MyViewHolder(iteView);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        public RiwayatPesanan produkArray;
        LinearLayout linearLayout_keranjang;
        TextView tv_produk, tv_total, tv_kuantitas;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout_keranjang = itemView.findViewById(R.id.ll_detail_riwayat_keranjang);
            tv_produk = itemView.findViewById(R.id.nama_detail_item_riwayat_keranjang);
            tv_total = itemView.findViewById(R.id.harga_detail_item_riwayat_keranjang);
            tv_kuantitas = itemView.findViewById(R.id.total_detail_item_riwayat_keranjang);

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

    public void setOnItemClickListener(DetailRiwayatTransaksiAdapter.OnItemClickListener listener) {
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
