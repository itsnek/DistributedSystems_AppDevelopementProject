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
                Message request = (Message)in.readObject();
                System.out.println("Message received.");
                request.setSum(request.a + request.b);
                System.out.println("Job's done!");
                out.writeObject(request);
                System.out.println("Object returning...");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}