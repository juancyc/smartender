  long previousMillis = 0;
  long currentMillis =0;
  const long interval =500;
  int ledState = LOW;
void setup() {
  pinMode(LED_BUILTIN, OUTPUT);

}

void loop() {
  currentMillis = millis();
  if(currentMillis - previousMillis >=interval){
    previousMillis = currentMillis;
    if(ledState ==LOW){
      ledState =HIGH;
    }
    else{
      ledState = LOW;
    }
    digitalWrite(LED_BUILTIN,ledState);
  }
}
