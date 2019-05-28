int m1Izq = 4;
int m1Der = 5;
int secando = 0; //0 es no hay lu mono
const int pinbuzzer = 11;
unsigned long previousMillis = 0;
const long interval = 1000;     
void setup() {
  Serial.begin(9600);
  pinMode(pinbuzzer,OUTPUT);
  pinMode(12,INPUT);
  pinMode(m1Izq,OUTPUT);
  pinMode(m1Der,OUTPUT);
  digitalWrite(pinbuzzer,HIGH);
  
}

void loop() {

 unsigned long currentMillis = millis();
   if (currentMillis - previousMillis >= interval) {
    previousMillis = currentMillis;
    Serial.println("Entro");
 if(digitalRead(12)==HIGH ){
  Serial.println("No hay lu");

  //no hay lu
  if(secando != 0){
    Serial.println("Escondo");
    digitalWrite(pinbuzzer,LOW);
    moverMotor(0);
  digitalWrite(pinbuzzer,HIGH);
  secando = 0;
  }
 }
 else{
  Serial.println("Hay Lu");
  if(secando == 0){
    Serial.println("Muestro");
    digitalWrite(pinbuzzer,LOW);
    moverMotor(1);
    digitalWrite(pinbuzzer,HIGH);
    
    secando = 1; 
  }
  //hay lu
  
 }
   }
  //delay(1000);
}
/**
 * Cuando le mando un 0 lo escondo, cuando le mando un 1 muestro
 */
void moverMotor(int direccion){
  int millisAnterior = 0;
  int tiempo = 500;
  int der, izq;
  if(direccion == 0){
  der = HIGH;
  izq = LOW;
  }
  else{
    der=LOW;
    izq = HIGH;
  }
//digitalWrite(m1Der,der);
//digitalWrite(m1Izq,izq);
/**
 * delay(tiempo);
 * digitalWrite(m1Der,LOW);
digitalWrite(m1Izq,LOW);
 * 
 */
//unsigned long millisActual = millis();
while(millis()  < tiempo){
    digitalWrite(m1Der,der);
    digitalWrite(m1Izq,izq);
}
    digitalWrite(m1Der,LOW);
    digitalWrite(m1Izq,LOW);
}
