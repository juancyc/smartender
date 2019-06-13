package com.example.smartender;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FloatingActionButton btnfabAgegar;
    private ListView listEventInfo;
    private ArrayList<Events> eventlist;
    private ArrayAdapter<Events> adapter;
    private DbHandler conn;
    private View vist;
    private Context currentcontex;
    private WeatherHandler whandler;

    private OnFragmentInteractionListener mListener;

    public EventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventFragment newInstance(String param1, String param2) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        currentcontex =  inflater.getContext();
        vist = inflater.inflate(R.layout.fragment_event, container, false);
        conn = new DbHandler(currentcontex,"db_Smartender",null,1);
        whandler = new WeatherHandler(currentcontex);
        eventlist = EventsDao.GetEventData(conn);
        adapter = new ArrayAdapter<Events>(currentcontex,R.layout.list_view,this.eventlist);

        btnfabAgegar = vist.findViewById(R.id.btnfabAgregar);
        listEventInfo = vist.findViewById(R.id.listviewEvents);
        listEventInfo.setAdapter(adapter);

        btnfabAgegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCalendarInputDataDialog(null);
            }
        });

        listEventInfo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Events data = eventlist.get(position);
                createButtonsDialog(data);
                return true;
            }
        });

        return  vist;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void createButtonsDialog(final Events eventData){
        AlertDialog.Builder builder = new AlertDialog.Builder(currentcontex);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(currentcontex);
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
                        DialogsHandler.createSimpleDialog(currentcontex,"Advertencia","No ha completado todos los campos necesarios para agregar un evento.");
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(currentcontex, new DatePickerDialog.OnDateSetListener() {
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(currentcontex, new TimePickerDialog.OnTimeSetListener() {
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
