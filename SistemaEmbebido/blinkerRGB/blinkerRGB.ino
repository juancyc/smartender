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
  analogWrite(9,255);
  delay(100);
  analogWrite(9,255);
}
