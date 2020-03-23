public class Publisher extends Thread {
    private List <ArtistName> Artists;
    private List <MusicFile> Songs;
    private Socket clientSocket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;

    public void init (List <ArtistName> artists, List<MusicFile> songs) {
        Artists = artists;
        Songs = songs;
    }

    public void connect () {
        try {
            clientSocket = new Socket("127.0.0.1", 1050);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
        } catch (UnknownHostException uhe) {
            System.err.println ("You are trying to connect to an unknown host.");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    public void disconnect () {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}












