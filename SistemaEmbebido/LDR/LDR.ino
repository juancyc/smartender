void setup() {
  pinMode(2,INPUT);
  Serial.begin(9600);
}

void loop() {

 if(digitalRead(2)==HIGH){
  Serial.println("No hay Lu Mono");
 }
 else{
  Serial.println("Hay Lu MOno");
 }

  delay(1000);
}
