import java.io.*;
import java.net.*;
import java.util.*;

public class Consumer extends Node implements Runnable { //den ginetai me to extend thread na kanw extend mia allh klash taytoxrona,me to interface runnable mporw

    String arg1,arg2;
    int hash,i = 0;
    Message broker;
    List<Broker> BrokerList ;
    List<ArrayList<Integer>> BrokerHashtables ;
    Queue<MusicChunk> SongReceived = new LinkedList<>(); ;
    private Socket requestSocket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    boolean found = false;

    Consumer(){}

    Consumer(String a){
        arg1 = a;
    }
    Consumer(int a){
        hash = a;
    }

    Consumer(String a,String b){
        arg1 = a;
        arg2 = b;
    }

    public String getArg1() {
        return arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public void handshake(ArtistName artist){

        //boolean foundCorrectBroker = false;
        try {

            requestSocket = new Socket("192.168.2.5", 50200);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            int artistHash = artist.getArtistName().hashCode();

            Message handshake = new Message(artistHash);

            out.writeObject(handshake);

            if(in.readBoolean()){

            }else{

                System.out.println("Im not serving this artist. Here are all the other Brokers");

                Message temp = (Message) in.readObject();
                BrokerList = temp.getBrokers();
                BrokerHashtables = temp.getBrokersHashtable();

                for(int j = 0; j < BrokerHashtables.size(); j++){

                    ArrayList<Integer> temp2 = BrokerHashtables.get(j);
                    if (temp2.contains(artistHash)) {
                        requestSocket = new Socket(BrokerList.get(j).getAddress(), BrokerList.get(j).getPort() - 1);
                        out = new ObjectOutputStream(requestSocket.getOutputStream());
                        out.flush();
                        in = new ObjectInputStream(requestSocket.getInputStream());

                        found = true;
                    }

                }
                if (!found){
                    System.out.println("Sorry,why don't have any song of this artist in our system.");
                }

            }

        }catch(UnknownHostException unknownHost){
            System.out.println("Error!You are trying to connect to an unknown host!");
        }catch (IOException ioException) {
            ioException.printStackTrace();
        }catch (ClassNotFoundException e) {
            System.out.println("/nUnknown object type received.");
            e.printStackTrace();
        }
    }

    public void lookForArtist(ArtistName artN){
        try {

            Message request = new Message(artN.getArtistName());
            System.out.println("Message created.");
            out.writeObject(request); //send message
            out.flush();
            System.out.println("Message sent.");

        }catch (UnknownHostException unknownHost) {
            System.out.println("Error!You are trying to connect to an unknown host!");
        }catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void requestSong(String song){

        try {

            Message requestSong = new Message(song); // create message
            System.out.println("Message of the song created.");
            out.writeObject(requestSong); //send message
            out.flush();
            System.out.println("Message of the song sent.");

        }catch (UnknownHostException unknownHost) {
            System.out.println("Error!You are trying to connect to an unknown host!");
        }catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    public void playData (){

        try {
            //Collecting them in a queue.Another option is to collect them in a folder.
            Message temp = (Message) in.readObject();
            SongReceived.add(temp.getChunk()); //try to read received message,the type may differ.

            //TODO:Start playing each chunk(suggested method ---> manually)

        }catch (ClassNotFoundException e) {
            System.out.println("/nUnknown object type received.");
            e.printStackTrace();
        }catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    public void disconnect(){

        try {
            if(in!=null) in.close();
            if (out!=null)out.close();
            requestSocket.close();

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    public void run(){

        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        ArtistName artist = new ArtistName(myObj.nextLine());

        //Handshake with a random broker and check if its the correct one and register, else try again.
        handshake(artist);
        //Look for the songs of one artist.
        lookForArtist(artist);
        //Request artist's song.
        System.out.println("Which song of this artist do you want to listen?/n");
        requestSong(myObj.nextLine());
        //Collect received chunks and play them manually.
        playData();

        disconnect();

    }


//    public static void main(String args[]) {
//
//        //First thread created and executed
//        Consumer cons1 = new Consumer();
//        Thread t1 = new Thread(cons1);
//        t1.start();
//
//        //Second thread created and executed
//       /* Consumer cons2 = new Consumer();
//        Thread t2 = new Thread(cons2);
//        t2.start();*/
//
//    }
    //THE MAIN FOR EVERY CONSUMER AFTER THE FINAL VERSION :
    public static void main(String args[]) {

        Consumer cons1 = new Consumer();
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        ArtistName artist = new ArtistName(myObj.nextLine());

        //Handshake with a random broker and check if its the correct one and register, else try again.
        cons1.handshake(artist);

        //Look for the songs of one artist.
        cons1.lookForArtist(artist);

        //Request artist's song.
        System.out.println("Which song of this artist do you want to listen?/n");
        cons1.requestSong(myObj.nextLine());

        cons1.playData();

        cons1.disconnect();
    }

}