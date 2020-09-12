package com.dicoding.rockman_barbershop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dicoding.rockman_barbershop.R;
import com.dicoding.rockman_barbershop.model.Location;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LocationAdapter extends FirestoreRecyclerAdapter<Location, LocationAdapter.MyViewHolder> {

    private String lokasi;

    private OnItemClickListener listener;

    public LocationAdapter(@NonNull FirestoreRecyclerOptions<Location> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Location model) {

        holder.tv_location.setText(model.getLokasi());

    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout linearLayout;
        TextView tv_location;

        MyViewHolder(View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.ll_layout_location);
            tv_location = itemView.findViewById(R.id.tv_title_location);

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
                .inflate(R.layout.list_location, parent, false);

        return new MyViewHolder(iteView);
    }


}
