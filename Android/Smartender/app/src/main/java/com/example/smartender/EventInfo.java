package com.example.smartender;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import java.util.ArrayList;

public class EventInfo extends AppCompatActivity {

    private Button btnAgregar;
    private ListView listEventInfo;
    private ArrayList<Events> eventlist;
    private ArrayAdapter<Events> adapter;
    private int selectedItem;
    private DbHandler conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        conn = new DbHandler(this,"db_Smartender",null,1);
        eventlist = EventsDao.GetEventData(conn);
        adapter = new ArrayAdapter<Events>(this,R.layout.list_view,this.eventlist);

        btnAgregar = findViewById(R.id.btnAgregar);
        listEventInfo = findViewById(R.id.listEventInfo);
        listEventInfo.setAdapter(adapter);
        listEventInfo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Events data = eventlist.get(position);
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

    private void createButtonsDialog(final Events eventData){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final CharSequence[] items = new CharSequence[2];
        items[0] = "Modificar Evento";
        items[1] = "Eliminar Evento";

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(items[which].toString().equals(items[0].toString())){
                    int index = eventlist.indexOf(eventData);
                    createCalendarInputDataDialog(eventData);
                }else{
                    if(EventsDao.DeleteEvent(conn,eventData)){
                        eventlist.remove(eventData);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        builder.show();
    }

    private void createCalendarInputDataDialog(final Events eventData){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.event_input_data,null);
        builder.setView(v);
        Button btnFecha = v.findViewById(R.id.btnFecha);
        Button btnHora = v.findViewById(R.id.btnHora);
        final EditText editTextFecha = v.findViewById(R.id.editTextFecha);
        final EditText editTextHora = v.findViewById(R.id.editTextHora);
        final EditText editTextDescripcion = v.findViewById(R.id.editTextDescripcion);
        editTextFecha.setEnabled(false);
        editTextHora.setEnabled(false);

        if(eventData == null){
            builder.setTitle("Agregar evento");
            builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if((editTextFecha.getText().length() > 0) && (editTextHora.getText().length() > 0) && (editTextDescripcion.getText().length() > 0)){
                        Events data = new Events();
                        data.setDate(editTextFecha.getText().toString());
                        data.setHour(editTextHora.getText().toString());
                        data.setDescription(editTextDescripcion.getText().toString());
                        if(EventsDao.AddEvent(conn,data)){
                            eventlist.add(data);
                            adapter.notifyDataSetChanged();
                        }
                        dialog.cancel();
                    }else{
                        DialogsHandler.createSimpleDialog(EventInfo.this,"Advertencia","No ha completado todos los campos necesarios para agregar un evento.");
                    }

                }
            });
        }else{
            builder.setTitle("Modificar evento");
            editTextFecha.setText(eventData.getDate());
            editTextHora.setText(eventData.getHour());
            editTextDescripcion.setText(eventData.getDescription());
            builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    eventData.setDescription(editTextDescripcion.getText().toString());
                    int index = eventlist.indexOf(eventData);
                    if(EventsDao.UpdateEvent(conn,eventData)){
                        eventlist.remove(index);
                        eventlist.add(index,eventData);
                        adapter.notifyDataSetChanged();
                    }
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
                final java.util.Calendar c = java.util.Calendar.getInstance();
                int day,month,year;
                day = c.get(java.util.Calendar.DAY_OF_MONTH);
                month = c.get(java.util.Calendar.MONTH);
                year = c.get(java.util.Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EventInfo.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextFecha.setText(dayOfMonth +"/"+(month+1)+"/"+year);
                        if(eventData != null)
                            eventData.setDate(editTextFecha.getText().toString());
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
                final java.util.Calendar c = java.util.Calendar.getInstance();
                final int hour,minutes;
                hour = c.get(java.util.Calendar.HOUR_OF_DAY);
                minutes = c.get(java.util.Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(EventInfo.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        editTextHora.setText(hourOfDay+":"+minute);
                        if(eventData != null)
                            eventData.setHour(editTextHora.getText().toString());
                    }
                }
                        ,hour,minutes,false);

                timePickerDialog.show();
            }
        });


        builder.show();
    }
}
