import java.io.*;
import java.net.*;
import java.util.List;

public class Node {

    List<Broker> brokers;
    Socket socket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    int id;

    Node(){

    }

    Node(int id){
        this.id = id;
    }

    public void init(int i){

        this.id = i;

    }

    public List<Broker> getBrokers(){

        for(int i = 0;i < brokers.size();i++){
            System.out.println(brokers.get(i));
        }

    }

    public void connect(){
        try {
            socket = new Socket("127.0.0.1", 4321); //opens connection    //"127.0.0.1" sees as server the cpu of my own pc
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


    public void updateNodes(){


    }

}
