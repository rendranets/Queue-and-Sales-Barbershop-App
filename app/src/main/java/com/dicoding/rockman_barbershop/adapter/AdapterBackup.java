//package com.dicoding.pdbi.adapter;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.dicoding.pdbi.R;
//import com.dicoding.pdbi.model.Entry;
//
//import java.util.List;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//public class AdapterBackup extends RecyclerView.Adapter<AdapterBackup.MyViewHolder> {
//
//    private List<Entry> listRequest;
//    private Activity activity;
//
//    public class MyViewHolder extends RecyclerView.ViewHolder{
//        public LinearLayout linearLayout;
//        public TextView tv_judul, tv_deskripsi, tv_kategori, tv_provinsi;
//
//        public MyViewHolder(View itemView) {
//            super(itemView);
//            linearLayout = itemView.findViewById(R.id.ll_layout_utama);
//            tv_judul = itemView.findViewById(R.id.tv_title_home);
//            tv_kategori = itemView.findViewById(R.id.tv_category_home);
//            tv_provinsi = itemView.findViewById(R.id.tv_location_home);
//            tv_deskripsi = itemView.findViewById(R.id.tv_content_home);
//
//        }
//    }
//
//    public AdapterBackup(List<Entry> listRequest, Activity activity) {
//        this.activity = activity;
//        this.listRequest = listRequest;
//    }
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View iteView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_home, parent, false);
//
//        return new MyViewHolder(iteView);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        final Entry entry = listRequest.get(position);
//
//        holder.tv_judul.setText(entry.getJudul_entry());
//        holder.tv_deskripsi.setText(entry.getKeterangan_entry());
//        holder.tv_kategori.setText(entry.getkategori_entry());
//        holder.tv_provinsi.setText(entry.getProvinsi_entry());
//
//        holder.linearLayout.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                Intent goDetail = new Intent(activity, DetailDataActivity.class);
//                goDetail.putExtra("id", entry.getKey());
//                goDetail.putExtra("judul", entry.getJudul_entry());
//                goDetail.putExtra("daerah", entry.getDaerah_entri());
//                goDetail.putExtra("keterangan", entry.getKeterangan_entry());
//                goDetail.putExtra("kategori", entry.getkategori_entry());
//                goDetail.putExtra("berkas", entry.getFoto_url_entry());
//                goDetail.putExtra("upload_by", entry.getUpload_by());
//
//                activity.startActivity(goDetail);
//            }
//        });
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return listRequest.size();
//    }
//}
