  long previousMillis = 0;
  long currentMillis =0;
  const long interval =1000;
  int ledState = LOW;
  int flag = 0;
void setup()
{
  Serial.begin(9600);
  pinMode(11, OUTPUT);
  pinMode(10, OUTPUT);
  pinMode(9, OUTPUT);
}

void loop()
{
  currentMillis = millis();
  if(currentMillis - previousMillis >=interval){
    previousMillis = millis();
    if(flag == 0){
	 analogWrite(11,145);
  	 analogWrite(10,112);
     analogWrite(9,0);
     flag = 1;
    Serial.println("Hola no estoy aca");
    }
    else{
      if(flag ==1){
        	 analogWrite(11,0);
  	 analogWrite(10,0);
     analogWrite(9,0);
        flag =2;
      }
      else{
    Serial.println("Hola estoy aca");
	analogWrite(11,0);
  	analogWrite(10,0);
  	analogWrite(9,255);  
    flag=0;
      }
    }
    
  }
}