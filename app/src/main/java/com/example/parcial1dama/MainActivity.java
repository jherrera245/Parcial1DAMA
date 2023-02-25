package com.example.parcial1dama;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button btnVerGaleria;
    Button btnCompartir;
    ImageView imgFoto;

    Uri uriImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnVerGaleria = findViewById(R.id.btnVerGaleria);
        btnCompartir = findViewById(R.id.btnCompartir);
        imgFoto = findViewById(R.id.imgInfo);

        btnVerGaleria.setOnClickListener(view -> {
            Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentGaleria, 1);
        });

        btnCompartir.setOnClickListener(view -> {
            Intent intentShare = new Intent(Intent.ACTION_SEND);
            intentShare.setType("image/*");
            intentShare.setPackage("com.whatsapp");

            if(uriImagen != null){
                intentShare.putExtra(Intent.EXTRA_STREAM, uriImagen);
                try{
                    startActivity(intentShare);
                }catch (Exception e){
                    Toast.makeText(MainActivity.this,"Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(MainActivity.this,"No hay imagen, tome una foto primero",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){

        if(requestCode ==1 && resultCode == RESULT_OK && data != null){
            uriImagen = data.getData();
            imgFoto.setImageURI( uriImagen );
        }

        super.onActivityResult(requestCode,resultCode,data);
    }

}