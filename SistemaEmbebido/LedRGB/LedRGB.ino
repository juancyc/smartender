void setup() {
  // put your setup code here, to run once:
  pinMode(9,OUTPUT);
  pinMode(10,OUTPUT);
  pinMode(11,OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  analogWrite(9,205);
  analogWrite(10,155);
  analogWrite(11,72);
  delay(1000);
  analogWrite(9,20);
  analogWrite(10,255);
  analogWrite(11,255);
  delay(1000);
}
