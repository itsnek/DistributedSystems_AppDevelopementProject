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
    ArrayList Broker2 = new ArrayList();
    ArrayList Broker3 = new ArrayList();
    List<Broker> registeredBrokers;
    int serverHash,port;
    int counter = 0;
    String address,Hashkey,Scope;
    Hashtable hashtable;

    Broker(){

    }

    Broker(String address,int port){
        this.address = address;
        this.port = port;
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
                if (registeredBrokers.get(i).getAddress().equals(InetAddress.getLocalHost().getHostAddress())) {System.out.println("This is my ip.");}
                else {
                    //TODO: send hashtable to all the other brokers.
                    try {
                        requestSocket = new Socket(registeredBrokers.get(i).getAddress(), registeredBrokers.get(i).getPort());
                        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
                        ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());

                        for (int j =0; j < hashtable.size(); j++){
                            out.writeObject(hashtable.get(j));
                        }

                        if(counter == 0){
                            Broker2.add(in.readObject());
                            counter++;
                        }else {
                            Broker3.add(in.readObject());
                        }

                    }catch (IOException e){
                        e.printStackTrace();
                    }catch (ClassNotFoundException e) {
                        System.out.println("/nUnknown object type received.");
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

            providerSocket = new ServerSocket(4321, 10);

            while (true) {

                connection = providerSocket.accept();
                Worker wk = new Worker(connection,registeredUsers,registeredPublishers);

                //Checks if the hash of the client is less than the Broker's.
                //if true, register the new client and start a worker in normal mode.
                //TODO: Create a check so old clients are only registered once.
                if(wk.checkBroker(providerSocket)){

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
                while (!wk.getEndOfThread()) {}
                connection.close();
            }
        }catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void notifyPublisher() {

        try {
            providerSocketPub = new ServerSocket(4322, 10);
            connectionPub = providerSocket.accept();
            ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());

            registeredPublishers.add(new Publisher(calculateKeys()));

            for (int j =0; j < hashtable.size(); j++) {
                out.writeObject(hashtable.get(j));
            }

            //Scope = (String)in.readObject();

            connectionPub.close();

        }catch (IOException ioException) {
            ioException.printStackTrace();
        }/*catch (ClassNotFoundException e) {
            System.out.println("/nUnknown object type received.");
            e.printStackTrace();
        }*/

    }

    public void disconnect(){

        super.disconnect();

    }

    public void run(){

        // Accept connection with client and starts the whole process.
        //NotifyBrokers();
        acceptConnection();

        //disconnect();


    }

    public static void main(String args[]) {

        /*Broker br = new Broker();
        br.acceptConnection();
        br.disconnect();*/
        File file = new File("C:\\Users\\Nikos\\Desktop\\Brokers.txt");
        Broker br = new Broker();
        br.setBrokers(file);

        br.notifyPublisher();

        //First Broker
        new Thread(br).start();
        //Second Broker
        /*new Thread(new Broker()).start();
        //Third Broker
        new Thread(new Broker()).start();*/

    }

}
