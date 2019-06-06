#include <Wire.h>
#include <SPI.h>
#include <Adafruit_BMP280.h>

#define BMP_SCK  (13)
#define BMP_MISO (12)
#define BMP_MOSI (11)
#define BMP_CS   (10)
Adafruit_BMP280 bmp;
#include <DHT.h>

#define DHTPIN 8
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

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
    //int dhtDisp = leerDHT();
    hum = dht.readHumidity();
    temp = dht.readTemperature();
    //tempBmp = bmp.readTemperature();
    pres = bmp.readPressure()/100;
    Serial.print("Humedad: ");
    Serial.println(hum);
    Serial.print("Temperatura: ");
    Serial.println(temp);

    
    cambiarLeds(temp);
    //poner datos de humedad, temperatura y presion aca
    if(nohayLu()==HIGH){
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
int leerDHT(){
    hum = dht.readHumidity();
    temp = dht.readTemperature();
    
    
  if (isnan(hum) || isnan(temp)) {
    
    Serial.println("Error obteniendo los datos del sensor DHT11");
    return LOW;
  }
  return HIGH; 
}

void cambiarLeds(int t){
  
  if(t>28){
    digitalWrite(pinRojo,255);
    return;
  }
  if(t>25){
    digitalWrite(pinRojo,127);
    digitalWrite(pinVerde,255);
    return;
  }
  if(t>18){
    digitalWrite(pinVerde,127);
    digitalWrite(pinAzul,255);
    return;
  }

  if(t>10){
    digitalWrite(pinAzul,255);
    return;
  }
  digitalWrite(pinRojo,255);
  digitalWrite(pinVerde,255);
  digitalWrite(pinAzul,255);
  
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
