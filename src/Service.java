import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Service {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    //filepath
    private final String filePath;

    private String lastSavedAuthToken = "not";

//    private Scanner sc = new Scanner(System.in);

    public Service(String filePath){
        this.filePath = filePath;
        initialAuthentication();
    }

    public void executeCommand(String[] command) {

        if (command[0].equals("CREATE") && command[1].equals("TOKEN")) {
            createToken(command);

        } else if (command[0].equals("AUTHENTICATE")) {
            authenticateUser(command);
        } else if (command[0].equals("CREATE") && command[1].equals("NFT")) {
            createNft(command);

        } else if (command[0].equals("GET") && command[1].equals("HOLDINGS")) {
            getHoldings(command);

        } else if (command[0].equals("GET") && command[1].equals("TOKEN")) {
            getToken();

        } else if (command[0].equals("TRANSFER") && command[1].equals("NFT")) {
            transferNft(command);

        } else if (command[0].equals("GET") && command[1].equals("TRANSACTIONS")) {
            getTransactions(command);

        } else if (command[0].equals("REMOVE") && command[1].equals("AUTHENTICATION")) {
            removeAuthentication();

        } else if (command[0].equals("EXIT")) {
            exitOut();

        }

        System.out.println("""
                Enter one of the following commands:\s
                 1) (public) CREATE TOKEN {personal_id} {first_name} {last_name} {gender} {birthdate}\s
                 2) (public) AUTHENTICATE {12 words}\s
                 3) (protected) CREATE NFT {type} {value} {description}\s
                 4) (public) GET HOLDINGS {token_uuid}\s
                 5) (protected) GET TOKEN\s
                 6) (protected) TRANSFER NFT {nft_uuid} TO {token_uuid} FOR {value}
                 7) (public) GET TRANSACTIONS {uuid}
                 8) REMOVE AUTHENTICATION\s
                 9) EXIT""");
    }

    private void initialAuthentication() {
        try {
            if (isFileEmpty(filePath)) {
                try (FileWriter writer = new FileWriter(filePath)) {
                    writer.write("authenticated;not");
                }
                System.out.println("File is empty. Authentication status 'not' was written to the file.");
            } else {
                System.out.println("File is not empty. No action taken.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            String[] strArr = line.split(";");
            System.out.println(strArr[0]+ " " + strArr[1]);
            if(!(strArr[1].equals("not"))){
                lastSavedAuthToken = strArr[1];
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exitOut() {
        System.exit(1);
    }

    private void removeAuthentication() {
        replaceAuth(lastSavedAuthToken, "not");
        lastSavedAuthToken="not";
        System.out.println("Authentication removed successfully");
    }

    private void getTransactions(String[] command) {
        String uuidToSearchFor = command[2];

        boolean foundTransactionsForToken = false;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            int ind = 1;
            while((line = reader.readLine()) != null) {
                String[] strArr = line.split(";");
                if(strArr[0].equals("transaction")){
                    if(strArr[1].equals(uuidToSearchFor)){
                        System.out.println("Transaction " + ind + " OUT: " + "\n" +
                                "To: " + strArr[2] + "\n" +
                                "For: " + strArr[6] +
                                " " + strArr[4] +
                                " " +strArr[5] + "\n");
                        ind++;
                        foundTransactionsForToken = true;
                    }else if(strArr[2].equals(uuidToSearchFor)){
                        System.out.println("Transaction " + ind + " IN: " + "\n" +
                                "From: " + strArr[1] + "\n" +
                                "For: " + strArr[6] +
                                " " + strArr[4] +
                                " " +strArr[5] + "\n");
                        ind++;
                        foundTransactionsForToken = true;
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!foundTransactionsForToken){
            System.out.println("No transactions found for token " + uuidToSearchFor);
        }
    }

    private void transferNft(String[] command) {
        boolean nftFound = false;
        if(!(lastSavedAuthToken.equals("not"))){
            nftFound = transferNft(lastSavedAuthToken, command[2], command[4], Double.parseDouble(command[6]));
        }else{
            System.out.println("No user authenticated");
        }

        if(!nftFound){
            System.out.println("Nft not found or the current authenticated user does not have ownership to the specified nft");
        }else{
            System.out.println("Nft transfered successfully");
        }
    }

    private void getToken() {
        if(!(lastSavedAuthToken.equals("not"))){

            try {
                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                String line;
                while((line = reader.readLine()) != null) {
                    String[] strArr = line.split(";");
                    if(strArr[0].equals("token")){
                        if(strArr[6].equals(lastSavedAuthToken)){
                            System.out.println("UUID: " + strArr[6] + "\n" +
                                    "Personal ID: " + strArr[1] + "\n" +
                                    "First name: " + strArr[2] + "\n" +
                                    "Last name: " + strArr[3] + "\n" +
                                    "Gender: " + strArr[4] + "\n" +
                                    "Birthdate: " + strArr[5] + "\n" +
                                    "Created at: " + strArr[8] + "\n");
                        }
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("Not Authenticated");
        }
    }

    private void getHoldings(String[] command) {
        String uuidToSearchFor = command[2];

        boolean foundNftForToken = false;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            int ind = 1;
            while((line = reader.readLine()) != null) {
                String[] strArr = line.split(";");
                if(strArr[0].equals("nft")){
                    if(strArr[6].equals(uuidToSearchFor)){
                        System.out.println("NFT " + ind + ": " + "\n" +
                                "uuid: " + strArr[4] + "\n" +
                                "type: " + strArr[1] + "\n" +
                                "value: " + strArr[2] + "\n" +
                                "description: " +strArr[3] + "\n");
                        ind++;
                        foundNftForToken = true;
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!foundNftForToken){
            System.out.println("No nft found for token " + uuidToSearchFor);
        }
    }

    public String createNft(String[] command) {
        if(lastSavedAuthToken.equals("not")){
            System.out.println("The person is not authenticated");
            return "The Person is not authenticated";
        }else{
            StringBuilder description = new StringBuilder();
            for(int i = 4; i< command.length; i++){
                if(i == command.length-1){
                    description.append(command[i]);
                }else{
                    description.append(command[i]).append(" ");
                }
            }
            Nft nft = new Nft(command[2], Integer.parseInt(command[3]), new String(description));
            if(nft.isValidType()){
                nft.setTokenUuid(lastSavedAuthToken);
//                        tokens.get(lastSavedAuthenticationTokenIndex).addNft(nft);
                //timestamp
                Instant instant = Instant.now();
                LocalDateTime dateTime = LocalDateTime.ofInstant(instant, java.time.ZoneOffset.UTC);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSSSSS]");

                saveNftToFile(nft.getType(),Double.toString(nft.getValue()),nft.getDescription(), nft.getUuid(), nft.getCreationTimeStamp(), nft.getTokenUuid());

                System.out.println("NFT " + nft.getUuid() + " added to token " + lastSavedAuthToken + " at " + dateTime.format(formatter));
                return "CREATE;NFT;" + nft.getType() + ";" + nft.getValue() + ";" + nft.getDescription() + ";" + nft.getUuid() + ";" + nft.getCreationTimeStamp() + ";" + nft.getTokenUuid();
            }else{
                System.out.println("Invalid command");
                return "Invalid Command";
            }
        }
    }

    private void authenticateUser(String[] command) {
        StringBuilder secretPhaseInput = new StringBuilder();


        for(int i = 1; i< command.length; i++){
            if(i == command.length-1){
                secretPhaseInput.append(command[i]);
            }else{
                secretPhaseInput.append(command[i]).append(" ");
            }

        }
        String secretPhaseIn = new String(secretPhaseInput);
        String oldAuthToken = lastSavedAuthToken;
        boolean authSuccess = false;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while((line = reader.readLine()) != null) {
                String[] strArr = line.split(";");

                if(strArr[0].equals("token")){

                    if(strArr[7].equals(secretPhaseIn)){
                        lastSavedAuthToken = strArr[6];
                        authSuccess = true;
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(authSuccess){
            replaceAuth(oldAuthToken, lastSavedAuthToken);
        }

//                int foundIndex = -1;
//                for(int i=0; i<tokens.size(); i++){
//                    if(tokens.get(i).getSecretPhase().equals(secretPhaseIn)){
//                        foundIndex = i;
//                        lastSavedAuthenticationTokenIndex = i;
//                        break;
//                    }
//                }

        if(authSuccess){
            System.out.println("Token " + lastSavedAuthToken + " authenticated successfully");
        }else{
            System.out.println("Authentication failed");
        }
    }

    public String createToken(String[] command) {
        StringBuilder date = new StringBuilder();
        for(int i = 6; i< command.length; i++){
            date.append(command[i]);
        }

        Token temp = new Token(command[2], command[3], command[4], command[5].charAt(0), new String(date));

        if(temp.isValidDateFormat(temp.getBirthDate())){
            saveTokenToFile(temp.getPersonalId(), temp.getFirstName(),  temp.getLastName(), Character.toString(temp.getGender()), temp.getBirthDate(), temp.getUuid(), temp.getSecretPhase(), temp.getCreationTimeStamp());
//            clientOut.println("CREATE TOKEN " + temp.getPersonalId() + " " + temp.getFirstName() + " " + temp.getLastName() + " " + temp.getGender() + " " + temp.getBirthDate() + " " + temp.getUuid() + " " + temp.getSecretPhase() + " " + temp.getCreationTimeStamp());
            System.out.println(temp);
            return "CREATE;TOKEN;" + temp.getPersonalId() + ";" + temp.getFirstName() + ";" + temp.getLastName() + ";" + temp.getGender() + ";" + temp.getBirthDate() + ";" + temp.getUuid() + ";" + temp.getSecretPhase() + ";" + temp.getCreationTimeStamp();
        }else{
            System.out.println("Invalid command");
            return "Invalid Command";
        }
    }


    private void replaceAuth(String oldAuth, String newAuth){
        StringBuilder sb = new StringBuilder();
        File file = new File(filePath);

        try(Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String s = scanner.nextLine();
                if(s.startsWith("auth")){
                    sb.append(s.replace(oldAuth, newAuth) + "\n");
                }else{
                    sb.append(s + "\n");
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        try(PrintWriter pw = new PrintWriter(file)) {
            pw.println(sb);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private boolean transferNft(String currentlyAuthUuid, String nftUuid, String destinationToken, double newValue){
        StringBuilder sb = new StringBuilder();
        File file = new File(filePath);
        boolean nftFound = false;

        try(Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String s = scanner.nextLine();
                if(s.startsWith("nft")){
                    String[] strArr = s.split(";");
                    if(strArr[4].equals(nftUuid) && strArr[6].equals(currentlyAuthUuid)){
                        s = s.replace(strArr[2], Double.toString(newValue));
                        sb.append(s.replace(currentlyAuthUuid, destinationToken) + "\n");
                        try  {
                            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
                            //timestamp
                            Instant instant = Instant.now();
                            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, java.time.ZoneOffset.UTC);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSSSSS]");
                            // The 'true' parameter in the FileWriter constructor indicates append mode
                            writer.write("\ntransaction;" + currentlyAuthUuid + ";" + destinationToken + ";" + nftUuid + ";" + strArr[1] + ";" + strArr[3] + ";" + Double.toString(newValue) + ";" + dateTime.format(formatter));
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        nftFound = true;
                    }else{
                        sb.append(s + "\n");
                    }
                }else{
                    sb.append(s + "\n");
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        try(PrintWriter pw = new PrintWriter(file)) {
            pw.println(sb);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        return nftFound;

    }


    private static boolean isFileEmpty(String filePath)  {
        Path path = Path.of(filePath);
        try {
            return Files.size(path) == 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }





    // Method to start listening for incoming messages
    public void startListening() {
        new Thread(() -> {
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received message from server: " + inputLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Method to start the server socket to accept incoming client connections
    public void startServer() {
        new Thread(() -> {
            try {
//                ServerSocket server = new ServerSocket(port);
//                System.out.println("Server socket for incoming connections running on port " + port);

                while (true) {
                    Socket client = serverSocket.accept();
                    System.out.println("Accepted connection from " + client.getInetAddress());

                    // Initialize input and output streams for the new client
                    PrintWriter clientOut = new PrintWriter(client.getOutputStream(), true);
                    BufferedReader clientIn = new BufferedReader(new InputStreamReader(client.getInputStream()));

                    // Start listening for incoming messages from the new client
                    new Thread(() -> {
                        try {
                            String inputLine;
                            while ((inputLine = clientIn.readLine()) != null) {
                                System.out.println("Received message from client: " + inputLine);

                                if(inputLine.startsWith("CREATE;TOKEN")){
                                    if(inputLine.split(";").length <= 7){
                                        executeCommand(inputLine.replace(";", " ").split(" "));
                                    }else{
                                        String[] strArrr = inputLine.split(";");
                                        saveTokenToFile(strArrr[2], strArrr[3],strArrr[4],strArrr[5],strArrr[6],strArrr[7],strArrr[8],strArrr[9]);
                                    }
                                }else if(inputLine.startsWith("CREATE;NFT")){
                                    if(inputLine.split(";").length <= 4){
                                        executeCommand(inputLine.replace(";", " ").split(" "));
                                    }else{
                                        String[] strArray = inputLine.split(";");
                                        saveNftToFile(strArray[2], strArray[3],strArray[4],strArray[5],strArray[6], strArray[7]);
                                    }
                                }else if(inputLine.startsWith("TRANSFER NFT")){
                                    executeCommand(inputLine.split(" "));
                                }else if(inputLine.startsWith("AUTHENTICATE")){
                                    executeCommand(inputLine.split(" "));
                                }else if(inputLine.startsWith("REMOVE")){
                                    executeCommand(inputLine.split(" "));
                                }
                                // Echo the message back to the client
                                clientOut.println("Echo: " + inputLine);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void saveTokenToFile(String personalId, String firstName, String lastName, String gender, String birthDate, String uuid, String secretPhase, String creationTimeStamp) {

        try  {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
            // The 'true' parameter in the FileWriter constructor indicates append mode
            writer.write("\ntoken;" + personalId + ";" + firstName + ";" + lastName + ";" + gender + ";" + birthDate + ";" + uuid + ";" + secretPhase + ";" + creationTimeStamp);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveNftToFile(String type, String value, String description, String uuid, String creationTimeStamp, String tokenUuid){

        try  {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
            // The 'true' parameter in the FileWriter constructor indicates append mode
            //            writer.write("\nnft;" + nft.getType() + ";" + nft.getValue() + ";" + nft.getDescription() + ";" + nft.getUuid() + ";" + nft.getCreationTimeStamp() + ";" + nft.getTokenUuid());
            writer.write("\nnft;" +type + ";" + value + ";" + description + ";" + uuid + ";" + creationTimeStamp + ";" + tokenUuid);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to send a message
    public void sendMessage(String message) {
        out.println(message);
    }

    public void initializeServer(int serverPort){
        // Start the server on the specified port
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Server running on port " + serverPort);
    }

    public void initializeClient(String remoteHost, int remotePort){
        // Accept client connection
        try{
            clientSocket = new Socket(remoteHost, remotePort);
            System.out.println("Connected to remote server at " + remoteHost + ":" + remotePort);

            // Initialize input and output streams for both server and client
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Service service = new Service("C:\\Users\\User\\3D Objects\\albanianskills2k23\\src\\database.txt");

        System.out.println("""
                Enter one of the following commands:\s
                 1) (public) CREATE TOKEN {personal_id} {first_name} {last_name} {gender} {birthdate}\s
                 2) (public) AUTHENTICATE {12 words}\s
                 3) (protected) CREATE NFT {type} {value} {description}\s
                 4) (public) GET HOLDINGS {token_uuid}\s
                 5) (protected) GET TOKEN\s
                 6) (protected) TRANSFER NFT {nft_uuid} TO {token_uuid} FOR {value}
                 7) (public) GET TRANSACTIONS {uuid}
                 8) REMOVE AUTHENTICATION\s
                 9) EXIT""");
        while (true) {
            String[] command = sc.nextLine().split(" ");
            service.executeCommand(command);
        }


    }


}
