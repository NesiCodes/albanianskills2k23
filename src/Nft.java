import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Nft {

    private String type;

    private double value;

    private String description;

    private String uuid;

    private String creationTimeStamp;

    private String tokenUuid;



    public Nft(){

    }

    public Nft(String type, double value, String description) {
        this.type = type;
        this.value = value;
        this.description = description;

        //uuid
        this.uuid = UUID.randomUUID().toString();

        //timestamp
        Instant instant = Instant.now();
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, java.time.ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSSSSS]");
        this.creationTimeStamp = dateTime.format(formatter);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getTokenUuid() {
        return tokenUuid;
    }

    public void setTokenUuid(String tokenUuid) {
        this.tokenUuid = tokenUuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCreationTimeStamp() {
        return creationTimeStamp;
    }

    public void setCreationTimeStamp(String creationTimeStamp) {
        this.creationTimeStamp = creationTimeStamp;
    }

    @Override
    public String toString() {

        return "uuid: " + getUuid() + "\n" +
                "type: " + getType() + "\n" +
                "value: " + getValue() + "\n" +
                "description: " +getDescription() + "\n";
    }

    public boolean isValidType(){
        if(this.type.equals("real_estate") || this.type.equals("vehicle") || this.type.equals("artwork") || this.type.equals("intellectual_property")){
            return true;
        }
        return false;
    }

}
