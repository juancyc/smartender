int m1Izq = 4;
int m1Der = 5;
void setup() {
  
  pinMode(m1Izq,OUTPUT);
  pinMode(m1Der,OUTPUT);
  Serial.begin(3600);
}

void loop() {
  digitalWrite(m1Der,HIGH);
  digitalWrite(m1Izq,LOW);
  delay(1000);
  digitalWrite(m1Der,LOW);
  digitalWrite(m1Izq,LOW);
  delay(1000);
  digitalWrite(m1Der,LOW);
  digitalWrite(m1Izq,HIGH);
  delay(1000);
  digitalWrite(m1Der,LOW);
  digitalWrite(m1Izq,LOW);

}
