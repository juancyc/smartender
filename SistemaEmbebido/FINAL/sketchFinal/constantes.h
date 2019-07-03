#include <Wire.h>
#include <SPI.h>
#include <Adafruit_BMP280.h> //Libreria para manejar el BMP
#include <SoftwareSerial.h> //Libreria para manejar el serial del bluetooth
#include <DHT.h>


#define RxBluntu 3 //Receptor bluetooth
#define TxBluntu 2 //Transmisor bluetooth

#define BMP_SCK  (13) //Constantes necesarias para el bmp
#define BMP_MISO (12)
#define BMP_MOSI (11)
#define BMP_CS   (10)
 //Asigna los pines 2 y 3 al serial para el bluetooth. Constructor




#define DHTPIN 8
#define DHTTYPE DHT11

const int intervalo = 500;
const int m1Izq = 4; //pin motor a la izq
const int m1Der = 5; //pin motor a la der

const int pinbuzzer = 7; //pin para el buzzer
const int pinLDR = 12; //pin del sensor de luz
const int pinRojo = 9; //pin manejo luz roja led RGB
const int pinVerde = 10; //pin manejo luz verde led RGB
const int pinAzul = 11; //pin manejo luz azul led RGB
const long interval = 2000; //tiempo de cada intervalo = 2 seg
const int BTMensajeConectar = '0';
const int BTMensajeEsconder = '1';
const int BTMensajeMostrar = '2';
const int BTMensajeRojoOn = '3';
const int BTMensajeVerdeOn = '4';
const int BTMensajeAzulOn = '5';
const int BTMensajeRojoOff = '6';
const int BTMensajeVerdeOff = '7';
const int BTMensajeAzulOff = '8';
const int BTMensajeDesconectar = '9';

const int minTemp = 2;
const int maxHum = 60;
const int minPres = 1004;


  const int rvApagado =0;
  const int aApagado = 255;
  const int rvMediaMaquina = 127;
  const int rvEncendido = 255;
  const int aEncendido =0;
  const int rvUnCuarto = 64;
  const int rvCasiNada = 32;