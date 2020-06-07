package MyPackage;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class PublisherThread extends Thread{

    private static final long serialVersionUID = 3828930004421967914L;
    private ArrayList<ArtistName> Artists = null;
    private ArrayList <MusicFile> Songs = null;
    private ArrayList<MusicChunk> Chunks = new ArrayList<>();
    private Socket clientSocket;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
    boolean found = false;
    boolean foundS = false;

    //Constructor
    public PublisherThread (Socket cs , ArrayList Artists , ArrayList Songs) {

        this.Artists = Artists;
        this.Songs = Songs;

        try {

            clientSocket = cs;

            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(clientSocket.getInputStream());

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    //Main method used for passing the chunks to the brokers.
    public void push(String song){

        try {

            //if client answers the song he requests then :
            for (int i = 0; i < Songs.size(); i++) {

                if (Songs.get(i).getTrackName().equals(song)) {

                    //Divides song into chunks.
                    Chunks = Songs.get(i).createChunks();

                    System.out.println("Job's done here!");

                    for (int j = 0; j < Chunks.size(); j++) {

                        //Starts sending chunks one by one,without waiting a response though.
                        Message temp = new Message(Chunks.get(j));
                        out.writeObject(temp);
                        out.flush();

                        System.out.println("Object returning to Broker...");

                    }

                    foundS = true;
                }
            }

            //If not found returns an appropriate message.
            if (!foundS){
                out.writeBytes("Invalid input!Song not found.");
            }

        }catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    public void run () {

        try {
            //Reads the message delivered from the Broker.
            Message request = (Message) in.readObject();
            System.out.println("Message received from Broker.");

            push(request.toString());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }finally {
            try {
                in.close();                                     // Closes
                out.close();                                    // streams
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

}