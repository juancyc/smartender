package com.example.smartender;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        EventFragment.OnFragmentInteractionListener,
        DatosFragment.OnFragmentInteractionListener,
        MainFragment.OnFragmentInteractionListener,
        SensorEventListener {

    private DbHandler conn;
    private Toolbar toolbar;
    private SensorManager shakeSensorManager;
    private SensorManager proxSensorManager;
    private float acelVal;
    private float acelLast;
    private float shake;
    public static boolean tenderHidden;
    public static BTHandler btHandler = null;
    public static boolean ArduinoModo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        Fragment fragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.content_main,fragment).commit();
        navigationView.setNavigationItemSelectedListener(this);
        conn = new DbHandler(this,"db_Smartender",null,1);
        setTenderHidden(true); // obtener donde se encuentra en verdad
        setArduinoModo(false);
        btHandler = new BTHandler();

        shakeSensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        shakeSensorManager.registerListener(this, shakeSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        acelVal = SensorManager.GRAVITY_EARTH;
        acelLast = SensorManager.GRAVITY_EARTH;
        shake = 0.00f;

        proxSensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        proxSensorManager.registerListener(this, proxSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(btHandler.btConeccted){
            try
            {
                btHandler.MyConexionBT.write("9");
                btHandler.btSocket.close();
                setArduinoModo(false);
                btHandler.btConeccted = false;
            } catch (IOException e2) {}
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try
        {
            btHandler.MyConexionBT.write("9");
            btHandler.btSocket.close();
        } catch (IOException e2) {}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment myfragment = null;
        boolean selectedFragment = false;

        if (id == R.id.nav_home) {
            myfragment = new MainFragment();
            selectedFragment = true;
            toolbar.setTitle("Smartender");
        } else if (id == R.id.nav_event) {
            myfragment = new EventFragment();
            selectedFragment = true;
            toolbar.setTitle("Mis eventos");
        } else if (id == R.id.nav_datos) {
            myfragment = new DatosFragment();
            selectedFragment = true;
            toolbar.setTitle("Datos Historicos");
        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        if(selectedFragment){
           getSupportFragmentManager().beginTransaction().replace(R.id.content_main,myfragment).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()){
            case Sensor.TYPE_PROXIMITY: eventAProx(event);break;
            case Sensor.TYPE_ACCELEROMETER: eventShake(event);break;
        }
    }

    public void eventAProx(SensorEvent event){
        float x = event.values[0];
        Sensor sensorprox = proxSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if(x <  sensorprox.getMaximumRange()){
            //poner led en blando
            Log.i("SENSORES","NO HAY LUZ");
        }else{
            Log.i("SENSORES","HAY LUZ");
            //ver que clima hay y en eso cambiar los colores de los led
        }

    }

    public void eventShake(SensorEvent event){
        if(checkShake(event)){
            if(isArduinoModo()){
                if(isTenderHidden()){
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_main);
                    if(currentFragment instanceof MainFragment){
                        TextView textViewTemp = currentFragment.getView().findViewById(R.id.textViewTemperature);
                        TextView textViewHumidity = currentFragment.getView().findViewById(R.id.textViewHumedad);
                        try{
                            int temp = Integer.parseInt(textViewTemp.getText().toString().split(" ")[0]);
                            int hum = Integer.parseInt(textViewHumidity.getText().toString().split("%")[0]);
                            verifyTenderStatus(temp,hum);
                        }catch (Exception e){
                            //obtener datos del arduino
                            //verifyTenderStatus(temp,hum);
                        }
                    }else {
                        //obtener datos del arduino
                        //verifyTenderStatus(temp,hum);
                    }
                }else{
                    if(btHandler.btConeccted && btHandler.VerificarEstadoBT()){
                        btHandler.MyConexionBT.write("1");
                        setTenderHidden(true);
                        addTenderDataToBD("Ocultamiento mediante SHAKE");
                    }else {
                        Toast.makeText(this,"No se encuentra conectado a Smartender",Toast.LENGTH_LONG).show();
                    }
                }
            }else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Smartender");
                builder.setMessage("No se encuentra conectado a Smarteneder.\nDesea conectarlo ?");
                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!btHandler.VerificarEstadoBT()){
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, 1);
                        }else {
                            dialog.cancel();
                            tryConnect();
                        }
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArduinoModo = false;
                        dialog.cancel();
                    }
                });
                builder.show();

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            tryConnect();
        }
    }

    public void tryConnect(){
        if(btHandler.Conectar()){
            setBtHandler(btHandler);
            setArduinoModo(true);
            Toast.makeText(this,"Conexion exitosa",Toast.LENGTH_LONG).show();
        }else {
            MainActivity.setArduinoModo(false);
            Toast.makeText(this,"No se pudo conectar a Smartender",Toast.LENGTH_LONG).show();
        }

    }

    public boolean checkShake(SensorEvent event){
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        acelLast=acelVal;
        acelVal= (float) Math.sqrt((double) (x*x + y*y + z*z));
        float delta = acelVal-acelLast;
        shake = shake * 0.9f + delta;

        return shake>12 ? true : false;
    }

    public static boolean isTenderHidden() {
        return tenderHidden;
    }

    public static void setTenderHidden(boolean tenderHidden) {
        MainActivity.tenderHidden = tenderHidden;
    }

    public void addTenderDataToBD(String Reason){
        final java.util.Calendar c = java.util.Calendar.getInstance();
        int day,month,year,hour,minutes;
        day = c.get(java.util.Calendar.DAY_OF_MONTH);
        month = c.get(java.util.Calendar.MONTH);
        year = c.get(java.util.Calendar.YEAR);
        hour = c.get(java.util.Calendar.HOUR_OF_DAY);
        minutes = c.get(java.util.Calendar.MINUTE);
        Tender tender = new Tender();
        tender.setDate(day+"/"+month+"/"+year);
        tender.setHour(hour+":"+minutes);
        tender.setReason(Reason);
        TenderDao.AddTenderData(conn,tender);
    }

    public void verifyTenderStatus(int temp,int hum){
        if(WeatherHandler.isWheatherOK(temp,hum)){
            if(btHandler.btConeccted && btHandler.VerificarEstadoBT()){
            btHandler.MyConexionBT.write("2");
            setTenderHidden(false);
            addTenderDataToBD("Salida al sol mediante SHAKE");
            Log.i("SENSORES","ESTOY AL SOL AHORA");
            }else {
                Toast.makeText(this,"No se encuentra conectado a Smartender",Toast.LENGTH_LONG).show();
            }

        }else {
            DialogsHandler.createSimpleDialog(this,"Advertencia","Las condiciones climaticas no son buenas para colgar la ropa.");
        }
    }

    public static BTHandler getBtHandler() {
        return btHandler;
    }

    public static void setBtHandler(BTHandler btHandler) {
        MainActivity.btHandler = btHandler;
    }

    public static boolean isArduinoModo() {
        return ArduinoModo;
    }

    public static void setArduinoModo(boolean arduinoModo) {
        ArduinoModo = arduinoModo;
    }
}
