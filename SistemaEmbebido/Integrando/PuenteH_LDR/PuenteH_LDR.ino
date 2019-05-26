int m1Izq = 4;
int m1Der = 5;
int sentido = 0; //0 es no hay lu mono
void setup() {
  Serial.begin(9600);
  pinMode(12,INPUT);
  pinMode(m1Izq,OUTPUT);
  pinMode(m1Der,OUTPUT);

  
}

void loop() {

 if(digitalRead(12)==HIGH ){
Serial.println("Mira como me muevo");
  //no hay lu
  if(sentido != 0){
    
  digitalWrite(m1Der,LOW);
  digitalWrite(m1Izq,HIGH);
  delay(500);
    digitalWrite(m1Der,LOW);
  digitalWrite(m1Izq,LOW);
  sentido = 0;
  }
 }
 else{
  if(sentido == 0){
    Serial.println("Ahora para el otro lado");
    digitalWrite(m1Der,HIGH);
    digitalWrite(m1Izq,LOW);
      delay(500);
    digitalWrite(m1Der,LOW);
  digitalWrite(m1Izq,LOW);
    
    sentido =1; 
  }
  //hay lu
  
 }

  delay(1000);
}
