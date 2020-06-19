package com.example.clase10demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

public class MainActivityStorage extends AppCompatActivity {
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_storage);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

    }

    public boolean validarPermisos(int requestCode, String permission_code) {

        int permission = ContextCompat.checkSelfPermission(this, permission_code);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{permission_code},
                    requestCode);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 1) {
                subirImagenStream(null);
            }
            if (requestCode == 2) {
                subirArchivoComoFile(null);
            }
            if (requestCode == 3) {
                descargarArchivo(null);
            }
        }
    }

    public void subirArchivoComoFile(View view) {
        if (validarPermisos(2, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            String fileName = "Clase 02.1 - Introducción a las aplicaciónes móviles.pdf";

            File archivo = new File(fileDir, fileName);

            Uri fileUri = Uri.fromFile(archivo);

            StorageReference imagenesRef =
                    storageReference.child("imagenes/" + fileName);

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setCustomMetadata("Autor", "Stuardo Lucho")
                    .setCustomMetadata("Curso", "TEL306")
                    .build();


            imagenesRef.putFile(fileUri, metadata)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("infoApp", "subida exitosa!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("infoApp", "error On Failure", e.getCause());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            long bytesTransferred = taskSnapshot.getBytesTransferred();
                            long totalByteCount = taskSnapshot.getTotalByteCount();
                            double progreso = (100.0 * bytesTransferred) / totalByteCount;
                            Log.d("infoAppPro", "porcentaje de subida: " + progreso);
                        }
                    });


        }
    }

    public void subirImagenStream(View view) {

        if (validarPermisos(1, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File archivo = new File(fileDir, "pucp.jpg");

            try {
                InputStream inputStream = new FileInputStream(archivo);

                StorageReference imagenesRef = storageReference.child("imagenes/pucpcito.jpg");

                imagenesRef.putStream(inputStream)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d("infoApp", "subida exitosa!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("infoApp", "error On Failure", e.getCause());
                            }
                        });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    public void descargarArchivo(View view) {
        if (validarPermisos(3, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            String fileName = "Clase 02.1 - Introducción a las aplicaciónes móviles.pdf";

            StorageReference pdfReference =
                    storageReference.child("imagenes/" + fileName);

            File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            File file = new File(fileDir,"clase.pdf");

            pdfReference.getFile(file)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d("infoApp", "descarga exitosa!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("infoApp", "error", e.getCause());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                            long bytesTransferred = taskSnapshot.getBytesTransferred();
                            long totalByteCount = taskSnapshot.getTotalByteCount();
                            double progreso = (100.0 * bytesTransferred) / totalByteCount;
                            Log.d("infoAppPro", "porcentaje de bajada: " + Math.round(progreso));
                        }
                    });

        }

    }

    public void descargarYmostrarImagen(View view){

        StorageReference imgRef = storageReference.child("imagenes/pucpcito.jpg");

        ImageView imageView = findViewById(R.id.imageView);

        Glide.with(this).load(imgRef).into(imageView);
    }
}