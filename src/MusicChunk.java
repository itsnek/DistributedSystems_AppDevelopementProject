import java.io.Serializable;

class MusicChunk implements Serializable {
    private String artistName; // optional
    private String songTitle;
    private byte [] partition;
    private int partitionNumber; // # of chunk
    private int totalPartitions; // the total number of partitions

    public MusicChunk () {
        this.artistName = null;
        this.songTitle = null;
        this.partitionNumber = 0;
        this.totalPartitions = 0;
    }

    public MusicChunk (String artistName, String songTitle, byte [] partition, int partitionNumber) {
        this.artistName = artistName;
        this.songTitle = songTitle;
        this.partition = partition;
        this.partitionNumber = partitionNumber;
        totalPartitions = 0;
    }

    public void setArtistName(String artistName){
        this.artistName = artistName;
    }

    public void setSongTitle(String songTitle){
        this.songTitle = songTitle;
    }

    public void setPartition(byte [] partition){
        this.partition = partition;
    }

    public void setPartitionNumber(int partitionNumber){
        this.partitionNumber = partitionNumber;
    }

    public void setTotalPartitions(int totalPartitions){
        this.totalPartitions = totalPartitions;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public byte[] getPartition() {
        return partition;
    }

    public int getPartitionNumber() {
        return partitionNumber;
    }

    public int getTotalPartitions() {
        return totalPartitions;
    }
}
