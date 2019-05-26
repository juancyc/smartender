const int pinBuzzer = 11;

void setup() 
{
  pinMode(pinBuzzer,OUTPUT);
}
  
void loop() 
{
 digitalWrite(pinBuzzer,HIGH);

 delay(1000);

 digitalWrite(pinBuzzer,LOW);

 delay(1000);
  /*tone(pinBuzzer, 39);
  delay(1000);
  tone(pinBuzzer,0);
  delay(2000);
*/
}
