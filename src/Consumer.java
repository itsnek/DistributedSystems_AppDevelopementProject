import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.file.*;

public class Consumer extends Node { //den ginetai me to extend thread na kanw extend mia allh klash taytoxrona,me to interface runnable mporw

    String arg1,arg2;
    int hash,i = 0;
    Broker tempBroker;
    List<Broker> BrokerList ;
    List<ArrayList<Integer>> BrokerHashtables ;
    LinkedList<MusicChunk> SongReceived = new LinkedList<>();
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

    public Boolean getFound(){
        return found;
    }

    public void handshake(ArtistName artist){

        //boolean foundCorrectBroker = false;
        try {

            requestSocket = new Socket("192.168.2.5", 50221);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            tempBroker = new Broker("192.168.2.5", 50221);

            Message handshake = new Message(artist.getArtistName(),null);

            out.writeObject(handshake);

            Message temp = (Message) in.readObject();
            if(temp.getBoolean()){
                BrokerList = temp.getBrokers();
                BrokerHashtables = temp.getBrokersHashtable();
                found = true;
            }else{

                System.out.println("Im not serving this artist. Here are all the other Brokers");

                //Message temp = (Message) in.readObject();
                BrokerList = temp.getBrokers();
                BrokerHashtables = temp.getBrokersHashtable();

                for(int j = 0; j < BrokerHashtables.size(); j++){

                    ArrayList<Integer> temp2 = BrokerHashtables.get(j);
                    System.out.println("edw mphka");

                    if (temp2.contains(artist.getArtistName().hashCode())) {
                        System.out.println(BrokerList.get(j).getAddress());
                        System.out.println(BrokerList.get(j).getPort() - 1);

                        tempBroker = new Broker(BrokerList.get(j).getAddress(), BrokerList.get(j).getPort() - 1);

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

    public void requestSong(ArtistName artist,String song){

        try {
            System.out.println(tempBroker.getAddress());
            System.out.println(tempBroker.getPort());

            requestSocket = new Socket(tempBroker.getAddress(), tempBroker.getPort());
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());

            Message requestSong = new Message(artist.getArtistName(),song); // create message
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

//    public void playData (){
//
//        try {
//            //Collecting them in a queue.Another option is to collect them in a folder.
//            Message temp = (Message) in.readObject();
//            SongReceived.add(temp.getChunk()); //try to read received message,the type may differ.
//
//            //TODO:Start playing each chunk(suggested method ---> manually)
//
//        }catch (ClassNotFoundException e) {
//            System.out.println("/nUnknown object type received.");
//            e.printStackTrace();
//        }catch (IOException ioException) {
//            ioException.printStackTrace();
//        }
//
//    }
public void playData (){
    File myObj = new File("D:\\Nikos\\Documents\\GitHub\\distributed\\song.mp3");
    try {
        while(true) {
            if(in.readObject()!=null){
                break;
            }
        }

        //Collecting them in a queue.Another option is to collect them in a folder.
        Message temp1 = (Message) in.readObject();

        SongReceived.add(temp1.getChunk()); //try to read received message,the type may differ.
        System.out.println("perasa");

        int recievedChunks = 1;
        while (recievedChunks < temp1.getChunk().getTotalPartitions()) {
//            System.out.println(temp.getChunk().getTotalPartitions());
//            System.out.println(temp.getChunk().getPartitionNumber() + " + " + temp.getChunk().getPartition().length);

            Message temp = (Message) in.readObject();
            System.out.println("perasa");

            System.out.println(recievedChunks);

            //System.out.println(temp.getChunk().getTotalPartitions());
            System.out.println(temp.getChunk().getPartitionNumber() + " + " + temp.getChunk().getPartition().length);
            SongReceived.add(temp.getChunk());
            recievedChunks++;

        }

        try {
            int partLookingFor = 0;
            for (int i=0; i<SongReceived.size(); i++) {
                boolean foundChunk = false;
                int j = 0;
                //Files.createDirectory("D:\\Nikos\\Documents\\GitHub\\distributed","Project");
                myObj.createNewFile();
                while (!foundChunk) {
                    if (partLookingFor == SongReceived.get(j).getPartitionNumber()) {
                        Files.write(Paths.get("song.mp3"), SongReceived.get(j).getPartition(), StandardOpenOption.APPEND);
                        foundChunk = true;
                        System.out.println ("Writing File");

                    }
                    j++;
                }
                partLookingFor++;
            }
        } catch (UnsupportedOperationException unsO) {
            System.out.println ("Appending isn't available.");
            unsO.printStackTrace();
        } catch (SecurityException sec) {
            sec.printStackTrace();
        } catch (EOFException eof) {
            System.out.println("error");
            eof.printStackTrace();
        }


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

    //THE MAIN FOR EVERY CONSUMER AFTER THE FINAL VERSION :
    public static void main(String args[]) {

        Consumer cons1 = new Consumer();
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        ArtistName artist = new ArtistName(myObj.nextLine());

        //Handshake with a random broker and check if its the correct one and register, else try again.
        cons1.handshake(artist);

        if (cons1.getFound()) {
            //Request artist's song.
            System.out.println("Which song of this artist do you want to listen?/n");
            cons1.requestSong(artist,myObj.nextLine());

            cons1.playData();
            System.out.println ("ending.");

        }
        myObj.close();
        //cons1.disconnect();
    }

}