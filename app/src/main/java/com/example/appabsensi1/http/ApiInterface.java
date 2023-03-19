package com.example.appabsensi1.http;

import com.example.appabsensi1.model.Presensi;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {

    @Multipart
    @POST("absen/masuk")
    Call<String> upload(@Part MultipartBody.Part part,
                        @Part("keterangan") RequestBody keterangan,
                        @Part("catatan") RequestBody catatan,
                        @Part("iduser") RequestBody iduser
    );

    @GET("absen")
    Call<List<Presensi>> getData(@Query("iduser") String iduser);

}
