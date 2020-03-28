import java.io.*;
import java.net.*;

public class PublisherThread extends Thread{

    private Socket clientSocket;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;

    public PublisherThread (Socket cs) {

        try {

            clientSocket = cs;

            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(clientSocket.getInputStream());

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    public void run () {

        try {

            Message request = (Message)in.readObject();     // Gives value to inputStream.
            System.out.println("Message received from Broker.");

            System.out.println("Job's done here!");
            out.writeObject(request);                       // Gives value to outputStream.
            System.out.println("Object returning to Broker...");



        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }finally {
            try {
                in.close();                                     // Closes
                out.close();                                    // streams
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        //push();

    }



}