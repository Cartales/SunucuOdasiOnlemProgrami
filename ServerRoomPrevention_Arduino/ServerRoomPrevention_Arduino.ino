#include<LiquidCrystal.h>
LiquidCrystal lcd(8, 9, 4, 5, 6, 7);

int smokeSensorDigital = 37;
int smokeSensorAnalog = A8;
int lm35Sensor = A13;
int systemState = 36;
int serverState = 35;
int smokeAlert = 34;
int temperatureAlert = 33;
int sicakKlima = 32;
int sogukKlima = 31;
int yanginSondurme = 30;

String systemStateControl = "";
String systemActive = "";

int firstRun = 0;
int sayac = 0;

String sicaklikDurum = "KAPALI";
String dumanDurum = "KAPALI";


void setup() {
  Serial.begin(9600);

  pinMode(smokeSensorDigital, INPUT);
  pinMode(smokeSensorAnalog, INPUT);

  pinMode(systemState, OUTPUT);
  pinMode(serverState, OUTPUT);
  pinMode(smokeAlert, OUTPUT);
  pinMode(temperatureAlert, OUTPUT);
  pinMode(sicakKlima, OUTPUT);
  pinMode(sogukKlima, OUTPUT);
  pinMode(yanginSondurme, OUTPUT);

  lcd.begin(16, 2);
  lcd.setCursor(0,0);
  lcd.print("Temp=");
  lcd.setCursor(0,1);
  lcd.print("Duman=");
}

void loop() {
  systemStateControl = Serial.readString();

  if(systemStateControl == "1"){
    systemActive = "1";
  }
  else if(systemStateControl == "2"){
    systemActive = "2";
  }

  if(systemActive == "1"){
    if(firstRun == 0){
      for(sayac = 0; sayac < 25; sayac++){
        
        int smokeValue = analogRead(smokeSensorAnalog);
        
        float value = analogRead(lm35Sensor);
        float mV = (value/1024.0)*5000;
        float celcius = mV/10;

        sicaklikDurum = "WAIT";
        dumanDurum = "WAIT";
        
        lcdSil();
        lcdYaz(sicaklikDurum, dumanDurum);

        digitalWrite(systemState, HIGH);
        delay(100);
        digitalWrite(systemState, LOW);
        delay(100);
         
         firstRun++;
      }
    }
    else{
        digitalWrite(systemState, HIGH);
        int smokeValue = analogRead(smokeSensorAnalog);
        
        float value = analogRead(lm35Sensor);
        float mV = (value/1023.0)*5000;
        float celcius = (mV/10) - 9.31;

        Serial.print(smokeValue);
        Serial.print("#");
        Serial.println(celcius);
        

      
        if(smokeValue < 200){
          dumanDurum = "OPTIMUM";
          lcdSil();
          lcdYaz(sicaklikDurum, dumanDurum);
          digitalWrite(smokeAlert, LOW);
          digitalWrite(yanginSondurme, LOW);
        }
        else if(smokeValue >= 200 && smokeValue <= 280){
          dumanDurum = "KOTU";
          lcdSil();
          lcdYaz(sicaklikDurum, dumanDurum);
          digitalWrite(smokeAlert, HIGH);
          digitalWrite(yanginSondurme, LOW);
        }
        else if(smokeValue > 280){
          dumanDurum = "UYGUNSUZ";
          lcdSil();
          lcdYaz(sicaklikDurum, dumanDurum);

          digitalWrite(smokeAlert, HIGH);
          digitalWrite(yanginSondurme, HIGH);
        }
        else {
          dumanDurum = "####";
          lcdSil();
          lcdYaz(sicaklikDurum, dumanDurum);
          digitalWrite(serverState, LOW); 
        }

        if(celcius <= 10){
          sicaklikDurum = "UYGUNSUZ";
          lcdSil();
          lcdYaz(sicaklikDurum, dumanDurum);

          digitalWrite(sogukKlima, LOW);
          digitalWrite(temperatureAlert, HIGH);
          digitalWrite(sicakKlima, HIGH);
        }
        else if((celcius > 10 && celcius <= 12)){
          sicaklikDurum = "KOTU";
          lcdSil();
          lcdYaz(sicaklikDurum, dumanDurum);

          digitalWrite(sogukKlima, LOW);
          digitalWrite(temperatureAlert, HIGH);
          digitalWrite(sicakKlima, HIGH);
        }
        else if(celcius > 12 && celcius <= 15){
          sicaklikDurum = "OPTIMUM";
          lcdSil();
          lcdYaz(sicaklikDurum, dumanDurum);
          
          digitalWrite(sogukKlima, LOW);
          digitalWrite(sicakKlima, HIGH);
          digitalWrite(temperatureAlert, LOW);
        }
        else if(celcius > 15 && celcius < 30){
          sicaklikDurum = "OPTIMUM";
          lcdSil();
          lcdYaz(sicaklikDurum, dumanDurum);

          digitalWrite(sicakKlima, LOW);
          digitalWrite(sogukKlima, HIGH);
          digitalWrite(temperatureAlert, LOW);
        }
        else if(celcius > 30 && celcius < 35){
          sicaklikDurum = "KOTU";
          lcdSil();
          lcdYaz(sicaklikDurum, dumanDurum);

          digitalWrite(sicakKlima, LOW);
          digitalWrite(temperatureAlert, HIGH);
          digitalWrite(sogukKlima, HIGH);
        }
        else if(celcius >= 35){
          sicaklikDurum = "UYGUNSUZ";
          lcdSil();
          lcdYaz(sicaklikDurum, dumanDurum);
          
          digitalWrite(sicakKlima, LOW);
          digitalWrite(temperatureAlert, HIGH);
          digitalWrite(sogukKlima, HIGH);
          digitalWrite(serverState, LOW);
        }
        else{
          sicaklikDurum = "####";
          lcdSil();
          lcdYaz(sicaklikDurum, dumanDurum);
          digitalWrite(serverState, LOW); 
        }

        if((sicaklikDurum == "OPTIMUM" || sicaklikDurum == "KOTU") && (dumanDurum == "OPTIMUM" || dumanDurum == "KOTU")){
          digitalWrite(serverState, HIGH);
        }
        else if(sicaklikDurum == "UYGUNSUZ" || dumanDurum == "UYGUNSUZ"){
          digitalWrite(serverState, LOW);
        }    
    }
  }
  else{
    kapali();
  }

  delay(1000);
  

}

void kapali(){
    digitalWrite(yanginSondurme, LOW);
    digitalWrite(systemState, LOW);
    digitalWrite(smokeAlert, LOW);
    digitalWrite(sicakKlima, LOW);
    digitalWrite(temperatureAlert, LOW);
    digitalWrite(sogukKlima, LOW);
    digitalWrite(serverState, LOW); 
    sicaklikDurum = "KAPALI";
    dumanDurum = "KAPALI";
    lcdSil();
    lcdYaz(sicaklikDurum, dumanDurum);
}

void lcdYaz(String s, String d){
  lcd.setCursor(5,0);
  lcd.print(s);
  lcd.setCursor(6,1);
  lcd.print(d);
}
void lcdSil(){
  lcd.setCursor(5,0);
  lcd.print("           ");
  lcd.setCursor(6,1);
  lcd.print("          ");
}

