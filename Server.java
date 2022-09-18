import java.net.*;
import java.util.Scanner;
import java.io.*;
import java.awt.Desktop;

public class Server{
    public static void main(String []args) throws IOException{
        ServerSocket serverSide = new ServerSocket(2728);
        Socket communication = serverSide.accept(); //block until the client is connected to the server (wait until the communication is established)
        
        System.out.println("Client connected");

        try{
            File index = new File("htdocs/index.html");
            Scanner fileScan = new Scanner(index);
            String content = "";
            while(fileScan.hasNext()){
                content = content + fileScan.nextLine();
            }
            while(true){
                PrintWriter out = new PrintWriter(communication.getOutputStream());
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: text/html");
                out.println("\r\n");
                out.println(content);
                out.flush();
                try{
                    out.wait();
                }
                catch(Exception e){
                    System.err.println(e);
                }
                //out.close();
                //communication.close();
                //serverSide.close();
            }
            //fileScan.close();
        }
        catch(FileNotFoundException e){
            System.err.println(e);
            System.exit(1);
        }
    }
}