#include <Wire.h>
#include <SPI.h>
#include <Adafruit_BMP280.h>

#define BMP_SCK  (13)
#define BMP_MISO (12)
#define BMP_MOSI (11)
#define BMP_CS   (10)
Adafruit_BMP280 bmp;
/*#include <DHT.h>

#define DHTPIN 8
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);
*/
int m1Izq = 4;
int m1Der = 5;
int secando = 0; //0 es no hay lu mono
const int pinbuzzer = 7;
const int pinLDR = 12;
const int pinRojo = 9;
const int pinVerde = 10;
const int pinAzul =11;
unsigned long previousMillis = 0;
const long interval = 5000;

float hum,temp,pres,tempBmp;    
int btOrdenEsconder = LOW;
int btOrdenLeds =LOW;
void setup() {
  Serial.begin(9600);
  pinMode(pinbuzzer,OUTPUT);
  pinMode(pinLDR,INPUT);
  pinMode(m1Izq,OUTPUT);
  pinMode(m1Der,OUTPUT);
  digitalWrite(pinbuzzer,HIGH);
  bmp.begin();
    /* Default settings from datasheet. */
  bmp.setSampling(Adafruit_BMP280::MODE_NORMAL,     /* Operating Mode. */
                  Adafruit_BMP280::SAMPLING_X2,     /* Temp. oversampling */
                  Adafruit_BMP280::SAMPLING_X16,    /* Pressure oversampling */
                  Adafruit_BMP280::FILTER_X16,      /* Filtering. */
                  Adafruit_BMP280::STANDBY_MS_500); /* Standby time. */
  secuenciaInicio();  
}

void loop() {
  

 unsigned long currentMillis = millis();
   if (currentMillis - previousMillis >= interval) {
    digitalWrite(m1Der,LOW);
    digitalWrite(m1Izq,LOW);
    digitalWrite(pinbuzzer,HIGH);
    previousMillis = currentMillis;
    hum = 35;
    //int dhtDisp = leerDHT();
    //hum = dht.readHumidity();
    //temp = dht.readTemperature();
    if(Serial.available()>0){
      tempBmp = Serial.parseFloat();
    }
    else{
    tempBmp = bmp.readTemperature();
    }
    pres = bmp.readPressure()/100;
    Serial.print("Humedad: ");
    Serial.println(hum);
    Serial.print("Temperatura: ");
    Serial.println(tempBmp);
    Serial.print("Presion: ");
    Serial.println(pres);
    
    if(btOrdenLeds==LOW){
    cambiarLeds(tempBmp);
    }
    else{
      //leeriamos antes los valores del android
      cambiarLedsBT();
    }
    //poner datos de humedad, temperatura y presion aca
    if(nohayLu()==HIGH||(hum>60)||tempBmp<10||pres<1007||btOrdenEsconder==HIGH){
          Serial.println("No hay lu");
          //no hay lu
          if(secando != 0){
              Serial.println("Escondo");
              digitalWrite(pinbuzzer,LOW);
              //moverMotorBloq(0);
              digitalWrite(m1Der,HIGH);
              digitalWrite(m1Izq,LOW);
              secando = 0;
            }
  }
 else{
      Serial.println("Hay Lu");
      if(secando == 0){
          Serial.println("Muestro");
          digitalWrite(pinbuzzer,LOW);
          //moverMotorBloq(1);
        digitalWrite(m1Der,LOW);
        digitalWrite(m1Izq,HIGH);
          
          secando = 1; 
      }
  //hay lu
  }
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
  analogWrite(pinAzul,0);

  if(t>28){
    analogWrite(pinRojo,255);
    return;
  }
  if(t>25){
    analogWrite(pinRojo,127);
    analogWrite(pinVerde,255);
    return;
  }
  if(t>18){
    analogWrite(pinVerde,127);
    analogWrite(pinAzul,255);
    return;
  }

  if(t>10){
    analogWrite(pinAzul,255);
    return;
  }
/*  digitalWrite(pinRojo,255);
  digitalWrite(pinVerde,);
  digitalWrite(pinAzul,255);
  */
}

void secuenciaInicio(){
digitalWrite(11,255);
delay(500);
digitalWrite(10,255);
delay(500);
digitalWrite(9,0);
delay(500);
digitalWrite(11,0);
digitalWrite(10,0);
digitalWrite(9,0);
delay(500);
}

void cambiarLedsBT(){
  Serial.println("Todavia no hago nada bro");
}
