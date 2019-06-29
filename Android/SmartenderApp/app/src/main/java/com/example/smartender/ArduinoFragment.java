package com.example.smartender;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ButtonBarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArduinoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArduinoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArduinoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View vist;
    private Context currentcontex;

    private TextView TextViewInfoArduino;
    private Button btnConexion;

    private OnFragmentInteractionListener mListener;

    public ArduinoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArduinoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArduinoFragment newInstance(String param1, String param2) {
        ArduinoFragment fragment = new ArduinoFragment();
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
        vist = inflater.inflate(R.layout.fragment_arduino, container, false);

        btnConexion = vist.findViewById(R.id.btnConexion);
        TextViewInfoArduino = vist.findViewById(R.id.TextViewInfoArduino);

        if(MainActivity.btHandler.btConeccted){
            //poner los datos
            btnConexion.setText("Desconecar Smartender");
        }
        else{
            TextViewInfoArduino.setText("No hay datos de Smartender");
            btnConexion.setText("Conectar Smartender");
        }

        btnConexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnConexion.getText().equals("Conectar Smartender")){
                    if(!MainActivity.btHandler.VerificarEstadoBT()){
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, 1);
                    }else {
                        tryConnect();
                    }
                }else {
                    try {
                        MainActivity.btHandler.MyConexionBT.write("9");
                        MainActivity.btHandler.btSocket.close();
                        MainActivity.setArduinoModo(false);
                        MainActivity.btHandler.btConeccted = false;
                        TextViewInfoArduino.setText("No hay datos de Smartender");
                        btnConexion.setText("Conectar Smartender");
                    } catch (IOException e) {
                        Toast.makeText(currentcontex,"No se pudo desconectar de Smartender",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });



        return vist;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == -1){
            tryConnect();
        }
    }

    public void tryConnect(){

        if(MainActivity.btHandler.Conectar()){
            MainActivity.setArduinoModo(true);
            Toast.makeText(currentcontex,"Conexion exitosa",Toast.LENGTH_LONG).show();
            //poner datos
            btnConexion.setText("Desconecar Smartender");
        }else {
            MainActivity.setArduinoModo(false);
            TextViewInfoArduino.setText("No hay datos de Smartender");
            Toast.makeText(currentcontex,"No se pudo conectar a Smartender",Toast.LENGTH_LONG).show();
        }
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
