import java.io.*;
import java.net.*;

public class Worker extends Thread {

    ObjectInputStream in;
    ObjectOutputStream out;

    public Worker(Socket connection) {
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            try {
                Message request = (Message)in.readObject();     // Gives value to inputStream.
                System.out.println("Message received.");
                if(request != null) {                           // Checks if it's a connection from broker's side.
                    System.out.println("mphka");
                    //new Broker().notifyPubliser(request);       // Not sure if it works for every circumstance.
                }
                System.out.println("Job's done!");
                out.writeObject(request);                       // Gives value to outputStream.
                System.out.println("Object returning...");


            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();                                     // Closes
                out.close();                                    // streams
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}