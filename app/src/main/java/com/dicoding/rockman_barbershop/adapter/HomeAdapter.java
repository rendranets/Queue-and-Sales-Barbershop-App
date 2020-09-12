package com.dicoding.rockman_barbershop.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dicoding.rockman_barbershop.R;
import com.dicoding.rockman_barbershop.model.Antrian;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HomeAdapter extends FirestoreRecyclerAdapter<Antrian, HomeAdapter.MyViewHolder> {


    private OnItemClickListener listener;

    public HomeAdapter(@NonNull FirestoreRecyclerOptions<Antrian> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Antrian model) {

        holder.tv_nama_antrian.setText(model.getKode_antrian());
        String cek = model.getStatus();
        if (cek.equals("selesai")){
            holder.tv_nomor_antrian.setTextColor(Color.parseColor("#DCC139"));
            holder.tv_nomor_antrian.setText(model.getStatus());
        }

//        judulDetail = model.getJudul_entry();
//        kategoriDetail = model.getkategori_entry();
//        provinsiDetail = model.getProvinsi_entry());
//        daerahDetail = model.getDaerah_entri();
//        keteranganDetail = model.getKeterangan_entry();
//        fotoUrlDetail = model.getFoto_url_entry();
//        UploadDate = model.getTanggal_entry();
//        uploadByDetail = model.getUpload_by();
//        namaUploadBy = model.get();


    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout linearLayout;
        TextView tv_nama_antrian, tv_nomor_antrian;

        MyViewHolder(View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.ll_layout_utama);
            tv_nama_antrian = itemView.findViewById(R.id.tv_title_home);
            tv_nomor_antrian = itemView.findViewById(R.id.tv_content_home);

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

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View iteView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home, parent, false);

        return new MyViewHolder(iteView);
    }


}
