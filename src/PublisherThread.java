import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PublisherThread extends Thread{
    private Socket clientSocket;

    public PublisherThread (Socket cs) {
        clientSocket = cs;
    }

    public void run () {

        try {

            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        //push();

    }



}