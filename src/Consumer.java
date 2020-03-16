import java.io.*;
import java.net.*;

public class Consumer extends Thread {  //Node

    String arg1,arg2;

    Socket requestSocket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;

    Consumer(){}

    Consumer(String a){
        arg1 = a;
    }

    protected void register (Broker br , ArtistName artN){

        try {

            requestSocket = new Socket(br, 4321); //opens connection
            out = new ObjectOutputStream(requestSocket.getOutputStream()); // streams
            in = new ObjectInputStream(requestSocket.getInputStream());    //  used


            String request = artN.getArtistName(); // create message
            System.out.println("Message created.");
            out.writeObject(request); //send message
            out.flush();
            System.out.println("Message sent.");
            try {
                System.out.println("Message received is: " + ((String) in.readObject())); //try to read received message
            } catch (ClassNotFoundException e) {
                System.out.println("/nUnknown object type received.");
                e.printStackTrace();
            }

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

    }

    public void run(){

    }


    public static void main(String args[]) {
        new Consumer("2pac").start();
        new Consumer("biggie").start();
    }

}
