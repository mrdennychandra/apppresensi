package com.example.appabsensi1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appabsensi1.http.ApiClient;
import com.example.appabsensi1.http.ApiInterface;
import com.example.appabsensi1.model.Presensi;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InputPresensiActivity extends AppCompatActivity {

    private Spinner sp_keterangan;
    private EditText txt_catatan;
    private Button btn_kirim;

    private static final int CAMERA_REQUEST_CODE = 100;
    private Uri file;
    private String imagePath;
    private ImageView img;

    private ProgressBar progressBar;

    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_presensi);
        sp_keterangan = findViewById(R.id.sp_keterangan);
        txt_catatan = findViewById(R.id.txt_catatan);
        btn_kirim = findViewById(R.id.btn_kirim);
        img = findViewById(R.id.img);
        progressBar = findViewById(R.id.progressBar);
        String[] kets = new String[]{"Hadir","Izin","Sakit","Dinas Luar"};
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,kets);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_keterangan.setAdapter(adapter);
        btn_kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String catatan = txt_catatan.getText().toString();
                String keterangan = sp_keterangan.getSelectedItem().toString();
                Presensi presensi = new Presensi();
                presensi.catatan = catatan;
                presensi.keterangan = keterangan;
                presensi.iduser = "1";
                presensi.path_foto = imagePath;
                send(presensi);
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
    }

    ////////////////////////////////////////// camera/gallery //////////////////////////////////////////
    //foto

    //camera
    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        //Toast.makeText(this,file.toString(),Toast.LENGTH_SHORT).show();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        startActivityForResult(intent, 100);
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Glide.with(this).load(file).into(img);
                imagePath = file.getPath();
            }
        }
        //Toast.makeText(this, imagePath, Toast.LENGTH_SHORT).show();
    }
    ////////////////////////////////////////// camera/gallery //////////////////////////////////////////
    private void send(final Presensi presensi){
        progressBar.setVisibility(View.VISIBLE);
        File file = new File(presensi.path_foto);//path image
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("berkas", file.getName(),
                reqFile);
        RequestBody keterangan = RequestBody.create(MediaType.parse("text/plain"), presensi.keterangan);
        RequestBody userid = RequestBody.create(MediaType.parse("text/plain"), presensi.iduser);
        RequestBody catatan = RequestBody.create(MediaType.parse("text/plain"), presensi.catatan);
        api = ApiClient.getClient().create(ApiInterface.class);
        Call<String> call = api.upload(body,keterangan,catatan,userid);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(progressBar.isShown()){
                    progressBar.setVisibility(View.INVISIBLE);
                }
                //mendapatkan message dari server
                //String result = response.body();
                //Jika status dari server 200,300
                if(response.isSuccessful()){
                    Snackbar.make(findViewById(android.R.id.content),"Data terkirim",Snackbar.LENGTH_SHORT).show();
                    Intent intent = new Intent(InputPresensiActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                }else{
                    //status dari server 400,500
                    Snackbar.make(findViewById(android.R.id.content),"tidak dapat mengirim data",Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Snackbar.make(findViewById(android.R.id.content),"tidak dapat terhubung ke server",Snackbar.LENGTH_SHORT).show();
                if(progressBar.isShown()){
                    progressBar.setVisibility(View.INVISIBLE);
                }
                //menampilkan detail eror di logcat
                t.printStackTrace();
            }
        });

    }
}