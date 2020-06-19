package com.example.clase10demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if(currentUser != null){
            Log.d("infoApp","Usuario logueado");
            String email = currentUser.getEmail();
            String uid = currentUser.getUid();
            String displayName = currentUser.getDisplayName();
            boolean emailVerified = currentUser.isEmailVerified();

            Log.d("infoApp","email: " + email);
            Log.d("infoApp","uid: " + uid);
            Log.d("infoApp","displayName: " + displayName);
            Log.d("infoApp","emailVerified: " + emailVerified);

        }else{
            Log.d("infoApp","Usuario no logueado");
        }

    }

    public void iniciarSesion(View view) {

        List<AuthUI.IdpConfig> listaProveedores = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        AuthMethodPickerLayout customLayout =
                new AuthMethodPickerLayout.Builder(R.layout.mipaginalogin)
                .setEmailButtonId(R.id.btnEmail)
                .setGoogleButtonId(R.id.btnGoogle)
                .build();

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAuthMethodPickerLayout(customLayout)
                        .setLogo(R.drawable.pucp)
                        .setAvailableProviders(listaProveedores)
                        .build(),
                1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                Log.d("infoApp","inicio de sesión exitoso!");
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.setLanguageCode("es-419");
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if(!currentUser.isEmailVerified()){
                    currentUser.sendEmailVerification()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MainActivity.this,
                                            "Se le ha enviado un correo para validar su cuenta",
                                            Toast.LENGTH_SHORT).show();
                                    Log.d("infoApp","Envío de correo exitoso");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("infoApp","error al enviar el correo");
                                }
                            });
                }
            }else{
                Log.d("infoApp","Inicio erroneo");
            }
        }
    }
}