import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Worker extends Thread {

    String requestedSong;
    private Socket requestSocket = null;
    private Socket connection = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    private ObjectOutputStream publisherOut = null;
    private ObjectInputStream publisherIn = null;
    private ArrayList<Consumer> registeredUsers =  new ArrayList<>();
    private ArrayList<Publisher> registeredPublishers =  new ArrayList<>();
    private ArrayList<Integer> artists =  new ArrayList<>();
    private ArrayList<Broker> registeredBrokers;
    private ArrayList<ArrayList<Integer>> BrokersHashtable;
    private boolean endOfThread = false;
    private boolean entrance = false;

    public Worker(Socket connection,ArrayList<Consumer> registeredUsers,ArrayList<Publisher> registeredPublishers,ArrayList<Broker> registeredBrokers,ArrayList<Integer> artists,ArrayList<ArrayList<Integer>> BrokersHashtable) {
        this.registeredUsers = registeredUsers;
        this.registeredBrokers = registeredBrokers;
        this.registeredPublishers = registeredPublishers;
        this.artists = artists;
        this.BrokersHashtable = BrokersHashtable;
        this.connection = connection;
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Consumer> getRegisteredUsers() {
        return registeredUsers;
    }

    public boolean getEntrance(){
        return entrance;
    }

    public void setEntrance(boolean entrance) {
        this.entrance = entrance;
    }

    public boolean getEndOfThread () {
        return endOfThread;
    }

    public ArrayList<ArrayList<Integer>> getBrokersHashtable() {
        return BrokersHashtable;
    }

    public void run() {
        endOfThread = false;
        try {
            try {

                if(!getEntrance()) {
                    for (int i = 0; i < registeredBrokers.size(); i++) {

                        if (registeredBrokers.get(i).getAddress().equals(connection.getInetAddress().getHostAddress())) {
                            System.out.println("You son of a bitch. Im in.");

                            Message temp = (Message) in.readObject();
                            BrokersHashtable.add(i, temp.getHashtable());
                            for (int j = 0; j < BrokersHashtable.size(); j++) {
                                if (BrokersHashtable.get(j) == null) {
                                    BrokersHashtable.remove(j);
                                }
                            }
                            System.out.println(i);
                            System.out.println(BrokersHashtable.size());
//                            if (registeredBrokers.size() == BrokersHashtable.size()) {
//                                System.out.println("mphka3");
//                                entrance = true;
//                            }
                        }
                    }
                    if (BrokersHashtable.size() == registeredBrokers.size()-1) {
                        System.out.println("mphka3");
                        entrance = true;
                    }
                }
                else {

                    Message request = (Message) in.readObject();
                    System.out.println("Message received from Client.");

                    int artistHash = request.toString().hashCode();
                    requestedSong = request.getSong();

                    //Checks if the hash of the client is less than the Broker's.
                    if (!artists.contains(artistHash)) {
                        System.out.println("here");
                        Message brokersInfo = new Message(BrokersHashtable, registeredBrokers,false);
                        out.writeObject(brokersInfo);
                    } else {
                        Message brokersInfo = new Message(BrokersHashtable, registeredBrokers, true);
                        out.writeObject(brokersInfo);
                        if (!registeredUsers.contains(new Consumer(connection.getInetAddress().getHostAddress()))) {
                            registeredUsers.add(new Consumer(connection.getInetAddress().getHostAddress()));
                        }
                        if (requestedSong!=null) {
                            System.out.println("hi");

                            try {
                                for (int i = 0; i < registeredPublishers.size(); i++) {
                                    //TODO: Check the limits,doesnt find the correct publisher.
                                    if (request.toString().charAt(0) >= registeredPublishers.get(i).getScope().charAt(0) && request.toString().charAt(1) <= registeredPublishers.get(i).getScope().charAt(1)) {

                                        requestSocket = new Socket(registeredPublishers.get(i).getAddress(), 50190); //opens connection
                                        publisherOut = new ObjectOutputStream(requestSocket.getOutputStream()); // streams
                                        publisherIn = new ObjectInputStream(requestSocket.getInputStream());    //  used
                                        System.out.println(requestedSong);

                                        publisherOut.writeObject(requestedSong); //send message
                                        publisherOut.flush();
                                        System.out.println("Message sent to publisher.");
                                    }
                                }
                            } catch (UnknownHostException unknownHost) {
                                System.out.println("Error! You are trying to connect to an unknown host!");
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                            System.out.println("Job's done!");
                            Message temp = (Message) publisherIn.readObject();
                            out.writeObject(temp);
                            System.out.println("Object returning to client...");
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                    System.out.println("Bye");

                    endOfThread = true;
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}