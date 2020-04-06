import java.io.*;
import java.net.*;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class Worker extends Thread {

    private Socket requestSocket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    private ObjectOutputStream publisherOut = null;
    private ObjectInputStream publisherIn = null;
    private ArrayList<Consumer> registeredUsers = new ArrayList<>();
    private ArrayList<Publisher> registeredPublishers =  new ArrayList<>();
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

    public Worker(Socket connection, ArrayList<Consumer> registeredUsers ,ArrayList<Publisher> registeredPublishers) {
        this.registeredUsers = registeredUsers;
        this.registeredPublishers = registeredPublishers;
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

                if(mode == 0){

                    out.write(0);
                    out.flush();

                }else {
                    Message request = (Message) in.readObject();     // Gives value to inputStream.
                    System.out.println("Message received from Client.");

                    try {
                        for(int i = 0; i < registeredPublishers.size(); i++) {
                            if (request.toString().charAt(0) > registeredPublishers.get(i).getScope().charAt(0)) {
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