long encendido = LOW;
unsigned long previousMillis = 0;  
const long interval = 1000;    
void setup() {
  
   pinMode(9,OUTPUT);
  pinMode(10,OUTPUT);
  pinMode(11,OUTPUT);
}

void loop() {
  unsigned long currentMillis = millis();

  if (currentMillis - previousMillis >= interval) {
    previousMillis = currentMillis;

    if (encendido == LOW) {
      encendido = HIGH;
      analogWrite(9,205);
      analogWrite(10,155);
      analogWrite(11,72);

    } else {
      encendido = LOW;
      analogWrite(9,20);
      analogWrite(10,255);
      analogWrite(11,255);
    }
  }
}
