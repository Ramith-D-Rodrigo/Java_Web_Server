import java.net.*;
import java.io.IOException;


public class Main {
    public static void main(String []args) throws IOException{

        try{
            ServerSocket serverconnection = new ServerSocket(Server.PORT);    //create the server connection to the given port
            System.out.println("Server successfully started.");
            System.out.println("Server is listening to PORT " + Server.PORT);

            while(true){    //wait until the connection is established with the client
                Server webServer = new Server(serverconnection.accept());

                if(Server.verbose){
                    System.out.println("connection opened.");
                }

                //create a thread specifically to handle this connection between the client and the server
                Thread connectionThread = new Thread(webServer);
                connectionThread.start();
            }
        }
        catch(IOException e){
            System.err.println("Server connection failed. Error : " + e.getMessage());
        }

    }
}
