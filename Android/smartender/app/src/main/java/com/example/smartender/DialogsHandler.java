package com.example.smartender;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


public class DialogsHandler {

    public static void createSimpleDialog(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        /*builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });*/

        builder.show();
    }
}
