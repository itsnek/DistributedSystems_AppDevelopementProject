import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class BrokerCommunicator extends Thread {
    ArrayList<Integer> hashtable;
    List<Broker> registeredBrokers;
    private Socket requestSocket = null;

    BrokerCommunicator(){

    }

    BrokerCommunicator(ArrayList<Integer> hashtable,List<Broker> registeredBrokers){
        this.hashtable = hashtable;
        this.registeredBrokers = registeredBrokers;
    }

    public void run() {

        try {

            for (int i = 0; i < registeredBrokers.size(); i++) {

                if (registeredBrokers.get(i).getAddress().equals(InetAddress.getLocalHost().getHostAddress())) {
                    System.out.println("This is my ip.");
                } else {
                    try {

                        requestSocket = new Socket(registeredBrokers.get(i).getAddress(), 50850);
                        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
                        //ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());

                        out.writeObject(new Message(hashtable));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        }catch (UnknownHostException unknownHost){
            System.out.println("Error!You are trying to connect to an unknown host!");
        }

    }

}
