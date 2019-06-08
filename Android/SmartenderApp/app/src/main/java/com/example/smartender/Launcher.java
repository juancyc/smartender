package com.example.smartender;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Launcher extends AppCompatActivity {

    private int DURACION_SPLASH = 6000; // 3 segundos
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Tenemos una plantilla llamada splash.xml donde mostraremos la información que queramos (logotipo, etc.)
        setContentView(R.layout.activity_launcher);

        if (ActivityCompat.checkSelfPermission(Launcher.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(Launcher.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                createPermisionDialog();
            }else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
            }
        }else{
            DURACION_SPLASH = 3000;
        }

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación

                Intent intent = new Intent(Launcher.this, MainActivity.class);
                startActivity(intent);
                finish();
            };
        }, DURACION_SPLASH);


    }

    private void createPermisionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Launcher.this);
        builder.setTitle("Permisos desactivados");
        builder.setMessage("Debe aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
                Intent intent = new Intent(Launcher.this, MainActivity.class);
                startActivity(intent);
                finish();
             }
        });

        builder.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*if(requestCode == 100){
            if(grantResults.length == 2 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED){

            }
        }*/
    }
}

