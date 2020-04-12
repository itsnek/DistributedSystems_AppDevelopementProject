import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class PublisherThread extends Thread{

    private ArrayList<ArtistName> Artists = null;
    private ArrayList <MusicFile> Songs = null;
    private ArrayList<MusicChunk> Chunks = new ArrayList<>();
    private Socket clientSocket;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
    boolean found = false;
    boolean foundS = false;

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

//    public void pushList(String artN){
//
//        try {
//
//            //First returns the list of the artist's songs,or failure message.
//            out.writeBytes(artN + "'s list of songs :");
//            for (int i = 0; i < Songs.size(); i++) {
//                if (Songs.get(i).getArtistName().equals(artN)) {
//                    out.writeBytes(Songs.get(i).getTrackName());
//                    found = true;
//                }
//            }
//
//        }catch (IOException ioe) {
//            ioe.printStackTrace();
//        }
//
//    }
//
//    public  void notifyFailure() {
//        try {
//
//            out.writeBytes("Sorry,we don't have any songs of this artist.");
//
//            found = false;
//            foundS = false;
//
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }
//    }


    public void push(String song){

        try {

            //if client answers the song he requests then :
            for (int i = 0; i < Songs.size(); i++) {

                if (Songs.get(i).getTrackName().equals(song)) {
                    System.out.println(Songs.get(i).getTrackName());

                    Chunks = Songs.get(i).createChunks();

                    System.out.println("Chunks : " + Chunks.size());
                    System.out.println("Job's done here!");

                    for (int j = 0; j < Chunks.size(); j++) {

                        System.out.println(Chunks.get(j).getPartitionNumber() + " + " +  Chunks.get(j).getPartition().length);
                        Message temp = new Message(Chunks.get(j));
                        out.writeObject(temp);
                        out.flush();

                        System.out.println("Object returning to Broker...");

                    }

                    foundS = true;
                }
            }

            if (!foundS){
                out.writeBytes("Invalid input!Song not found.");
            }

        }catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    public void run () {

        try {

            Message request = (Message) in.readObject();     // Gives value to inputStream.
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