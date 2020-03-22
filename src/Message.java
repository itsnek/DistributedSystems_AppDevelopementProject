import java.io.Serializable;

public class Message implements Serializable {
    String a, b, sum;

    public Message(String a, String b) {
        this.a = a;
        this.b = b;
    }

    public Message(String a){
        this.a = a;
    }
    public String getA() {
        return a;
    }

    public String getB(){
        return b;
    }

    public void setSum(String sum){
        this.sum = sum;
    }
}