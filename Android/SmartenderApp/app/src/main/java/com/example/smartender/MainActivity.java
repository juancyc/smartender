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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        EventFragment.OnFragmentInteractionListener,
        DatosFragment.OnFragmentInteractionListener,
        MainFragment.OnFragmentInteractionListener,
        ArduinoFragment.OnFragmentInteractionListener,
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
    public static boolean isShacking;

    /*
            Override Metods
     */

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

        //seteo el main fragment para que aparesca por defecto
        Fragment fragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.content_main,fragment).commit();
        navigationView.setNavigationItemSelectedListener(this);

        //seteo los flag de modo de trabajo
        setTenderHidden(true);
        setArduinoModo(false);
        setIsShacking(false);

        //creo objeto para manejar la base de datos
        conn = new DbHandler(this,"db_Smartender",null,1);

        //creo un objeto para conectarme al bluetooh
        btHandler = new BTHandler();

        //seteo el acelerometro
        shakeSensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        shakeSensorManager.registerListener(this, shakeSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        acelVal = SensorManager.GRAVITY_EARTH;
        acelLast = SensorManager.GRAVITY_EARTH;
        shake = 0.00f;

        //seteo el sensor de proximidad
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
    protected void onDestroy() {
        super.onDestroy();
        //me desconecto del bluetooh
        try
        {
            btHandler.Desconectar();
        } catch (IOException e2) {
            Log.i("ERROR MAIN","ON DESTROY");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //evento que maneja los cambios de fragmente dependiendo de cual se selecciono
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
            myfragment = new ArduinoFragment();
            ArduinoFragment.estoy = true;
            selectedFragment = true;
            toolbar.setTitle("Datos Smartender");
        }

        //cambio el content main por el fragment que se selecciono
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //metodo que cuando recibe que me conecte al bluetooh, trata de conectarse al arduino
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            tryConnect();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()){
            case Sensor.TYPE_PROXIMITY: eventAProx(event);break;
            case Sensor.TYPE_ACCELEROMETER: eventShake(event);break;
        }
    }

    /*
            Eventos del los sensores
     */

    public boolean checkShake(SensorEvent event){
        //Metodo encargado de verificar los datos del evento del sensor acelerometro para detectar shake
        if(isIsShacking())
            return false;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        acelLast=acelVal;
        acelVal= (float) Math.sqrt((double) (x*x + y*y + z*z));
        float delta = acelVal-acelLast;
        shake = shake * 0.9f + delta;

        return shake>12 ? true : false;
    }

    public void eventAProx(SensorEvent event){
        //Metodo encargado de verificar los datos del evento del sensor de proximidad
        float x = event.values[0];
        Sensor sensorprox = proxSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if(x <  sensorprox.getMaximumRange()){
            if(btHandler.btConeccted)
                btHandler.MyConexionBT.write("5");
        }else{
            if(btHandler.btConeccted)
                btHandler.MyConexionBT.write("8");
        }

    }

    public void eventShake(SensorEvent event){
        /*
            1-verifico el shake
            2-verifico si estoy en modo arduino, si no lo estoy pido conectar
            3-Si estoy en modo arduino, veo donde estaba el tender
                si estaba al sol lo oculto de una y actualizo la BD
                si estaba oculto: obtengo los datos de la pantalla y verifico el estado del clima
         */
        if(checkShake(event)){
            setIsShacking(true);
            if(isArduinoModo()){
                if(isTenderHidden()){
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_main);
                    if(currentFragment instanceof MainFragment){
                        TextView textViewTemp = currentFragment.getView().findViewById(R.id.textViewTemperature);
                        TextView textViewHumidity = currentFragment.getView().findViewById(R.id.textViewHumedad);
                        TextView textViewLugar = currentFragment.getView().findViewById(R.id.textViewHumedad);
                        try{
                            int temp = Integer.parseInt(textViewTemp.getText().toString().split(" ")[0]);
                            int hum = Integer.parseInt(textViewHumidity.getText().toString().split("%")[0]);
                            if(verifyTenderStatus(temp,hum))
                                textViewLugar.setText("El tender se encuentra al sol");

                        }catch (Exception e){
                            setIsShacking(false);
                        }
                    }
                }else{
                    if(btHandler.btConeccted && btHandler.VerificarEstadoBT()){
                        btHandler.MyConexionBT.write("1");
                        setTenderHidden(true);
                        addTenderDataToBD("Ocultamiento mediante SHAKE");
                        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_main);
                        if(currentFragment instanceof MainFragment){
                            TextView textViewLugar = currentFragment.getView().findViewById(R.id.textViewHumedad);
                            textViewLugar.setText("El tender se encuentra oculto");
                        }
                        setIsShacking(false);
                    }else {
                        setTenderHidden(false);
                        setIsShacking(false);
                        Toast.makeText(this,"No se encuentra conectado a Smartender",Toast.LENGTH_LONG).show();
                    }
                }
            }else {
                showDialogConnect();
            }
        }
    }

    public void showDialogConnect(){
        //Metodo encargado de mostrar el dialogo para que te conectes al arduino
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Smartender");
        builder.setMessage("No se encuentra conectado a Smarteneder.\nDesea conectarlo ?");
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //si aprito si, primero verifico si estoy conectado al bluetooh, si no lo estoy pide que lo conectes
                if(!btHandler.VerificarEstadoBT()){
                    //intent a una pantalla propia del bluetooth adapter
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
                setArduinoModo(false);
                setIsShacking(false);
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void tryConnect(){
        //llama al metodo conectar de la clase bluetooh, si se conecta setea el flag de modo de trabajo arduino
        if(btHandler.Conectar()){
            Toast.makeText(this,"Conexion exitosa",Toast.LENGTH_LONG).show();
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_main);
            if(currentFragment instanceof ArduinoFragment){
                TextView textViewDatos = currentFragment.getView().findViewById(R.id.TextViewInfoArduino);
                Button btnConexion = currentFragment.getView().findViewById(R.id.btnConexion);
                btnConexion.setText("Desconecar Smartender");
                //poner los datos del arduino en textview
            }
            setArduinoModo(true);
            setIsShacking(false);
        }else {
            setArduinoModo(false);
            setIsShacking(false);
            Toast.makeText(this,"No se pudo conectar a Smartender",Toast.LENGTH_LONG).show();
        }

    }

    public void addTenderDataToBD(String Reason){
        //Agrega a la base de datos los datos de cuando muevo el tender
        final java.util.Calendar c = java.util.Calendar.getInstance();
        int day,month,year,hour,minutes;
        day = c.get(java.util.Calendar.DAY_OF_MONTH);
        month = c.get(java.util.Calendar.MONTH);
        year = c.get(java.util.Calendar.YEAR);
        hour = c.get(java.util.Calendar.HOUR_OF_DAY);
        minutes = c.get(java.util.Calendar.MINUTE);
        Tender tender = new Tender();
        tender.setDate(EventFragment.checkFormatNumber(day)+"/"+EventFragment.checkFormatNumber(month)+"/"+year);
        tender.setHour(EventFragment.checkFormatNumber(hour)+":"+EventFragment.checkFormatNumber(minutes));
        tender.setReason(Reason);
        TenderDao.AddTenderData(conn,tender);
    }

    public boolean verifyTenderStatus(int temp,int hum){
        //verifica si el clima esta bien para mover el tender al sol y tambien si estoy conectado
        if(WeatherHandler.isWheatherOK(temp,hum)){
            if(btHandler.btConeccted && btHandler.VerificarEstadoBT()){
                btHandler.MyConexionBT.write("2");
                setTenderHidden(false);
                addTenderDataToBD("Salida al sol mediante SHAKE");
                setIsShacking(false);
                return true;
            }else {
                Toast.makeText(this,"No se encuentra conectado a Smartender",Toast.LENGTH_LONG).show();
                setIsShacking(false);
            }
        }else {
            DialogsHandler.createSimpleDialog(this,"Advertencia","Las condiciones climaticas no son buenas para colgar la ropa.");
            setIsShacking(false);
            return false;
        }

        return false;
    }


    /*
            Getters and Setters
     */

    public static boolean isTenderHidden() {
        return tenderHidden;
    }

    public static void setTenderHidden(boolean tenderHidden) {
        MainActivity.tenderHidden = tenderHidden;
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

    public static boolean isIsShacking() {
        return isShacking;
    }

    public static void setIsShacking(boolean isShacking) {
        MainActivity.isShacking = isShacking;
    }
}
