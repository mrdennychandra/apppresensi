package com.example.appabsensi1.adapter;

import static com.example.appabsensi1.http.ApiClient.BASE_IMAGE_URL;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;

import com.example.appabsensi1.InputPresensiActivity;
import com.example.appabsensi1.R;
import com.example.appabsensi1.model.Presensi;

//controller antara data dan row_lapor
//1. Mengetahui row_lapor (layoutnya yg mana)
//2. Mengetahui id setiap komponen dan mengisinya
//3. Mengetahui jumlah data
public class PresensiAdapter extends
        RecyclerView.Adapter<PresensiAdapter.ViewHolder>{

    //digunakan di activity yang mana
    Context context;
    //array dari lapor
    List<Presensi> lapors;
    //mengisikan data
    public PresensiAdapter(Context context, List<Presensi> lapors){
        this.context = context;
        this.lapors = lapors;
    }

    //1.mengetahui layoutnya
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_lapor, parent, false);
        return new ViewHolder(view);
    }

    //2. mengisi id nya
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Presensi lapor = lapors.get(position);
        holder.txt_keterangan.setText(lapor.keterangan);
        if(lapor.masuk!= null) {
            holder.txt_waktu_masuk.setText("Masuk = " + lapor.masuk);
        }
        if(lapor.keluar!= null) {
            holder.txt_waktu_keluar.setText("Keluar = " + lapor.keluar);
        }
        if(lapor.path_foto != null){
            Glide.with(context).load(BASE_IMAGE_URL + lapor.path_foto)
                    .into(holder.img_lapor);
        }
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    //mengetahui jumlah datanya
    @Override
    public int getItemCount() {
        return lapors != null ? lapors.size() : 0;
    }

    //mengambil setiap id dr layout
    class ViewHolder extends RecyclerView.ViewHolder{
        //deklarasi variabel
        TextView txt_keterangan,txt_waktu_masuk,txt_waktu_keluar;
        ImageView img_lapor;
        CardView card;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_keterangan = itemView.findViewById(R.id.txt_keterangan);
            txt_waktu_masuk = itemView.findViewById(R.id.txt_waktu_masuk);
            txt_waktu_keluar = itemView.findViewById(R.id.txt_waktu_keluar);
            img_lapor = itemView.findViewById(R.id.img_lapor);
            card = itemView.findViewById(R.id.card);
        }
    }
}
