import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;
import java.lang.*;

import static java.lang.Integer.parseInt;

public class Broker extends Node implements Runnable {

    private ServerSocket providerSocket = null;
    private ServerSocket providerSocketPub = null;
    private Socket connection = null;
    private Socket connectionPub = null;
    private Socket requestSocket = null;
    private ArrayList<Consumer> registeredUsers = new ArrayList<>();
    private ArrayList<Publisher> registeredPublishers =  new ArrayList<>();
    List<Broker> registeredBrokers;
    int serverHash,port;
    String address,Hashkey,Scope;
    Hashtable hashtable;

    Broker(){

    }

    Broker(String address,int port){
        this.address = address;
        this.port = port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public int getPort(){
        return port;
    }

    public int calculateKeys() {

        int ip = parseInt(providerSocket.getInetAddress().getHostAddress());  // Make registry key for
        int socketNumber = providerSocket.getLocalPort();                     // registeredUsers list.
        Integer sum = ip + socketNumber;
        serverHash = sum.hashCode();

        return serverHash;

    }

    public void initHashtable(){
        hashtable = new Hashtable(10,(long)0.8);
    }

    public void NotifyBrokers(){
        registeredBrokers = super.getBrokers();
        try {

            for (int i = 0; i < registeredBrokers.size(); i++) {

                if (registeredBrokers.get(i).getAddress().equals(InetAddress.getLocalHost().getHostAddress())) {
                    System.out.println("This is my ip.");
                    setAddress(registeredBrokers.get(i).getAddress());
                    setPort(registeredBrokers.get(i).getPort());

                }
                else {
                    //TODO: send hashtable to all the other brokers.
                    try {
                        //edw stravwnei stis parametrous.
                        System.out.println(registeredBrokers.get(i).getAddress());
                        System.out.println(registeredBrokers.get(i).getPort());
                        requestSocket = new Socket(registeredBrokers.get(i).getAddress(), registeredBrokers.get(i).getPort()-1);
                        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
                        //ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());

                        for (int j =0; j < hashtable.size(); j++){
                            out.writeObject(hashtable.get(j));
                        }

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }catch (UnknownHostException unknownHost){
            System.out.println("Error!You are trying to connect to an unknown host!");
        }
    }

    public void acceptConnection() {
        try{

            providerSocket = new ServerSocket(getPort()-1, 10);

            while (true) {

                connection = providerSocket.accept();

                Worker wk = new Worker(connection,registeredUsers,registeredPublishers,registeredBrokers);

                if(registeredBrokers.contains(new Broker(connection.getInetAddress().getHostAddress(),connection.getLocalPort()))) {
                    System.out.println("exw mpei");

                    new Thread(wk).start();
                }

                //Checks if the hash of the client is less than the Broker's.
                //if true, register the new client and start a worker in normal mode.
                //TODO: Create a check so old clients are only registered once.
                else if(wk.checkBroker(providerSocket)){

                    if(!registeredUsers.contains(new Consumer(serverHash))) {
                        System.out.println("Client registered.");
                        registeredUsers.add(new Consumer(serverHash));
                    }

                    System.out.println("Worker created.");
                    //Starting the worker in mode "1" --> Normal Operation
                    wk.setMode(1);

                }else {
                    System.out.println("Client connected on wrong broker. Letting him know...");
                    //Starting the worker in mode "0" --> Letting the Consumer know that its not the correct broker.
                    wk.setMode(0);
                }
                new Thread(wk).start();
                while (!wk.getEndOfThread()) {System.out.println("edw eimai!");}
                connection.close();
            }
        }catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void notifyPublisher() {

            try {
                providerSocketPub = new ServerSocket(50800, 10);

                while (true) {

                    connectionPub = providerSocketPub.accept();
                    ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());

                    registeredPublishers.add(new Publisher(calculateKeys()));

                    Scope = (String)in.readObject();

                    connectionPub.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }catch (ClassNotFoundException e) {
                System.out.println("/nUnknown object type received.");
                e.printStackTrace();
            }

    }

    public void disconnect(){

        super.disconnect();

    }

    public void run(){

        NotifyBrokers();

        // Accept connection with client and starts the whole process.
        acceptConnection();

        //disconnect();


    }

    public static void main(String args[]) {

        File file = new File("src\\Brokers.txt");

        Broker br1 = new Broker();
        Broker br2 = new Broker();
        Broker br3 = new Broker();

        br1.setBrokers(file);
        br2.setBrokers(file);
        br3.setBrokers(file);

        /*br1.notifyPublisher();
        br2.notifyPublisher();
        br3.notifyPublisher();*/

        //First Broker
        new Thread(br1).start();
        new Thread(br2).start();
        new Thread(br3).start();

        //Second Broker
        /*new Thread(new Broker()).start();
        //Third Broker
        new Thread(new Broker()).start();*/

    }

}
