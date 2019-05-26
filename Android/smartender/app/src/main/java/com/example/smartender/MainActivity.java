package com.example.smartender;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View;
import android.widget.ListView;

import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity {

    private Button btnAgregar, btnModificar, btnEliminar;
    private ListView listCalendarInfo;
    private String items[] = new String[] {"Juan","Cami","Ger","Carlos","Pepeno"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_info);

        btnAgregar = (Button)findViewById(R.id.btnAgregar);
        btnModificar = (Button)findViewById(R.id.btnModificar);
        btnEliminar = (Button)findViewById(R.id.btnEliminar);
        listCalendarInfo = (ListView)findViewById(R.id.listCalendarInfo);
        ArrayAdapter<String> adpter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,items);
        listCalendarInfo.setAdapter(adpter);

        btnAgregar.setOnClickListener(buttonsListeners);
        btnModificar.setOnClickListener(buttonsListeners);
        btnEliminar.setOnClickListener(buttonsListeners);

    }

    private View.OnClickListener buttonsListeners = new View.OnClickListener(){
        public void onClick(View v)
        {
            if(v == btnAgregar){
                createSimpleDialog("Boton agregar","Salio bien");
            }
            if(v == btnModificar){
                createSimpleDialog("Boton Modificar","Salio bien");
            }
            if(v == btnEliminar){
                createSimpleDialog("Boton Eliminar","Salio bien");
            }
        }

    };


    private void createSimpleDialog(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
