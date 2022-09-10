import java.net.*;
import java.io.*;

public class Server{
    public static void main(String []args) throws IOException{
        ServerSocket serverSide = new ServerSocket(2728);
        Socket communication = serverSide.accept(); //block until the client is connected to the server (wait until the communication is established)

        System.out.println("Client connected");
        while(true){
        }
    }
}