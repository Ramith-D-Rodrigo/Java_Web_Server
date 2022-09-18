import java.net.*;
import java.util.StringTokenizer;
import java.io.*;

public class Server implements Runnable{

    public static final File WEB_ROOT = new File("htdocs"); //starting directory
    public static final String DEFAULT_FILE = "index.html";    //inital page of the webserver (when you type localhost:PORT into the web browser)
    public static final String UNSUPPORTED_METHOD = "unsupported_request.html"; //page that loads when the client request is not supported
    public static final String FILE_NOT_FOUND = "404.html";    //page that loads when the client requests for a web page that is not located in htdocs

    public static final int PORT = 2728;  //port number which the server listens to

    public static final boolean verbose = true;    //to see console logs (true to on, false to off)

    private Socket connection; //use a socket to establish the connection

    public Server(Socket c){    //Server Constructor
        connection = c;
    }

    @Override
    public void run() {
        //managing the client connection here
        BufferedReader input = null;
        PrintWriter output = null;
        BufferedOutputStream dataOut = null;
        String requestedFile = null;

        try{
            input = new BufferedReader(new InputStreamReader(connection.getInputStream())); //Read each character from the client using input stream on the socket
            output = new PrintWriter(connection.getOutputStream());    //To output the header to the client (character output)
            dataOut = new BufferedOutputStream(connection.getOutputStream());  //get binary output stream to client (for requested data)

            String clientInput = input.readLine();  //client request's first line
            StringTokenizer parse = new StringTokenizer(clientInput);   //parse into String tokenizer so we can split the structure of the HTTP request
            String HTTPMethod = parse.nextToken().toUpperCase();    //first token is the HTTP method

            requestedFile = parse.nextToken().toLowerCase();

            //System.out.println(requestedFile + " - " + clientInput + " - " + HTTPMethod);

            if(!HTTPMethod.equals("GET") && !HTTPMethod.equals("HEAD")){    //check if the method is not a GET or HEAD request
                if(verbose){
                    System.out.println(HTTPMethod + " method is not supported");
                }
                File file = new File(WEB_ROOT, UNSUPPORTED_METHOD); //the web page to load when method is not supported
                int fileLength = (int) file.length(); //type cast into int because the original returning type is long
                String contentMimeType = "text/html";   //file type
                byte[] fileData = readFileData(file, fileLength);

                //header information that we have to send to the client 
                output.println("HTTP/1.1 501 Not Implemented");
                output.println("Server: Java WebServer for SCS2205 Assignment");
                output.println("Content-type: " + contentMimeType);
                output.println("Content-length: " + fileLength);

                output.println();   //a blank line between header and the content of the html file
                output.flush();     //flush character output stream buffer

                dataOut.write(fileData, 0, fileLength);
                dataOut.flush();
            }
            else{
                //GET or HEAD Method

                if(requestedFile.endsWith("/")){    //we are at the root
                    requestedFile += DEFAULT_FILE;  //add the initial web page, i.e. index.html
                }

                File file = new File(WEB_ROOT, requestedFile);
                int fileLength = (int) file.length();
                String content = getContentType(requestedFile);

                if(HTTPMethod.equals("GET")){   //GET METHOD
                    byte[] fileData = readFileData(file, fileLength);

                    //header information that we have to send to the client 
                    output.println("HTTP/1.1 200 OK");
                    output.println("Server: Java WebServer for SCS2205 Assignment");
                    output.println("Content-Type: " + content);
                    output.println("Content-length: " + fileLength);

                    output.println(); //a blank line between header and the content of the html file
                    output.flush();    //flush character output stream buffer

                    dataOut.write(fileData, 0, fileLength);
                    dataOut.flush();
                    System.out.println("hi");
                }

                if(verbose){
                    System.out.println("File " + requestedFile + " of type " + content + " returned");
                }
            }
        }
        catch(FileNotFoundException e1){
            try{
                fileNotFound(output, dataOut, requestedFile);
            }
            catch(IOException e2){
                System.out.println("File not found. Error : " + e2.getMessage());
            }
        }
        catch(IOException e3){
            System.err.println("Server error has occured. Error : " + e3.getMessage());
        }
        finally{    //after done communicating with the server
            try {
                input.close();  //close the input stream
                output.close(); //close the output stream
                dataOut.close();    //close the buffer
                connection.close(); //close the connection
            } catch (Exception e) {
                System.err.println("Error while closing the input stream. Error: " + e.getMessage());
            }
            if(verbose){
                System.out.println("connection closed");
            }
        }
    }

    private byte[] readFileData(File file, int fileLength) throws IOException{ //the function to read the file data and store in bytes
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try{
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        }finally{    //after reading the file in bytes
            if(fileIn != null){ //if the input stream is not closed
                fileIn.close();
            }
        }
        return fileData;
    }

    private String getContentType(String requestedFile){
        if(requestedFile.endsWith(".htm") || requestedFile.endsWith(".html")){  //the current checking file is html
            return "text/html";
        }
        else{   //other files
            return "text/plain";
        }
    }

    private void fileNotFound(PrintWriter output, OutputStream dataOut, String requestedFile) throws IOException{
        File file = new File(WEB_ROOT, FILE_NOT_FOUND);     //load the correspoding web page to show the file not found error
        int fileLength = (int) file.length();
        String content = "text/html";
        byte[] fileContent = readFileData(file, fileLength);

        //header information that we have to send to the client 
        output.println("HTTP/1.1 404 File Not Found");
        output.println("Server: Java WebServer for SCS2205 Assignment");
        output.println("Content-type: " + content);
        output.println("Content-length: " + fileLength);

        output.println();   //a blank line between header and the content of the html file
        output.flush();     //flush character output stream buffer

        dataOut.write(fileContent, 0, fileLength);
        dataOut.flush();

        if(verbose){
            System.out.println("File " + requestedFile + " not found");
        }
    }

}