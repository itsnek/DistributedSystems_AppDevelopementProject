package MyPackage;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.file.*;

public class Consumer extends Node { //den ginetai me to extend thread na kanw extend mia allh klash taytoxrona,me to interface runnable mporw

    String arg1,arg2;
    int hash,i = 0;
    Broker tempBroker;
    List<Broker> BrokerList ;
    List<ArrayList<Long>> BrokerHashtables ;
    List<String> ArtistList;
    LinkedList<MusicChunk> SongReceived = new LinkedList<>();
    private Socket requestSocket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    boolean found = false;

    //Constructors

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

    //Setters / getters


    public String getArg1() {
        return arg1;
    }

    public Boolean getFound(){
        return found;
    }

    public void getArtistList(){
        try {
            //Creates request socket.
            requestSocket = new Socket("192.168.2.8", 50221);
            out = new ObjectOutputStream(requestSocket.getOutputStream());  // Streams
            in = new ObjectInputStream(requestSocket.getInputStream());     //  used

            Message handshake = new Message("Hello");

            //Sends hello message.
            out.writeObject(handshake);

            //Checks his response.
            Message temp = (Message) in.readObject();

            ArtistList=temp.getMegaArtistList();

        }catch (IOException ioException) {
            ioException.printStackTrace();
        }catch (ClassNotFoundException e) {
            System.out.println("/nUnknown object type received.");
            e.printStackTrace();
        }
    }

    public void handshake(ArtistName artist){

        try {
            //Creates request socket.
            requestSocket = new Socket("192.168.2.2", 50221);
            out = new ObjectOutputStream(requestSocket.getOutputStream());  // Streams
            in = new ObjectInputStream(requestSocket.getInputStream());     //  used

            tempBroker = new Broker("192.168.2.2", 50221);

            Message handshake = new Message(artist.getArtistName(),null);

            System.out.println(artist.getArtistName().hashCode());
            //Sends artist's name.
            out.writeObject(handshake);

            //Checks his response.
            Message temp = (Message) in.readObject();
            if(temp.getBoolean()){
                //Receives the hashtables and BrokerList
                BrokerList = temp.getBrokers();
                BrokerHashtables = temp.getBrokersHashtable();
                found = true;
            }else{

                System.out.println("Im not serving this artist. Here are all the other Brokers");

                //Receives the hashtables and BrokerList
                BrokerList = temp.getBrokers();
                BrokerHashtables = temp.getBrokersHashtable();

                for(int j = 0; j < BrokerHashtables.size(); j++){
                    System.out.println("mphka?");

                    ArrayList<Long> temp2 = BrokerHashtables.get(j);

                    if (temp2.contains((long)artist.getArtistName().hashCode())) {

                        tempBroker = new Broker(BrokerList.get(j).getAddress(), BrokerList.get(j).getPort() - 1);
                        System.out.println("edw?");

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

    public void playData (){
        //Gets file's path.
        File myObj = new File("D:\\Nikos\\Documents\\GitHub\\distributed\\1st_Part\\song.mp3");
        try {
            while(true) {
                if(in.readObject()!=null){
                    break;
                }
            }

            //Collecting them in a queue.Another option is to collect them in a folder.
            Message temp1 = (Message) in.readObject();

            SongReceived.add(temp1.getChunk()); //try to read received message,the type may differ.

            int recievedChunks = 1;
            while (recievedChunks < temp1.getChunk().getTotalPartitions()) {

                Message temp = (Message) in.readObject();

                SongReceived.add(temp.getChunk());
                recievedChunks++;

            }

            //Writes the in order in a file,which is playable.
            try {
                int partLookingFor = 0;

                for (int i=0; i<SongReceived.size(); i++) {

                    boolean foundChunk = false;
                    int j = 0;

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
            }finally {
                System.out.println("Enjoy!");
            }

        }catch (ClassNotFoundException e) {
            System.out.println("/nUnknown object type received.");
            e.printStackTrace();
        }catch (IOException ioException) {
            System.out.println("Sorry,why don't have this song in our system.");
            //ioException.printStackTrace();
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

    public static void main(String args[]) {

        Consumer cons1 = new Consumer();
        cons1.getArtistList();
        for(int i = 0; i < cons1.ArtistList.size(); i++){
            System.out.println(cons1.ArtistList.get(i));
        }
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        ArtistName artist = new ArtistName(myObj.nextLine()); //Client inserts the artist he wants.

        //Handshake with a random broker and check if its the correct one and register, else try again.
        cons1.handshake(artist);

        if (cons1.getFound()) {
            //Request artist's song.
            System.out.println("Which song of this artist do you want to listen?/n");
            cons1.requestSong(artist,myObj.nextLine());

            //Put the incoming chunks in a file that can be played manually.
            cons1.playData();

        }
        myObj.close();
        //cons1.disconnect();
    }

}