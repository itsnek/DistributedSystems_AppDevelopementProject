import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Handler;

public class Consumer extends Node implements Runnable { //den ginetai me to extend thread na kanw extend mia allh klash taytoxrona,me to interface runnable mporw

    String arg1,arg2;

    private Socket requestSocket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;

    Consumer(){}

    Consumer(String a){
        arg1 = a;
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

    /*public void register (Broker br , ArtistName artN){

        try {

            requestSocket = new Socket(br.getAddress(), 4321); //opens connection //"127.0.0.1" sees as server the cpu of my own pc
            out = new ObjectOutputStream(requestSocket.getOutputStream()); // streams
            in = new ObjectInputStream(requestSocket.getInputStream());    //  used

            Message request = new Message(artN.getArtistName()); // create message
            System.out.println("Message created.");
            out.writeObject(request); //send message
            out.flush();
            System.out.println("Message sent.");

        }catch (UnknownHostException unknownHost) {
            System.out.println("Error!You are trying to connect to an unknown host!");
        }catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }*/

    public void handshake(ArtistName artist){

        boolean foundCorrectBroker = false;
        try {

            requestSocket = new Socket(InetAddress.getLocalHost(), 4321);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            int ip = requestSocket.getInetAddress().getHostAddress().hashCode();
            int socketNumber = requestSocket.getLocalPort();
            int artistHash = artist.getArtistName().hashCode();
            int sum = ip + socketNumber + artistHash;
            int clientHash = Integer.hashCode(sum);

            Message handshake = new Message(clientHash);

            out.writeObject(handshake);

            if(in.readBoolean()){
                System.out.println("mphka");
                foundCorrectBroker = true;
            }

            while(!foundCorrectBroker) {
                System.out.println("mphka2");
                requestSocket = new Socket("127.0.0.1", 4321);
                if(in.readBoolean()){
                    foundCorrectBroker = true;
                }
            }

        }catch(UnknownHostException unknownHost){
            System.out.println("Error!You are trying to connect to an unknown host!");
        }catch (IOException ioException) {
            ioException.printStackTrace();
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

            System.out.println("Message received is: " + ( in.readObject().toString())); //try to read received message,the type may differ.

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
        //Check if the broker has the artist i want.
        playData();
        //Request artist's song.
        System.out.println("Which song of this artist do you want to listen?/n");
        requestSong(myObj.nextLine());

        playData();

        disconnect();

    }


    public static void main(String args[]) {

        //First thread created and executed
        Consumer cons1 = new Consumer();
        Thread t1 = new Thread(cons1);
        t1.start();

        //Second thread created and executed
       /* Consumer cons2 = new Consumer();
        Thread t2 = new Thread(cons2);
        t2.start();*/

    }
    //THE MAIN FOR EVERY CONSUMER AFTER THE FINAL VERSION :
    /*public static void main(String args[]) {

        Consumer cons1 = new Consumer();

        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        ArtistName artist = new ArtistName(myObj.nextLine());

        //Handshake with a random broker and check if its the correct one and register, else try again.
        cons1.handshake(artist);
        cons1.requestSong(artist.getArtistName());
        //Check if the broker has the artist i want.
        cons1.playData(); //new Value(new MusicFile (getArg2()))
        //Request artist's song.
        System.out.println("Which song of this artist do you want to listen?/n");
        cons1.requestSong(myObj.nextLine());

        cons1.playData();

        cons1.disconnect();

    }*/

}
