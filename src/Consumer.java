import java.io.*;
import java.net.*;

public class Consumer extends Node implements Runnable { //den ginetai me to extend thread na kanw extend mia allh klash taytoxrona,me to interface runnable mporw

    String arg1,arg2;

    Socket requestSocket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;

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

    protected void register (Broker br , ArtistName artN){

        try {

            requestSocket = new Socket(br, 4321); //opens connection //"127.0.0.1" sees as server the cpu of my own pc
            out = new ObjectOutputStream(requestSocket.getOutputStream()); // streams
            in = new ObjectInputStream(requestSocket.getInputStream());    //  used


            String request = artN.getArtistName(); // create message
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

    protected void disconnect(Broker br, ArtistName artN){

        try {

            in.close(); out.close();
            requestSocket.close();

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    protected void playData (ArtistName artN , Value v){

        //pubsub code here

        try {

            System.out.println("Message received is: " + ((String) in.readObject())); //try to read received message,the type may differ.

        }catch (ClassNotFoundException e) {
            System.out.println("/nUnknown object type received.");
            e.printStackTrace();
        }catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    public void run(){

        ArtistName artist = new ArtistName(getArg1());

        register(new Broker(),artist);
        playData(artist,new Value(new MusicFile (getArg2())));
        disconnect(new Broker(),artist);

    }


    public static void main(String args[]) {

        //First thread created and executed
        Consumer cons1 = new Consumer("2pac","all eyes on me");
        Thread t1 = new Thread(cons1);
        t1.start();

        //Second thread created and executed
        Consumer cons2 = new Consumer("biggie","notorious thugs");
        Thread t2 = new Thread(cons2);
        t2.start();

    }

}
