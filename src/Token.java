import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

public class Token {

    private String personalId;

    private String firstName;

    private String lastName;

    private char gender;

    //format: DD/MM/YYYY
    private String birthDate;

    private String uuid;

    private String secretPhase;

    private String creationTimeStamp;



    public Token(){
    }

    public Token(String personalId, String firstName, String lastName, char gender, String birthDate) {
        this.personalId = personalId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;

        //uuid
        this.uuid = UUID.randomUUID().toString();



        while(true){
            //secret phase
            boolean secretPhaseAlreadyExists = false;
            String secretPhaseIn = SecretPhraseGenerator.generateSecretPhase();
            try {
                BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\User\\3D Objects\\albanianskills2k23\\src\\database.txt"));
                String line;
                while((line = reader.readLine()) != null) {
                    String[] strArr = line.split(";");

                    if(strArr[0].equals("token")){

                        if(strArr[7].equals(secretPhaseIn)){
                            secretPhaseAlreadyExists = true;
                            break;
                        }
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(secretPhaseAlreadyExists){
                break;
            }else{
                this.secretPhase = secretPhaseIn;
                break;
            }
        }




        //timestamp
        Instant instant = Instant.now();
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, java.time.ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSSSSS]");
        this.creationTimeStamp = dateTime.format(formatter);


    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSecretPhase() {
        return secretPhase;
    }

    public void setSecretPhase(String secretPhase) {
        this.secretPhase = secretPhase;
    }

    public String getCreationTimeStamp() {
        return creationTimeStamp;
    }

    public void setCreationTimeStamp(String creationTimeStamp) {
        this.creationTimeStamp = creationTimeStamp;
    }

    public String toString(){
        return "Token" + " " + getUuid() + " created at " + getCreationTimeStamp();
    }



    public boolean isValidDateFormat(String input) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);

        try {
            dateFormat.parse(input);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
