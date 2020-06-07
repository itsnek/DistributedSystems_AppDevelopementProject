package MyPackage;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import com.example.musico.*;

import androidx.annotation.RequiresApi;

import com.example.musico.AsyncTaskActivity;
import com.example.musico.MusicPlayerActivity;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Consumer extends Node implements Serializable {
    private static final String TAG = Consumer.class.getSimpleName();

    String arg1,arg2;
    int hash,i = 0;
    List<Broker> BrokerList ;
    List<ArrayList<Long>> BrokerHashtables ;
    private List<String> ArtistList;
    LinkedList<MusicChunk> SongReceived = new LinkedList<>();
    private Socket requestSocket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    boolean found = false;
    Broker tempBroker = new Broker("192.168.2.8", 55221);
    private static final long serialVersionUID = 3828930004421967914L;
    private Context context;

    //Constructors

    public Consumer(){}

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

    public void setArtistList(List<String> artistList) {
        ArtistList = artistList;
    }

    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    public String getArg1() {
        return arg1;
    }

    public Boolean getFound(){
        return found;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public List<String> getArtistList() {
        return ArtistList;
    }

    public void getAllArtists(){
        try {
            //Creates request socket.
            requestSocket = new Socket("192.168.2.8", 55221);
            out = new ObjectOutputStream(requestSocket.getOutputStream());  // Streams
            in = new ObjectInputStream(requestSocket.getInputStream());     //  used

            Message handshake = new Message("Hello");

            //Sends hello message.
            out.writeObject(handshake);

            //Checks his response.
            Message temp = (Message) in.readObject();

            ArtistList = temp.getMegaArtistList();

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
            requestSocket = new Socket("192.168.2.8", 55221);
            out = new ObjectOutputStream(requestSocket.getOutputStream());  // Streams
            in = new ObjectInputStream(requestSocket.getInputStream());     //  used

            Message handshake = new Message(artist.getArtistName());

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

                    ArrayList<Long> temp2 = BrokerHashtables.get(j);

                    if (temp2.contains((long)artist.getArtistName().hashCode())) {

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

    public ObjectInputStream requestSong(ArtistName artist,String song){

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
        return in;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void playData (String song,Context con) throws FileNotFoundException {


        //Gets file's path.
        String filename = song + ".mp3";

        context = con;
        File myObj = new File(this.context.getFilesDir(), filename);

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
            try{
               while (recievedChunks <= temp1.getChunk().getTotalPartitions()) {
                //while (true){

                    Message temp = (Message) in.readObject();

                    SongReceived.add(temp.getChunk());

                    if (temp.getChunk().getPartitionNumber()==temp.getChunk().getTotalPartitions()-1) break;

                   recievedChunks++;

                }
            }catch (EOFException eof){
                eof.printStackTrace();
                System.out.println("End of file.");
            }

            //Writes the in order in a file,which is playable.
            try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
                int partLookingFor = 0;

                for (int i=0; i<SongReceived.size(); i++) {

                    boolean foundChunk = false;
                    int j = 0;

                    myObj.createNewFile();

                    while (!foundChunk) {
                        if (partLookingFor == SongReceived.get(j).getPartitionNumber()) {
                            fos.write(SongReceived.get(j).getPartition());
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
                System.out.println("find me at : " + context.getFilesDir());
                System.out.println("Enjoy!");
            }

        }catch (ClassNotFoundException e) {
            System.out.println("/nUnknown object type received.");
            e.printStackTrace();
        }catch (IOException ioException) {
            System.out.println("Sorry,why don't have this song in our system.");
            ioException.printStackTrace();
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    void playSongOnline(Consumer cons,Context con,String song) throws IOException, ClassNotFoundException {
        /* This list contains chunks that came earlier than than their order. For example chunk with number 3 if it arrived earlier
        than chunk with number 2. I delete a chunk after i use it.
         */
        ArrayList<MusicChunk> earlyChunks = new ArrayList<>();

        //Set path.
        String filename = song + ".mp3";
        context = con;
        File mp3File = new File(this.context.getFilesDir(), filename);

        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            while (true) {
                if (cons.getIn().readObject() != null) break;
            }

            Message message = (Message) cons.getIn().readObject();
            MusicChunk mChunk = message.getChunk();
            int partLookingFor = 0;
            if (mChunk.getPartitionNumber() == 0) {
                mp3File.createNewFile();
                fos.write(mChunk.getPartition());
                partLookingFor++; //Now it's equal to one.
            } else {
                earlyChunks.add(mChunk);
            }
            int totalChunks = mChunk.getTotalPartitions();

            while (partLookingFor < totalChunks - 1) {
                message = (Message) cons.getIn().readObject();
                mChunk = message.getChunk();
                if (partLookingFor == mChunk.getPartitionNumber()) {
                    if(!mp3File.exists()) {
                        mp3File.createNewFile();
                    }
                    fos.write(mChunk.getPartition());
                    partLookingFor++;
                } else {
                    earlyChunks.add(mChunk);
                    for (int i=0; i < earlyChunks.size(); i++) {
                        if (earlyChunks.get(i).getPartitionNumber() == partLookingFor) {
                            if(!mp3File.exists()) {
                                mp3File.createNewFile();
                            }
                            fos.write(earlyChunks.get(i).getPartition());
                            earlyChunks.remove(i); // Remove from list part that we were looking for (remove from RAM) because we wrote it in disk.
                            partLookingFor++;
                        }
                    }
                }
            }

        } catch (ClassNotFoundException cnf) {
            cnf.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
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

}
