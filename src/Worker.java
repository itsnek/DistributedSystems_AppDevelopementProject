import java.io.*;
import java.net.*;
import java.util.ArrayList;
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
    List<Broker> registeredBrokers;
    ArrayList Broker2 = new ArrayList();
    ArrayList Broker3 = new ArrayList();
    String AddrBr2,AddrBr3;
    int port2,port3;
    int counter = 0;
    int mode;
    private boolean endOfThread = false;

    /*public Worker(Socket connection,int mode) {
        this.mode = mode;
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public Worker(Socket connection, ArrayList<Consumer> registeredUsers ,ArrayList<Publisher> registeredPublishers,List<Broker> registeredBrokers) {
        this.registeredBrokers = registeredBrokers;
        this.registeredUsers = registeredUsers;
        this.registeredPublishers = registeredPublishers;
        this.connection = connection;
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public boolean getEndOfThread () {
        return endOfThread;
    }

    public boolean checkBroker(ServerSocket mySocket) {
        int ip = mySocket.getInetAddress().getHostAddress().hashCode();
        int socketNumber = mySocket.getLocalPort();
        int sum = ip + socketNumber;
        int serverHash = Integer.hashCode(sum);

        try {
            Message msg = (Message)in.readObject();
            int connectionHash = msg.getHash();
            return connectionHash <= serverHash;
        }catch (IOException | ClassNotFoundException ioException){
            return false;
        }
    }

    public void run() {
        endOfThread = false;
        try {
            try {
                System.out.println(connection.getInetAddress().getHostAddress());

                if(registeredBrokers.contains(new Broker(connection.getInetAddress().getHostAddress(),connection.getLocalPort()))){

                    if(counter == 0){
                        Broker2.add(in.readObject());
                        AddrBr2 = connection.getInetAddress().getHostAddress();
                        port2 = connection.getLocalPort();
                        counter++;
                    }else {
                        Broker3.add(in.readObject());
                        AddrBr3 = connection.getInetAddress().getHostAddress();
                        port3 = connection.getLocalPort();
                        System.out.println("mphka3");
                    }
                }else {

                    if (mode == 0) {

                        out.writeBoolean(false);

                        Message request = (Message) in.readObject();     // Gives value to inputStream.
                        int artistHash = request.getArtist();

                        if(Broker2.contains(artistHash)){
                            Message nextBroker = new Message(AddrBr2,port2);
                            out.writeObject(nextBroker);
                        }
                        if(Broker3.contains(artistHash)){
                            Message nextBroker = new Message(AddrBr3,port3);
                            out.writeObject(nextBroker);
                        }

                        out.flush();

                    } else {
                        Message request = (Message) in.readObject();     // Gives value to inputStream.
                        System.out.println("Message received from Client.");

                        try {
                            for (int i = 0; i < registeredPublishers.size(); i++) {
                                if (request.toString().charAt(0) > registeredPublishers.get(i).getScope().charAt(0) && request.toString().charAt(1) > registeredPublishers.get(i).getScope().charAt(1)) {
                                    //TODO : change the parametres.
                                    requestSocket = new Socket("127.0.0.1", 50190); //opens connection
                                    publisherOut = new ObjectOutputStream(requestSocket.getOutputStream()); // streams
                                    publisherIn = new ObjectInputStream(requestSocket.getInputStream());    //  used

                                    publisherOut.writeObject(request); //send message
                                    publisherOut.flush();
                                    System.out.println("Message sent to publisher.");
                                }
                            }

                        } catch (UnknownHostException unknownHost) {
                            System.out.println("Error!You are trying to connect to an unknown host!");
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }

                        System.out.println("Job's done!");
                        out.writeObject((Message) publisherIn.readObject());                       // Gives value to outputStream.
                        System.out.println("Object returning to client...");

                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();                                     // Closes
                out.close();
                // streams
                endOfThread = true;
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}