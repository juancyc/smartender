package com.example.smartender;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements LocationListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LocationManager mLocationManager;
    private TextView textViewDateHour, textViewTemperature, textViewCity, textViewHumidity, textViewDescription;
    private ImageView imageWeather;
    private Context currentcontex;
    private View vist;


    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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
        // Inflate the layout for this fragment
        currentcontex = inflater.getContext();
        vist = inflater.inflate(R.layout.fragment_main, container, false);
        textViewDateHour = vist.findViewById(R.id.textViewDateHour);
        textViewTemperature = vist.findViewById(R.id.textViewTemperature);
        textViewCity = vist.findViewById(R.id.textViewCity);
        textViewHumidity = vist.findViewById(R.id.textViewHumedad);
        textViewDescription = vist.findViewById(R.id.textViewDescripcion);
        imageWeather = vist.findViewById(R.id.imageWeather);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM", Locale.getDefault());
        Date date = new Date();
        String fecha = dateFormat.format(date);
        textViewDateHour.setText(fecha);
        return vist;
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
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(currentcontex, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(currentcontex, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)){
                createPermisionDialog();
            }else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
            }
        }else{
            double lat = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
            double lon = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
            getLocationName(lat,lon);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
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

    private void createPermisionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(currentcontex);
        builder.setTitle("Permisos desactivados");
        builder.setMessage("Debe aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
            }
        });

        builder.show();

    }

    private void getLocationName(double latitude, double longitude){
        if(latitude != 0.0 && longitude != 0.0){
            try {
                Geocoder geocoder = new Geocoder(currentcontex, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(latitude, longitude, 1);
                if (!list.isEmpty()) {
                    Address address = list.get(0);
                    String lugar = address.getLocality() + "," + address.getCountryName();
                    if(lugar.length() != 0 && !lugar.contains("null")){
                        String aux = Normalizer.normalize(lugar,Normalizer.Form.NFD);
                        aux = aux.replaceAll("[^\\p{ASCII}]", "");
                        setWeathe(aux,lugar);
                    }else {
                        textViewCity.setText("Ubicacion desconocida");
                        textViewTemperature.setText("---");
                        textViewHumidity.setText("---");
                        textViewDescription.setText("");
                    }
                }

            }catch (IOException e) {
                Toast.makeText(currentcontex,"No se puede obtener datos del clima",Toast.LENGTH_SHORT);
            }

        } else {
            textViewCity.setText("Ubicacion desconocida");
            textViewTemperature.setText("---");
            textViewHumidity.setText("---");
            textViewDescription.setText("");
        }
    }

    private void setWeathe(String loc, final String city_name){
        String url = "http://api.openweathermap.org/data/2.5/weather?q=";
        url += loc.replace(" ", "%20");
        url += "&appid=4c95f217ec82fde1928e70729b44c12a&units=Imperial";
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main_objet = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    String temp = String.valueOf(main_objet.getDouble("temp"));
                    String hum = String.valueOf(main_objet.getInt("humidity"));
                    String descripcion = object.getString("description");
                    double temp_int = Double.parseDouble(temp);
                    double centi = (temp_int -32)/1.8000;
                    centi = Math.round(centi);
                    int i = (int)centi;
                    textViewCity.setText(city_name);
                    textViewTemperature.setText(String.valueOf(i)+" ℃");
                    textViewHumidity.setText(hum+"%");
                    textViewDescription.setText(descripcion);
                } catch (JSONException e) {
                    Toast.makeText(currentcontex,"No se puede obtener datos del clima",Toast.LENGTH_SHORT);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textViewTemperature.setText("---");
                textViewHumidity.setText("---");
                textViewDescription.setText("");
                textViewCity.setText("Ubicacion desconocida");
            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(currentcontex);
        queue.add(jor);

    }

    @Override
    public void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i("Clima", "Provider " + provider + " has now status: " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i("Clima", "Provider " + provider + " is enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i("Clima", "Provider " + provider + " is disabled");
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


}
