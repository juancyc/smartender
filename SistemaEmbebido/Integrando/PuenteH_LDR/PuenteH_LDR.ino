int m1Izq = 4;
int m1Der = 5;
int sentido = 0; //0 es no hay lu mono
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
    // save the last time you blinked the LED
    previousMillis = currentMillis;
    Serial.println("Entro");
 if(digitalRead(12)==HIGH ){
  Serial.println("No hay lu");

  //no hay lu
  if(sentido != 0){
    Serial.println("Escondo");
  digitalWrite(m1Der,LOW);
  digitalWrite(m1Izq,HIGH);
  digitalWrite(pinbuzzer,LOW);
  delay(500);
  digitalWrite(pinbuzzer,HIGH);
    digitalWrite(m1Der,LOW);
  digitalWrite(m1Izq,LOW);
  sentido = 0;
  }
 }
 else{
  Serial.println("Hay Lu");
  if(sentido == 0){
    Serial.println("Muestro");
    digitalWrite(m1Der,HIGH);
    digitalWrite(m1Izq,LOW);
    digitalWrite(pinbuzzer,LOW);
      delay(500);
      digitalWrite(pinbuzzer,HIGH);
    digitalWrite(m1Der,LOW);
  digitalWrite(m1Izq,LOW);
    
    sentido =1; 
  }
  //hay lu
  
 }
   }
  delay(1000);
}
