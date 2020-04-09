import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Worker extends Thread {

    private Socket requestSocket = null;
    private Socket connection = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    private ObjectOutputStream publisherOut = null;
    private ObjectInputStream publisherIn = null;
    private ArrayList<Consumer> registeredUsers = new ArrayList<>();
    private ArrayList<Publisher> registeredPublishers =  new ArrayList<>();
    private ArrayList<ArtistName> artists =  new ArrayList<>();
    private List<Broker> registeredBrokers;
    private List<Hashtable> BrokersHashtable;
    private boolean endOfThread = false;

    public Worker(Socket connection, ArrayList<Consumer> registeredUsers, ArrayList<Publisher> registeredPublishers, List<Broker> registeredBrokers, ArrayList<ArtistName> artists) {
        this.registeredBrokers = registeredBrokers;
        this.registeredUsers = registeredUsers;
        this.registeredPublishers = registeredPublishers;
        this.artists = artists;
        this.connection = connection;
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getEndOfThread () {
        return endOfThread;
    }

    public void run() {
        endOfThread = false;
        try {
            try {

                for (int i = 0; i < registeredBrokers.size(); i++) {
                    if (registeredBrokers.get(i).getAddress().equals(connection.getInetAddress().getHostAddress())) {
                        System.out.println("You son of a bitch. Im in.");

                        Message temp = (Message) in.readObject();
                        BrokersHashtable.add(i, temp.getHashtable());
                        System.out.println("mphka2");
                    }
                }

                Message request = (Message) in.readObject();
                System.out.println("Message received from Client.");
                if (!artists.contains(request.getArtistHash())) {
                    System.out.println("Im not serving this artist. Here are all the other Brokers");
                    Message brokersInfo = new Message(BrokersHashtable, registeredBrokers);
                    out.writeObject(brokersInfo);
                } else {
                    try {
                        for (int i = 0; i < registeredPublishers.size(); i++) {
                            if (request.toString().charAt(0) > registeredPublishers.get(i).getScope().charAt(0) && request.toString().charAt(1) > registeredPublishers.get(i).getScope().charAt(1)) {
                                //TODO : change the parameters.
                                requestSocket = new Socket("127.0.0.1", 50190); //opens connection
                                publisherOut = new ObjectOutputStream(requestSocket.getOutputStream()); // streams
                                publisherIn = new ObjectInputStream(requestSocket.getInputStream());    //  used

                                publisherOut.writeObject(request); //send message
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
                    out.writeObject((Message) publisherIn.readObject());
                    System.out.println("Object returning to client...");
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
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