package com.example.smartender;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;

public class TenderDao {

    private static final String TABLE_NAME = "datos_tender";
    private static final String ID = "id";
    private static final String FECHA = "fecha";
    private static final String HORA = "hora";
    private static final String TIEMPO = "tiempo_total";
    private static final String RAZON = "razon";

    public static String CREATE_TABLE = "CREATE TABLE "+ TABLE_NAME +
            " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+ FECHA +
            " TEXT, " + HORA +" TEXT, "+TIEMPO +" TEXT, "+RAZON +" TEXT)";

    public static final String DELETE_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;

    public static boolean AddTenderData(DbHandler dbHandler,Tender tender){
        SQLiteDatabase database = dbHandler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FECHA,tender.getDate());
        values.put(HORA,tender.getHour());
        values.put(TIEMPO,String.valueOf(tender.getTime()));
        values.put(RAZON,tender.getReason());
        //String query = "INSERT INTO "+TABLE_NAME+" ("+FECHA+","+HORA+","+DESCRIPCION+") values ('"+event.getDate()+"','"+event.getHour()+"','"+event.getDescription()+"')";

        long id = -1;
        id = database.insert(TABLE_NAME,ID,values);

        if (id >= 0){
            tender.setId((int) id);
            Toast.makeText(dbHandler.getContext(),"Datos registrado exitosamente",Toast.LENGTH_SHORT).show();
            database.close();
            return true;
        }
        database.close();
        Toast.makeText(dbHandler.getContext(),"No se pudo registrar los datos",Toast.LENGTH_SHORT).show();
        return false;
    }

    public static ArrayList<Tender> GetTenderData(DbHandler dbHandler){
        SQLiteDatabase database = dbHandler.getWritableDatabase();
        Tender tender = null;
        ArrayList<Tender> tenderlist = new ArrayList<Tender>();
        String query= "SELECT * FROM "+TABLE_NAME;
        Cursor cursor = database.rawQuery(query,null);
        while (cursor.moveToNext()){
            tender = new Tender();
            tender.setId(cursor.getInt(0));
            tender.setDate(cursor.getString(1));
            tender.setHour(cursor.getString(2));
            tender.setTime(Double.parseDouble(cursor.getString(3)));
            tender.setReason(cursor.getString(4));
            tenderlist.add(tender);
        }

        return tenderlist;
    }
}
