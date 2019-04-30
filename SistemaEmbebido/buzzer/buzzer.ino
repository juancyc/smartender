const int pinBuzzer = 9;

void setup() 
{
  pinMode(pinBuzzer,OUTPUT);
}

void loop() 
{
  //generar tono de 440Hz durante 1000 ms
  /*tone(pinBuzzer, 550);
  delay(1000);
  */
  //detener tono durante 500ms  
  //noTone(pinBuzzer);
  //delay(500);

  //generar tono de 523Hz durante 500ms, y detenerlo durante 500ms.
  digitalWrite(pinBuzzer,HIGH);
  delay(500);
  digitalWrite(pinBuzzer,LOW);
}
