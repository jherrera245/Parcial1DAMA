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

    Button btnTomarFoto;
    Button btnVerGaleria;
    Button btnCompartir;
    ImageView imgFoto;
    String rutaImagenes;

    private static final int REQUEST_CODIGO_CAMERA = 200;
    private static final int REQUEST_CODIGO_CAPTURAR_IMAGEN = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnVerGaleria = findViewById(R.id.btnVerGaleria);
        btnCompartir = findViewById(R.id.btnCompartir);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        imgFoto = findViewById(R.id.imgInfo);

        btnTomarFoto.setOnClickListener(view -> realizarProcesoFotografia());

        if(imgFoto.getDrawable() == null){
            btnCompartir.setVisibility(View.INVISIBLE);
        }


        btnVerGaleria.setOnClickListener(view -> {
            Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentGaleria, 1);
        });

        btnCompartir.setOnClickListener(view -> {
            Intent intentShare = new Intent(Intent.ACTION_SEND);
            intentShare.setType("image/*");
            intentShare.setPackage("com.whatsapp");

            if(rutaImagenes != null){
                intentShare.putExtra(Intent.EXTRA_STREAM, Uri.parse(rutaImagenes));
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

    private void realizarProcesoFotografia() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                tomarFoto();
            }else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.CAMERA}, REQUEST_CODIGO_CAMERA);
            }
        }else{
            tomarFoto();
        }
    }

    public void onRequestPermissionResult(int requestCodigo, @NonNull String[] permissions, @NonNull int[] grantResult ){
        if(requestCodigo == REQUEST_CODIGO_CAMERA){
            if(permissions.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED){
                tomarFoto();
            }else{
                Toast.makeText(MainActivity.this,"Se requieren permisos para usar la camara", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCodigo, permissions, grantResult);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if(requestCode == REQUEST_CODIGO_CAPTURAR_IMAGEN){
            if(resultCode == Activity.RESULT_OK){
                imgFoto.setImageURI(Uri.parse(rutaImagenes));
                btnCompartir.setVisibility(View.VISIBLE);
            }

        }

        if(requestCode ==1 && resultCode == RESULT_OK && data != null){
            Uri uriImagen = data.getData();
            imgFoto.setImageURI( uriImagen );
            Bitmap imgBitmap = BitmapFactory.decodeFile(rutaImagenes);
        }

        super.onActivityResult(requestCode,resultCode,data);
    }

    private void tomarFoto() {
        Intent intentCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intentCamara.resolveActivity(getPackageManager()) != null){
            File archivoFoto = null;
            archivoFoto = crearArchivo();
            if(archivoFoto != null){
                Uri rutaFoto = FileProvider.getUriForFile(MainActivity.this, "com.example.usodecamara_1", archivoFoto);
                intentCamara.putExtra(MediaStore.EXTRA_OUTPUT, rutaFoto);
                startActivityForResult(intentCamara, REQUEST_CODIGO_CAPTURAR_IMAGEN);
            }
        }

    }

    private File crearArchivo() {
        String nomenclatura = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String prefijoArchivo = "APPCAM_" + nomenclatura + "_";
        File directorioImagen = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File miImagen = null;
        try {
            miImagen = File.createTempFile(prefijoArchivo,".jpg",directorioImagen);
            rutaImagenes =miImagen.getAbsolutePath();

        }catch(IOException e){
            e.printStackTrace();
        }

        return miImagen;
    }

}