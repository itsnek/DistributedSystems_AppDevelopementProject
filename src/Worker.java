import java.io.*;
import java.net.*;

public class Worker extends Thread {

    ObjectInputStream in;
    ObjectOutputStream out;
    private Socket requestSocket = null;
    private ObjectOutputStream out2 = null;
    private ObjectInputStream in2 = null;

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
                System.out.println("Message received from Client.");
                if(request != null) {                           // Checks if it's a connection from broker's side.
                    //new Broker().notifyPubliser(request);       // Not sure if it works for every circumstance.
                }

                try {

                    requestSocket = new Socket("127.0.0.1", 50190); //opens connection
                    out2 = new ObjectOutputStream(requestSocket.getOutputStream()); // streams
                    in2 = new ObjectInputStream(requestSocket.getInputStream());    //  used

                    out2.writeObject(request); //send message
                    out2.flush();
                    System.out.println("Message sent to publisher.");

                }catch (UnknownHostException unknownHost) {
                    System.out.println("Error!You are trying to connect to an unknown host!");
                }catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                System.out.println("Job's done!");
                out.writeObject((Message)in2.readObject());                       // Gives value to outputStream.
                System.out.println("Object returning to client...");


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