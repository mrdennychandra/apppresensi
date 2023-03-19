package com.example.appabsensi1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.ProgressBar;

import com.example.appabsensi1.adapter.PresensiAdapter;
import com.example.appabsensi1.http.ApiClient;
import com.example.appabsensi1.http.ApiInterface;
import com.example.appabsensi1.model.Presensi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView list;
    private PresensiAdapter adapter;
    private SwipeRefreshLayout swipe_refresh;
    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipe_refresh = findViewById(R.id.swipe_refresh);
        //permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
        //Untuk enable mengambil URI dari galery
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,InputPresensiActivity.class);
                startActivity(intent);
            }
        });

        list =findViewById(R.id.list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData();
                    }
                }, 2000);
            }
        });
        getData();
    }

    private void getData(){
        swipe_refresh.setRefreshing(true);
        api = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Presensi>> call = api.getData("1");
        call.enqueue(new Callback<List<Presensi>>() {
            @Override
            public void onResponse(Call<List<Presensi>> call, Response<List<Presensi>> response) {
                //String result = response.body();
                //Jika status dari server 200,300
                swipe_refresh.setRefreshing(false);
                if(response.isSuccessful()){
                    List<Presensi> result = response.body();
                    adapter = new PresensiAdapter(MainActivity.this,result);
                    list.setAdapter(adapter);
                }else{
                    //status dari server 400,500
                    Snackbar.make(findViewById(android.R.id.content),"tidak dapat mengirim data",Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Presensi>> call, Throwable t) {
                swipe_refresh.setRefreshing(false);
                Snackbar.make(findViewById(android.R.id.content),"tidak dapat terhubung ke server",Snackbar.LENGTH_SHORT).show();
                //menampilkan detail eror di logcat
                t.printStackTrace();
            }
        });

    }
}