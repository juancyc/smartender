#include "constantes.h"

float hum, temp, pres, tempBmp;
int lectura; 
int btConectado = LOW;
int btLedVerde = LOW;
int btLedRojo = LOW;
int btLedAzul = LOW;
int btOrdenEsconder = LOW; 
int secando = LOW; 
unsigned long previousMillis = 0; //milisegundos de comienzo

Adafruit_BMP280 bmp;
SoftwareSerial BT(RxBluntu,TxBluntu);
DHT dht(DHTPIN, DHTTYPE);


void setup() {
  Serial.begin(9600); //arranca el serial con 9600 baudios de sincronismo
  BT.begin(9600); //Idem, bt funciona con serial
  pinMode(pinbuzzer, OUTPUT); //setea el buzzer como sensor de salida
  pinMode(pinLDR, INPUT); //setea el LDR como sensor de entrada
  pinMode(m1Izq, OUTPUT); //setea el control del manejo del motor hacia la izq como salida
  pinMode(m1Der, OUTPUT); //idem anterior con derecha
  digitalWrite(pinbuzzer, HIGH); //el HIGH del buzzer en realidad es estar apagado, LOW es cuando suena. Logica inversa.
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
    Serial.println(secando);
    digitalWrite(m1Der, LOW); //detiene el movimiento del motor a la derecha
    digitalWrite(m1Izq, LOW); //detiene el movimiento del motor a la izquierda
    digitalWrite(pinbuzzer, HIGH); //no hace ruido el buzzer
    previousMillis = currentMillis; //asigna como miliseg previos los actuales
    hum = dht.readHumidity();
    //temp = dht.readTemperature();
    if (BT.available()) { //si puede leer info del bluetooth arranca por ahi
      lectura = BT.read(); //obtiene la data mandada del bt y la guarda en la variable lectura
      setearFlags(); //setea los flags segun lectura
      Serial.print("Lectura del telefono: "); //manda texto al igual que println
      Serial.write(lectura); //manda bits en vez de texto
      Serial.println(" ");
    }
    if (Serial.available() > 0) { //Si no consigue obtener info del bt, se fija si le manda algo desde el serial/computadora
      temp = Serial.parseFloat(); //
    }
    else {
      temp = dht.readTemperature();//si no lee del bt ni del serial, saca la data del sensor de temperatura
    }
    pres = bmp.readPressure() / 100; //divide por 100 porque esta en pascales
    Serial.print("Humedad: ");
    Serial.println(hum);
    Serial.print("Temperatura: ");
    Serial.println(tempBmp);
    Serial.print("Presion: ");
    Serial.println(pres);

    BT.print(String(temp) + "-" + String(hum) + "-" + String(pres)); //le pasa la temperatura al bt
    BT.println(); //le manda el /n que necesita el android para poder comenzar a procesar la info enviada


    if (btConectado == LOW) { //si no esta el bt conectado, modo arduino
      cambiarLeds(tempBmp); //cambia los leds segun la temperatura obtenida del sensor bmp
      if (nohayLuz() == HIGH || (hum > maxHum) || temp < minTemp || pres < maxHum || btOrdenEsconder == HIGH) { //si se cumple alguna de las condiciones como para esconder el tender...
        Serial.println("Condiciones no optimas   ");
        //no hay lu
        if (secando != LOW) { //se fija si el tender estaba al sol
          Serial.println("Escondo"); //si estaba, lo esconde por las malas condiciones de clima
          digitalWrite(pinbuzzer, LOW); //PIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIP
          digitalWrite(m1Der, HIGH); //el motor se mueve a la derecha
          digitalWrite(m1Izq, LOW); // no se activa moverlo a la izq
          secando = LOW; //avisa que la ropa esta bajo techo
        }
      }
      else { 
        Serial.println("Condiciones optimas");
        if (secando == LOW) { 
          Serial.println("Muestro");
          digitalWrite(pinbuzzer, LOW);
          digitalWrite(m1Der, LOW);
          digitalWrite(m1Izq, HIGH);

          secando = HIGH; //la ropa esta al sol
        }
        //hay lu
      }
    }
    else {
      //leeriamos antes los valores del android
      Serial.println("Bluntu mode");
      cambiarLedsBT(); //cambia los leds segun la temperatura dada por el bt
      if (btOrdenEsconder == HIGH && secando != 0) { //si el bt da la orden de poner la ropa bajo techo y estaba previamente al sol...
        Serial.println("Esconder");
        digitalWrite(pinbuzzer, LOW); //PIIIIIIIIIIIIIIIIIIIIP
        digitalWrite(m1Der, HIGH);
        digitalWrite(m1Izq, LOW);
        secando = LOW;
      }

      if (btOrdenEsconder == LOW && secando == 0) { //si el bt da la orden de sacar al sol y estaba escondido....
        Serial.println("Mostrar");
        digitalWrite(pinbuzzer, LOW); //PIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIP
        digitalWrite(m1Der, LOW);
        digitalWrite(m1Izq, HIGH);
        secando = HIGH;
      }
    }

  }

}

/**
   Devuelve high cuando no hay lu
*/
int nohayLuz() {
  return  digitalRead(pinLDR);
}



void cambiarLeds(float t) {

  
  analogWrite(pinRojo, rvApagado);
  analogWrite(pinVerde, rvApagado);
  analogWrite(pinAzul, aApagado);

  if (t > 35) {
    analogWrite(pinRojo, rvEncendido);
    analogWrite(pinVerde, rvEncendido);
    analogWrite(pinAzul, aEncendido);
    return;
  }
  if (t > 25) {

    analogWrite(pinRojo, rvEncendido);
    analogWrite(pinVerde, rvCasiNada);
    return;
  }
  if (t > 18) {


    analogWrite(pinAzul, aApagado);
    analogWrite(pinVerde, rvMediaMaquina);
    analogWrite(pinRojo, rvUnCuarto);

    return;
  }

  if (t > 10) {

    analogWrite(pinVerde, rvMediaMaquina);
    analogWrite(pinAzul, aApagado);
    return;
  }
  analogWrite(pinRojo, rvApagado);
  analogWrite(pinVerde, rvApagado);
  analogWrite(pinAzul, aEncendido);

}

void secuenciaInicio() {
  
  analogWrite(pinAzul, aEncendido);
  delay(intervalo);
  analogWrite(pinVerde,rvEncendido);
  delay(intervalo);
  analogWrite(pinRojo,rvEncendido);
  delay(intervalo);
  analogWrite(pinAzul, aApagado);
  analogWrite(pinVerde, rvApagado);
  analogWrite(pinRojo, rvApagado);
  delay(intervalo);
}

void cambiarLedsBT() {
  analogWrite(pinAzul, aApagado);
  analogWrite(pinVerde, rvApagado);
  analogWrite(pinRojo, rvApagado);

  if (btLedRojo == HIGH) {
    analogWrite(pinRojo, rvEncendido);
  }
  if (btLedVerde == HIGH) {
    analogWrite(pinVerde, rvEncendido);
  }
  if (btLedAzul == HIGH) {
    analogWrite(pinAzul, aEncendido);
  }

}

void setearFlags() {
  switch (lectura) {
    case BTMensajeConectar:
      btConectado = HIGH;
      if (secando == LOW) {
        btOrdenEsconder = HIGH;
      }
      else {
        btOrdenEsconder = LOW;
      }
      break;
    case BTMensajeEsconder:
      btOrdenEsconder = HIGH;
      break;
    case BTMensajeMostrar:
      btOrdenEsconder = LOW;
      break;
    case BTMensajeRojoOn:
      btLedRojo = HIGH;
      break;
    case BTMensajeVerdeOn:
      btLedVerde = HIGH;
      break;
    case BTMensajeAzulOn:
      btLedAzul = HIGH;
      break;
    case BTMensajeRojoOff:
      btLedRojo = LOW;
      break;
    case BTMensajeVerdeOff:
      btLedVerde = LOW;
      break;
    case BTMensajeAzulOff:
      btLedAzul = LOW;
      break;
    case BTMensajeDesconectar:
      btConectado = LOW;
      secando = LOW;
      break;
    default:
      btConectado = LOW;
      secando = LOW;
      break;
  }
}
