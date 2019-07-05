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
import android.telecom.GatewayInfo;
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
import com.squareup.picasso.Picasso;

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
    private TextView textViewDateHour, textViewTemperature, textViewCity, textViewHumidity, textViewDescription, textViewLugarTender;
    private ImageView imageWeather;
    private Context currentcontex;
    private WeatherHandler weatherHandler;
    private View vist;


    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /*
            Override metods
            los que importan son onCreateView y onResume()
     */

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
        textViewLugarTender = vist.findViewById(R.id.textViewLugarTender);

        return vist;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Verifico si tengo permisos para GPS
        if (ActivityCompat.checkSelfPermission(currentcontex, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(currentcontex, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            try{
                //Obtengo la latitud y longitud con el location manager
                //Despues llamo al metodo que me obtiene los datos
                weatherHandler = new WeatherHandler(currentcontex);
                double lat = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
                double lon = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
                getLocationName(lat,lon);
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }catch (Exception e){
                double lat = -34.6699075563091;
                double lon = -58.56386728584767;
                getLocationName(lat,lon);
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
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

    /*
        Metodos que me pido crear
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*
        Metodos propios
     */

    private void getLocationName(double latitude, double longitude){
        //en base a la latid y longitud obtiene el nombre del lugar y se lo pasa a la API del clima
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
                        setWeather(aux,lugar);
                    }else {
                        setDate();
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
            setDate();
            textViewCity.setText("Ubicacion desconocida");
            textViewTemperature.setText("---");
            textViewHumidity.setText("---");
            textViewDescription.setText("");
        }
    }

    private void setWeather(String loc, final String city_name){
        //Busca los datos del JSON que obtiene y los setea en la pantalla
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
                    String descripcion = WeatherHandler.changeWheatherName(object.getString("description"));
                    String icon = object.getString("icon");
                    String iconUrl = "http://openweathermap.org/img/w/" + icon + ".png";
                    double temp_int = Double.parseDouble(temp);
                    double centi = (temp_int -32)/1.8000;
                    centi = Math.round(centi);
                    int i = (int)centi;
                    setDate();
                    textViewCity.setText(city_name);
                    textViewTemperature.setText(String.valueOf(i)+" ℃");
                    textViewHumidity.setText(hum+"%");
                    textViewDescription.setText(descripcion);
                    Picasso.with(currentcontex).load(iconUrl).into(imageWeather);
                    imageWeather.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    Toast.makeText(currentcontex,"No se puede obtener datos del clima",Toast.LENGTH_SHORT);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setDate();
                textViewTemperature.setText("---");
                textViewHumidity.setText("---");
                textViewDescription.setText("");
                textViewCity.setText("Ubicacion desconocida");
                imageWeather.setVisibility(View.INVISIBLE);
            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(currentcontex);
        queue.add(jor);

    }

    public void setDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM", Locale.getDefault());
        Date date = new Date();
        String fecha = dateFormat.format(date);
        textViewDateHour.setText(fecha);
    }


}
