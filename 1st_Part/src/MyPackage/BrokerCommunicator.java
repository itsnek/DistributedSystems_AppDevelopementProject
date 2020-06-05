package MyPackage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class BrokerCommunicator extends Thread {

    ArrayList<Long> hashtable;
    List<Broker> registeredBrokers;
    private Socket requestSocket = null;
    private long myHash;

    //Constructors

    BrokerCommunicator(){

    }

    BrokerCommunicator(ArrayList<Long> hashtable,List<Broker> registeredBrokers,long myHash){
        this.hashtable = hashtable;
        this.registeredBrokers = registeredBrokers;
        this.myHash = myHash;
    }

    public void run() {

        try {
            //Sends to every registered Broker the hashtable and the hash of the broker that created the thread.
            for (int i = 0; i < registeredBrokers.size(); i++) {
                //Overrides his registry.
                if (registeredBrokers.get(i).getAddress().equals(InetAddress.getLocalHost().getHostAddress())) {
                    System.out.println("This is my ip.");
                } else {
                    try {
                        //Creates a request socket.
                        requestSocket = new Socket(registeredBrokers.get(i).getAddress(), 50850);
                        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
                        //ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());

                        //Sends his information.
                        out.writeObject(new Message(hashtable,myHash));

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
