import java.util.Arrays;
import java.util.Scanner;

public class Main {

    private static Scanner sc = new Scanner(System.in);


    public static void main(String[] args){

        Service serviceInstance1 = new Service("C:\\Users\\User\\3D Objects\\albanianskills2k23\\src\\database.txt");
        serviceInstance1.initializeServer(9080);
        serviceInstance1.startServer();

        Service serviceInstance2 = new Service("C:\\Users\\User\\3D Objects\\albanianskills2k23\\src\\database2.txt");
        serviceInstance2.initializeServer(9081);
        serviceInstance2.startServer();

        serviceInstance1.initializeClient("localhost",9081);
        serviceInstance2.initializeClient("localhost", 9080);

        serviceInstance1.startListening();
        serviceInstance2.startListening();


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
        while (true){

            System.out.print("Do you want to use service1 or service2 (Enter 1 or 2): ");
            int choice = sc.nextInt();
            sc.nextLine();
            if(choice == 1){
                useServerInstance(serviceInstance1);
            }else if(choice == 2){
                useServerInstance(serviceInstance2);
            }

            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

    }

    private static void useServerInstance(Service serviceInstance) {
        String[] command = sc.nextLine().split(" ");
        if(command[0].equals("CREATE") && command[1].equals("TOKEN")){
            String newCommand = serviceInstance.createToken(command);
            serviceInstance.sendMessage(newCommand);
        }else if(command[0].equals("CREATE") && command[1].equals("NFT")){
            String newCommForNft = serviceInstance.createNft(command);
            serviceInstance.sendMessage(newCommForNft);
        }else if(command[0].equals("TRANSFER") && command[1].equals("NFT")){
            serviceInstance.executeCommand(command);
            serviceInstance.sendMessage(String.join(" ", command));
        }else if(command[0].equals("AUTHENTICATE")){
            serviceInstance.executeCommand(command);
            serviceInstance.sendMessage(String.join(" ", command));
        }else if(command[0].equals("REMOVE") && command[1].equals("AUTHENTICATION")){
            serviceInstance.executeCommand(command);
            serviceInstance.sendMessage(String.join(" ", command));
        }
        else{
            serviceInstance.executeCommand(command);
        }
    }


}