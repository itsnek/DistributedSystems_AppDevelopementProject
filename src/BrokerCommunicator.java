import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.List;

public class BrokerCommunicator extends Thread {
    Hashtable hashtable;
    List<Broker> registeredBrokers;
    private Socket requestSocket = null;

    BrokerCommunicator(){

    }

    BrokerCommunicator(Hashtable hashtable,List<Broker> registeredBrokers){
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
                        //edw stravwnei stis parametrous.
                        System.out.println(registeredBrokers.get(i).getAddress());
                        System.out.println(registeredBrokers.get(i).getPort());
                        requestSocket = new Socket(registeredBrokers.get(i).getAddress(), registeredBrokers.get(i).getPort() - 1);
                        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
                        //ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());

                        for (int j = 0; j < hashtable.size(); j++) {
                            out.writeObject(hashtable.get(j));
                            System.out.println("here.");

                        }
                        System.out.println("or here.");

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
