//Librerias necesarias para la conexion Bluetooth
#include <Wire.h>
#include <SPI.h>
#include <Adafruit_BMP280.h> //Libreria para manejar el BMP
#include <SoftwareSerial.h> //Libreria para manejar el serial del bluetooth

#define RxBluntu 3 //Receptor bluetooth
#define TxBluntu 2 //Transmisor bluetooth

#define BMP_SCK  (13) //Constantes necesarias para el bmp
#define BMP_MISO (12)
#define BMP_MOSI (11)
#define BMP_CS   (10)
Adafruit_BMP280 bmp;
SoftwareSerial BT(3,2); //Asigna los pines 2 y 3 al serial para el bluetooth. Constructor


/*#include <DHT.h>

#define DHTPIN 8
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);
*/
int m1Izq = 4; //pin motor a la izq
int m1Der = 5; //pin motor a la der
int secando = 0; //0 esta abajo del techo, 1 esta al sol 
const int pinbuzzer = 7; //pin para el buzzer
const int pinLDR = 12; //pin del sensor de luz
const int pinRojo = 9; //pin manejo luz roja led RGB
const int pinVerde = 10; //pin manejo luz verde led RGB
const int pinAzul =11; //pin manejo luz azul led RGB
unsigned long previousMillis = 0; //milisegundos de comienzo
const long interval = 2000; //tiempo de cada intervalo = 2 seg

float hum,temp,pres,tempBmp; //valores de humedad, temperatura del DHT11, presion y temperatura del bmp
int lectura; //lo que lee del bluetooth. Los valores que luego utiliza para el switch
int btConectado = LOW; //se conecto o no al bt
int btLedVerde = LOW; //bandera de control de led verde
int btLedRojo = LOW; //bandera de control de led rojo
int btLedAzul = LOW; //bandera de control de led azul
int btOrdenEsconder = LOW; //Orden obtenida del bt


void setup() {
  Serial.begin(9600); //arranca el serial con 9600 baudios de sincronismo
  BT.begin(9600); //Idem, bt funciona con serial
  pinMode(pinbuzzer,OUTPUT); //setea el buzzer como sensor de salida
  pinMode(pinLDR,INPUT); //setea el LDR como sensor de entrada
  pinMode(m1Izq,OUTPUT); //setea el control del manejo del motor hacia la izq como salida
  pinMode(m1Der,OUTPUT);//idem anterior con derecha
  digitalWrite(pinbuzzer,HIGH);//el HIGH del buzzer en realidad es estar apagado, LOW es cuando suena. Logica inversa.
  bmp.begin();//inicia el sensor bmp y abajo hace cosas trambolicas propias de bmp
    /* Default settings from datasheet. */
  bmp.setSampling(Adafruit_BMP280::MODE_NORMAL,     /* Operating Mode. */
                  Adafruit_BMP280::SAMPLING_X2,     /* Temp. oversampling */
                  Adafruit_BMP280::SAMPLING_X16,    /* Pressure oversampling */
                  Adafruit_BMP280::FILTER_X16,      /* Filtering. */
                  Adafruit_BMP280::STANDBY_MS_500); /* Standby time. */
  secuenciaInicio();  
}

void loop() {
  

 unsigned long currentMillis = millis(); //obtiene en miliseg el tiempo desde que se enciende el arduino
 
   if (currentMillis - previousMillis >= interval) { //si el intervalo entre el tiempo ejecutado y el de inicio es mayor al intervalo
    digitalWrite(m1Der,LOW); //detiene el movimiento del motor a la derecha
    digitalWrite(m1Izq,LOW); //detiene el movimiento del motor a la izquierda
    digitalWrite(pinbuzzer,HIGH); //no hace ruido el buzzer
    previousMillis = currentMillis; //asigna como miliseg previos los actuales
    hum = 35; 
    //int dhtDisp = leerDHT();
    //hum = dht.readHumidity();
    //temp = dht.readTemperature();
    if(BT.available()){ //si puede leer info del bluetooth arranca por ahi
      lectura = BT.read(); //obtiene la data mandada del bt y la guarda en la variable lectura
      setearFlags(); //setea los flags segun lectura
      Serial.print("Lectura del telefono: "); //manda texto al igual que println
      Serial.write(lectura); //manda bits en vez de texto
      Serial.println(" ");
    }
    if(Serial.available()>0){ //Si no consigue obtener info del bt, se fija si le manda algo desde el serial/computadora
      tempBmp = Serial.parseFloat(); //
    }
    else{
    tempBmp = bmp.readTemperature(); //si no lee del bt ni del serial, saca la data del sensor de temperatura
    }
    pres = bmp.readPressure()/100; //divide por 100 porque esta en pascales
    Serial.print("Humedad: ");
    Serial.println(hum);
    Serial.print("Temperatura: ");
    Serial.println(tempBmp);
    Serial.print("Presion: ");
    Serial.println(pres);/*
    BT.print("Humedad: ");
    BT.println(hum);
    BT.print("Temperatura: ");
    BT.println(tempBmp);
    BT.print("Presion: ");
    BT.println(pres);*/
    
    
    BT.print(String(tempBmp)+"-"+String(hum)+"-"+String(pres));  //le pasa la temperatura al bt
    
    
    BT.println(); //le manda el /n que necesita el android para poder comenzar a procesar la info enviada
    if(btConectado==LOW){ //si no esta el bt conectado, modo arduino
    cambiarLeds(tempBmp); //cambia los leds segun la temperatura obtenida del sensor bmp
        if(nohayLu()==HIGH||(hum>60)||tempBmp<10||pres<1007||btOrdenEsconder==HIGH){ //si se cumple alguna de las condiciones como para esconder el tender...
          Serial.println("Condiciones no optimas");
          //no hay lu
          if(secando != 0){ //se fija si el tender estaba al sol
              Serial.println("Escondo"); //si estaba, lo esconde por las malas condiciones de clima
              digitalWrite(pinbuzzer,LOW); //PIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIP
              digitalWrite(m1Der,HIGH); //el motor se mueve a la derecha
              digitalWrite(m1Izq,LOW); // no se activa moverlo a la izq
              secando = 0; //avisa que la ropa esta bajo techo
            }
  }
 else{ //si estan las condiciones como para ponerlo al sol
      Serial.println("Condiciones optimas");
      if(secando == 0){ //se fija si estaba bajo techo y de ser asi lo mueve hacia afuera
          Serial.println("Muestro");
          digitalWrite(pinbuzzer,LOW);//PIIIIIIIIIIIIIIIIIIIIIIIIIIP
        digitalWrite(m1Der,LOW);
        digitalWrite(m1Izq,HIGH);
          
          secando = 1; //la ropa esta al sol
      }
  //hay lu
  }
    }
    else{
      //leeriamos antes los valores del android
      Serial.println("Bluntu mode");
      cambiarLedsBT(); //cambia los leds segun la temperatura dada por el bt
      if(btOrdenEsconder == HIGH && secando !=0){ //si el bt da la orden de poner la ropa bajo techo y estaba previamente al sol...
         Serial.println("Esconder");
         digitalWrite(pinbuzzer,LOW); //PIIIIIIIIIIIIIIIIIIIIP
         digitalWrite(m1Der,HIGH); 
         digitalWrite(m1Izq,LOW);
         secando = 0;
      }

      if(btOrdenEsconder == LOW && secando ==0){ //si el bt da la orden de sacar al sol y estaba escondido....
        Serial.println("Mostrar");
        digitalWrite(pinbuzzer,LOW); //PIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIP
        digitalWrite(m1Der,LOW);
        digitalWrite(m1Izq,HIGH);
        secando = 1; 
      }
    }
    //poner datos de humedad, temperatura y presion aca

   }

}

/**
 * Devuelve high cuando no hay lu
 */
int nohayLu(){
return  digitalRead(pinLDR);
}

/**
 * Devuelve si pudo leer o no lo que hay en el dht
 */
/*int leerDHT(){
    hum = dht.readHumidity();
    temp = dht.readTemperature();
    
    
  if (isnan(hum) || isnan(temp)) {
    
    Serial.println("Error obteniendo los datos del sensor DHT11");
    return LOW;
  }
  return HIGH; 
}
*/
void cambiarLeds(float t){
  analogWrite(pinRojo,0);
  analogWrite(pinVerde,0);
  analogWrite(pinAzul,255);

  if(t>35){
      analogWrite(pinRojo,255);
    analogWrite(pinVerde,255);
    analogWrite(pinAzul,0);
    return; 
  }
  if(t>25){

     analogWrite(pinRojo,255);
     analogWrite(pinVerde,32);
    return;
  }
  if(t>18){


    analogWrite(pinAzul,255);
    analogWrite(pinVerde,111);
    analogWrite(pinRojo,68);
    
    return;
  }

  if(t>10){

    analogWrite(pinVerde,127);
    analogWrite(pinAzul,255);
    return;
  }
  analogWrite(pinRojo,0);
  analogWrite(pinVerde,0);
  analogWrite(pinAzul,0);
  
}

void secuenciaInicio(){
analogWrite(11,255);
delay(500);
analogWrite(10,255);
delay(500);
analogWrite(9,0);
delay(500);
analogWrite(11,0);
analogWrite(10,0);
analogWrite(9,0);
delay(500);
}

void cambiarLedsBT(){
  analogWrite(pinAzul,255);
  analogWrite(pinVerde,0);
  analogWrite(pinRojo,0);

  if(btLedRojo == HIGH){
    analogWrite(pinRojo,255);
  }
  if(btLedVerde == HIGH){
    analogWrite(pinVerde,255);
  }
  if(btLedAzul == HIGH){
      analogWrite(pinAzul,0);
  }
 
}

void setearFlags(){
  switch(lectura){
    case '0':
    btConectado = HIGH;
    if(secando==0){
      btOrdenEsconder = HIGH;  
    }
    else{
      btOrdenEsconder = LOW;
    }
    break;
    case '1':
    btOrdenEsconder = HIGH;
    break;
    case '2':
    btOrdenEsconder = LOW;
    break;
    case '3':
    btLedRojo = HIGH;
    break;
    case '4':
    btLedVerde = HIGH;
    break;
    case '5':
    btLedAzul = HIGH;
    break;
    case '6':
    btLedRojo = LOW;
    break;
    case '7':
    btLedVerde = LOW;
    break;
    case '8':
    btLedAzul = LOW;
    break;
    case '9':
    btConectado = LOW;
    break;
    default:
    btConectado = LOW;
    break;
  }
}
