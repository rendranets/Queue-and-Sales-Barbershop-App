package com.dicoding.rockman_barbershop.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dicoding.rockman_barbershop.R;
import com.dicoding.rockman_barbershop.model.Produk;
import com.dicoding.rockman_barbershop.model.RiwayatTransaksi;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RiwayatTransaksiAdapter extends FirestoreRecyclerAdapter<RiwayatTransaksi, RiwayatTransaksiAdapter.MyViewHolder> {

    private OnItemClickListener listener;

    public RiwayatTransaksiAdapter(@NonNull FirestoreRecyclerOptions<RiwayatTransaksi> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull RiwayatTransaksi model) {
        holder.id_riwayat.setText(model.getKode_transaksi());
        holder.status_riwayat.setText(model.getStatus());

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View iteView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_riwayat, parent, false);

        return new MyViewHolder(iteView);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout_riwayat;
        TextView id_riwayat,status_riwayat;

        MyViewHolder(View itemView) {
            super(itemView);
            linearLayout_riwayat = itemView.findViewById(R.id.ll_layout_riwayat);
            id_riwayat = itemView.findViewById(R.id.tv_id_riwayat);
            status_riwayat = itemView.findViewById(R.id.tv_status_riwayat);

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

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


}
