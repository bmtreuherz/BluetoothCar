 /* ------Controlls------
 * Inputs take the following form: "command:speed"
 * 
 * 1 -Forward
 * 2 -Stop
 * 3 -Reverse
 * 
 * 4 -Left
 * 5 -Stop turn
 * 6 -Right
 * 
 * 7 -Lights on
 * 8 -Lights off
 */
 

 // ------Declarations------

 // Motor 1: Power
 const int forwardPinA = 3;
 const int reversePinA = 2;
 const int speedPinA = 9;

 // Motor 2: Direction
 const int leftPinB = 5;
 const int rightPinB = 4;
 const int speedPinB = 10;

 // Leds
 const int headlights = 6;
 const int taillights = 7;

 // Input
 String input;
 char command;
 int magnitude; 

 
 

 // ------Helper functions------

 // Returns a substring of the data split by a delimiter at a specified index.
 String getValue(String data, char delimiter, int index)
{
  int found = 0;
  int strIndex[] = {0, -1 };
  int maxIndex = data.length()-1;
  for(int i=0; i<=maxIndex && found<=index; i++){
    if(data.charAt(i)==delimiter || i==maxIndex){
      found++;
      strIndex[0] = strIndex[1]+1;
      strIndex[1] = (i == maxIndex) ? i+1 : i;
    }
 }
  return found>index ? data.substring(strIndex[0], strIndex[1]) : "";
}


// ------setup-----
void setup() {
  // intizialize serial communication
  Serial.begin(9600);

  // Define output pins
  pinMode(forwardPinA, OUTPUT);
  pinMode(reversePinA, OUTPUT);
  pinMode(speedPinA, OUTPUT);
  pinMode(leftPinB, OUTPUT);
  pinMode(rightPinB, OUTPUT);
  pinMode(speedPinB, OUTPUT);
  pinMode(headlights, OUTPUT);
  pinMode(taillights, OUTPUT);

}

// ---main loop---
void loop() {

  // While there is information to be read
  while(Serial.available()){
    input += char(Serial.read());
  }

  // If there is no new information coming in
  if(!Serial.available()){
    if(input != ""){
      // get the necessary information from the input string
      command = getValue(input, ':', 0).charAt(0);
      magnitude = getValue(input, ':', 1).toInt();

      // clear the input string
      input = "";

      // print the values for debugging purposes
      Serial.print("command: ");
      Serial.println(command);
      Serial.print("intensity: ");
      Serial.println(magnitude); 
    }
  }

  // Switch for commands
  switch(command){
    
    // ------Power------
    
    case '1': // Move forward
      analogWrite(speedPinA, magnitude);
      digitalWrite(forwardPinA, HIGH);
      digitalWrite(reversePinA, LOW);

      // print for debugging
      Serial.println("Moving forward \n");
      Serial.println(magnitude);
      break;

    case '2': // Stop motor 
      analogWrite(speedPinA, 0);
      digitalWrite(forwardPinA, HIGH);
      digitalWrite(reversePinA, LOW);

      // print for debugging
      Serial.println("Stopping \n");
      break;

    case '3': // Reverse
      analogWrite(speedPinA, magnitude);
      digitalWrite(forwardPinA, LOW);
      digitalWrite(reversePinA, HIGH);

      // print for debugging
      Serial.println("Moving in reverse \n");
      break;

    // ------Direction------
    case '4': //Turn Left
      analogWrite(speedPinB, magnitude);
      digitalWrite(leftPinB, HIGH);
      digitalWrite(rightPinB, LOW);

      // print for debugging
      Serial.println("Turning Left \n");
      Serial.println(magnitude);
      break;

    case '5': // Stop Turn
      analogWrite(speedPinB, 0);
      digitalWrite(leftPinB, HIGH);
      digitalWrite(rightPinB, LOW);

      // print for debugging
      Serial.println("Stopping turn \n");
      break;

    case '6': //Go Right
      analogWrite(speedPinB, magnitude);
      digitalWrite(leftPinB, LOW);
      digitalWrite(rightPinB, HIGH);

      // print for debugging
      Serial.println("Turning Right \n");
      Serial.println(magnitude);
      break;

    case '7': //Lights on
      digitalWrite(headlights, HIGH);
      digitalWrite(taillights, HIGH);
      break;

    case '8': // Lights off
      digitalWrite(headlights, LOW);
      digitalWrite(taillights, LOW);
      break;

    default: // Turn of all power
      for(int currentPin =2; currentPin < 11; currentPin++){
        digitalWrite(currentPin, LOW);
      }
      Serial.println("all power off\n");
  }

}
