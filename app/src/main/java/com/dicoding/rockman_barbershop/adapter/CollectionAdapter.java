package com.dicoding.rockman_barbershop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dicoding.rockman_barbershop.R;
import com.dicoding.rockman_barbershop.model.Produk;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



public class CollectionAdapter extends FirestoreRecyclerAdapter<Produk, CollectionAdapter.MyViewHolder> {
    private OnItemClickListener listener;
//    private Context context;


    public CollectionAdapter(@NonNull FirestoreRecyclerOptions<Produk> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Produk model) {

        holder.tv_produk.setText(model.getNama_barang());
//        Glide.with(context)
//                .load(model.getFoto_url_entry())
//                .placeholder(R.drawable.pdbi_opening)
//                .into(holder.iv_produk);
        Picasso.get()
                .load(model.getFoto_url_barang())
                .into(holder.iv_produk);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View iteView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collection, parent, false);

        return new CollectionAdapter.MyViewHolder(iteView);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        public Produk produkArray;
        LinearLayout linearLayout_collection;
        TextView tv_produk;
        ImageView iv_produk;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout_collection = itemView.findViewById(R.id.ll_produk);
            tv_produk = itemView.findViewById(R.id.nama_produk);
            iv_produk = itemView.findViewById(R.id.gambar_produk);

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
