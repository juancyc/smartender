  long previousMillis = 0;
  long currentMillis =0;
  long interval;
  int ledState = LOW;
  int flag = 0;
void setup()
{
}

void loop()
{
  interval=analogRead(A0);
  Serial.println(interval);
  currentMillis = millis();
  if(currentMillis - previousMillis >=interval){
    previousMillis = millis();
    if(flag == 0){
   analogWrite(11,145);
     analogWrite(10,112);
     analogWrite(9,0);
     flag = 1;
    }
    else{
   analogWrite(11,0);
     analogWrite(10,0);
     analogWrite(9,0);
     flag = 0;
    }
    
  }
}