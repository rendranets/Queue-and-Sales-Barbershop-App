package com.dicoding.rockman_barbershop.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dicoding.rockman_barbershop.R;
import com.dicoding.rockman_barbershop.model.Produk;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class EditProdukAdapter extends FirestoreRecyclerAdapter<Produk, EditProdukAdapter.MyViewHolder> {
    private OnItemClickListener listener;

    public EditProdukAdapter(@NonNull FirestoreRecyclerOptions<Produk> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Produk model) {
        holder.tv_nama_produk_edit.setText(model.getNama_barang() + "(" + model.getKode_barang() + ")");

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View iteView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_produk_edit, parent, false);

        return new MyViewHolder(iteView);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout_category_edit;
        TextView tv_nama_produk_edit;

        MyViewHolder(View itemView) {
            super(itemView);
            linearLayout_category_edit = itemView.findViewById(R.id.ll_layout_produk_edit);
            tv_nama_produk_edit = itemView.findViewById(R.id.tv_nama_produk);

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

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    @Override
    public void startListening() {
        super.startListening();

    }
}
