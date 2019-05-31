package com.example.smartender;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;

public class EventsDao {

    private static final String TABLE_NAME = "eventos";
    private static final String ID = "id";
    private static final String FECHA = "fecha";
    private static final String HORA = "hora";
    private static final String DESCRIPCION = "descripcion";

    public static final String CREATE_EVENTS_TABLE = "CREATE TABLE "+ TABLE_NAME +
            " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+ FECHA +
            " TEXT, " + HORA +" TEXT, "+ DESCRIPCION +" TEXT)";

    public static final String DELETE_EVENTS_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;

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
        ArrayList<Events> eventlist = new ArrayList<Events>();
        String query= "SELECT * FROM "+TABLE_NAME;
        Cursor cursor = database.rawQuery(query,null);
        while (cursor.moveToNext()){
            event = new Events();
            event.setId(cursor.getInt(0));
            event.setDate(cursor.getString(1));
            event.setHour(cursor.getString(2));
            event.setDescription(cursor.getString(3));
            eventlist.add(event);
        }

        return eventlist;
    }
}
