package com.example.smartender;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
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

import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventsDao {

    private static final String TABLE_NAME = "eventos";
    private static final String ID = "id";
    private static final String FECHA = "fecha";
    private static final String HORA = "hora";
    private static final String DESCRIPCION = "descripcion";
    private static String weatherDescription = "";

    public static final String CREATE_EVENTS_TABLE = "CREATE TABLE "+ TABLE_NAME +
            " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+ FECHA +
            " TEXT, " + HORA +" TEXT, "+ DESCRIPCION +" TEXT)";

    public static final String DELETE_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;

    public static boolean AddEvent(DbHandler dbHandler,Events event){
        SQLiteDatabase database = dbHandler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FECHA,event.getDate());
        values.put(HORA,event.getHour());
        values.put(DESCRIPCION,event.getDescription());
        //String query = "INSERT INTO "+TABLE_NAME+" ("+FECHA+","+HORA+","+DESCRIPCION+") values ('"+event.getDate()+"','"+event.getHour()+"','"+event.getDescription()+"')";

        long id = -1;
        id = database.insert(TABLE_NAME,ID,values);

        if (id >= 0){
            event.setId((int) id);
            Toast.makeText(dbHandler.getContext(),"Evento registrado exitosamente",Toast.LENGTH_SHORT).show();
            database.close();
            return true;
        }
        database.close();
        Toast.makeText(dbHandler.getContext(),"No se pudo registrar el evento",Toast.LENGTH_SHORT).show();
        return false;
    }

    public static  boolean UpdateEvent(DbHandler dbHandler,Events event){
        SQLiteDatabase database = dbHandler.getWritableDatabase();
        //String query = "UPDATE nombretabla SET ";
        String[] paramters = {event.getId().toString()};
        ContentValues values = new ContentValues();
        values.put(FECHA,event.getDate());
        values.put(HORA,event.getHour());
        values.put(DESCRIPCION,event.getDescription());

        int error = database.update(TABLE_NAME,values,ID+"=?",paramters);
        if(error != 0){
            Toast.makeText(dbHandler.getContext(),"Evento actualizado exitosamente",Toast.LENGTH_SHORT).show();
            database.close();
            return true;
        }
        database.close();
        Toast.makeText(dbHandler.getContext(),"No se pudo actualizar el evento",Toast.LENGTH_SHORT).show();
        return false;
    }

    public static boolean DeleteEvent(DbHandler dbHandler,Events event){
        SQLiteDatabase database = dbHandler.getWritableDatabase();
        String[] paramters = {event.getId().toString()};
        int error = database.delete(TABLE_NAME,ID+"=?",paramters);
        if(error != 0){
            Toast.makeText(dbHandler.getContext(),"Evento eliminado exitosamente",Toast.LENGTH_SHORT).show();
            database.close();
            return true;
        }
        database.close();
        Toast.makeText(dbHandler.getContext(),"No se pudo eliminar el evento",Toast.LENGTH_SHORT).show();
        return false;
    }

    public static ArrayList<Events> GetEventData(DbHandler dbHandler){
        SQLiteDatabase database = dbHandler.getWritableDatabase();
        Events event = null;
        WeatherHandler whandler= new WeatherHandler(dbHandler.getContext());
        ArrayList<Events> eventlist = new ArrayList<Events>();
        String query= "SELECT * FROM "+TABLE_NAME;
        Cursor cursor = database.rawQuery(query,null);
        while (cursor.moveToNext()){
            event = new Events();
            event.setId(cursor.getInt(0));
            String date = cursor.getString(1);
            if(validateDate(date)){
                event.setDate(date);
                event.setHour(cursor.getString(2));
                event.setDescription(cursor.getString(3));
                getDayWeather(whandler,event.getDate());
                event.setWeatherdescription(getWeatherDescription());
                eventlist.add(event);
            }
        }

        return eventlist;
    }


    public static void getDayWeather(WeatherHandler weatherHandler, final String eventdate){
        final String city = weatherHandler.getWeatherDaysData().replace(" ", "%20");;
        if(city.length() == 0)
            setWeatherDescription("No hay datos del clima");
        final String url = "http://api.openweathermap.org/data/2.5/forecast?q=";
        final String key = "&appid=4c95f217ec82fde1928e70729b44c12a&units=Imperial&cnt=5";
        final String url_connection = url + city + key;
        JsonObjectRequest jObj = new JsonObjectRequest(Request.Method.GET, url_connection, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("list");
                    for (int i=0; i < array.length(); i++) {
                        JSONObject jDayForecast = array.getJSONObject(i);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        long dt = jDayForecast.getLong("dt") *1000;
                        String date = sdf.format(new Date(dt));
                        if(date.equals(eventdate)){
                            JSONArray jWeatherArr = jDayForecast.getJSONArray("weather");
                            JSONObject jWeatherObj = jWeatherArr.getJSONObject(0);
                            String desc = jWeatherObj.getString("description");
                            setWeatherDescription(desc);
                            break;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(weatherHandler.getContext());
        queue.add(jObj);


    }

    public static boolean validateDate(String date){
        try {
            Date date1 = new Date();
            Date date2=new SimpleDateFormat("dd/MM/yyyy").parse(date);
            if(date1.compareTo(date2)<=0)
                return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return  false;
    }

    public static String getWeatherDescription() {
        return weatherDescription;
    }

    public static void setWeatherDescription(String weatherDescription) {
        EventsDao.weatherDescription = weatherDescription;
    }
}
