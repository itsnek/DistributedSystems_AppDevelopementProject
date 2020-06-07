package MyPackage;

import java.io.*;
import java.net.*;
import java.util.*;

public class Node {

    protected ArrayList<Broker> brokers = new ArrayList<>();
    private ArrayList<String> addr = new ArrayList<String>();
    private Socket socket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private int id;
    private String line;

    //Constructors
    Node(){}

    Node(int id){
        this.id = id;
    }

    //Set method basically
    public void init(int i){

        this.id = i;

    }

    // Setters/Getters

    //Reading the addresses of the Brokers form the csv file.
    public void setBrokers(File Brokers) {
        //Stores the result return from ReadFile temporarily.
        ArrayList<String> temp = ReadFile(Brokers);
        for(int i = 0; i < temp.size(); i = i + 2){
            //Basically gets the first and second place of each row(ip,port),creates an instance and adds it to the main arraylist.
            this.brokers.add(new Broker(temp.get(i), Integer.parseInt(temp.get(i + 1))));
        }
    }

    public ArrayList<Broker> getBrokers(){
        return brokers;
    }

    //Reads the file that includes Brokers information inserted in the beginning.
    public ArrayList<String> ReadFile(File Brokers){

        try (BufferedReader br = new BufferedReader(new FileReader(Brokers))) {

            line = br.readLine();
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] addresses = line.split(",");

                for(int j = 0; j < addresses.length; j++){
                    addr.add(addresses[j]);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addr;
    }

    //Method used for the connection.
    public void connect(){
        try {

            socket = new Socket("127.0.0.1", 4321); //opens connection
            out = new ObjectOutputStream(socket.getOutputStream()); // streams
            in = new ObjectInputStream(socket.getInputStream());    //  used

        }catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void disconnect(){

        try {

            in.close(); out.close();
            socket.close();

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

}
