package com.example.smartender;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnAgregar, btnModificar, btnEliminar;
    private ListView listCalendarInfo;
    private ArrayList<Calendar> calendarlist;
    private ArrayAdapter<Calendar> adapter;
    private int selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_info);
        calendarlist = new ArrayList<Calendar>();
        adapter = new ArrayAdapter<Calendar>(this,R.layout.list_view,this.calendarlist);

        calendarlist.add(new Calendar(1,"29/05/2019","15:30","futbol con pepeno"));
        calendarlist.add(new Calendar(2,"12/06/2019","08:30","casamiento de Carlos"));
        calendarlist.add(new Calendar(3,"22/06/2019","16:30","Veterinario para michin"));

        btnAgregar = (Button)findViewById(R.id.btnAgregar);
        listCalendarInfo = (ListView)findViewById(R.id.listCalendarInfo);
        listCalendarInfo.setAdapter(adapter);
        listCalendarInfo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Calendar data = calendarlist.get(position);
                createButtonsDialog(data);
                return true;
            }
        });

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCalendarInputDataDialog(null);
            }
        });


    }


    private void createButtonsDialog(final Calendar calendarinfo){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final CharSequence[] items = new CharSequence[2];
        items[0] = "Modificar Evento";
        items[1] = "Eliminar Evento";

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(items[which].toString().equals(items[0].toString())){
                    int index = calendarlist.indexOf(calendarinfo);
                    createCalendarInputDataDialog(calendarinfo);
                }else{
                    //Eliminar de la BD
                    int index = calendarlist.indexOf(calendarinfo);
                    calendarlist.remove(index);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        builder.show();
    }

    private void createCalendarInputDataDialog(final Calendar calendarinfo){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View v = inflater.inflate(R.layout.activity_calendar_inputdata,null);
        builder.setView(v);
        Button btnFecha = (Button) v.findViewById(R.id.btnFecha);
        Button btnHora = (Button) v.findViewById(R.id.btnHora);
        final EditText editTextFecha = (EditText) v.findViewById(R.id.editTextFecha);
        final EditText editTextHora = (EditText) v.findViewById(R.id.editTextHora);
        final EditText editTextDescripcion = (EditText) v.findViewById(R.id.editTextDescripcion);
        editTextFecha.setEnabled(false);
        editTextHora.setEnabled(false);

        if(calendarinfo == null){
            builder.setTitle("Agregar evento");
            builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if((editTextFecha.getText().length() > 0) && (editTextHora.getText().length() > 0) && (editTextDescripcion.getText().length() > 0)){
                        Calendar data = new com.example.smartender.Calendar();
                        data.setDate(editTextFecha.getText().toString());
                        data.setHour(editTextHora.getText().toString());
                        data.setDescription(editTextDescripcion.getText().toString());
                        calendarlist.add(data);
                        adapter.notifyDataSetChanged();
                        dialog.cancel();
                    }else{
                        DialogsHandler.createSimpleDialog(MainActivity.this,"Advertencia","No ha completado todos los campos necesarios para agregar un evento.");
                    }

                }
            });
        }else{
            builder.setTitle("Modificar evento");
            editTextFecha.setText(calendarinfo.getDate());
            editTextHora.setText(calendarinfo.getHour());
            editTextDescripcion.setText(calendarinfo.getDescription());
            builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Update en la BD
                    calendarinfo.setDescription(editTextDescripcion.getText().toString());
                    int index = calendarlist.indexOf(calendarinfo);
                    calendarlist.remove(index);
                    calendarlist.add(index,calendarinfo);
                    adapter.notifyDataSetChanged();
                    dialog.cancel();
                }
            });
        }

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final java.util.Calendar c = java.util.Calendar.getInstance(); // clase caledar de java, no la nuestra
                int day,month,year;
                day = c.get(java.util.Calendar.DAY_OF_MONTH);
                month = c.get(java.util.Calendar.MONTH);
                year = c.get(java.util.Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextFecha.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                        if(calendarinfo != null)
                            calendarinfo.setDate(editTextFecha.getText().toString());
                    }
                }
                        ,year,month,day);
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        btnHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final java.util.Calendar c = java.util.Calendar.getInstance(); // clase caledar de java, no la nuestra
                final int hour,minutes;
                hour = c.get(java.util.Calendar.HOUR_OF_DAY);
                minutes = c.get(java.util.Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        editTextHora.setText(hourOfDay+":"+minute);
                        if(calendarinfo != null)
                            calendarinfo.setHour(editTextHora.getText().toString());
                    }
                }
                        ,hour,minutes,false);

                timePickerDialog.show();
            }
        });


        builder.show();
    }



}
