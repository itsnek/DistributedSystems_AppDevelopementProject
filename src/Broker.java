import java.io.IOException;
import java.net.*;
import java.util.List;
import java.lang.*;

import static java.lang.Integer.parseInt;

public class Broker extends Node{

    ServerSocket providerSocket;
    Socket connection = null;
    List<Consumer> registeredUsers;
    List<Publisher> registeredPublishers;
    InetAddress addr;
    int port;

    public static void main(String args[]) {
        new Broker().listenSocket();
    }

    void listenSocket() {
        try {

            providerSocket = new ServerSocket(4321, 10);
            int ip = parseInt(providerSocket.getInetAddress().getHostAddress());
            int socketNumber = providerSocket.getLocalPort();
            Integer sum = ip + socketNumber;
            int serverHash = sum.hashCode();

            while (true) {

                connection = providerSocket.accept();


                if(){
                    pull();
                }else{

                }

                System.out.println("Client connected.");
                System.out.println("Handler created.");
                new Thread(new Worker(connection)).start();
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void calculateKeys(){



    }
    public Publisher acceptConnection(Publisher pub){



    }
    public Consumer acceptConnection(Consumer con){



    }
    public void notifyPubliser(){



    }
    public void pull(ArtistName aName){



    }
}
