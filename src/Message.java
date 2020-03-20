import java.io.Serializable;

public class Message implements Serializable {
    Integer a, b, sum;
    public Message(Integer a, Integer b) {
        this.a = a;
        this.b = b;
    }
    public Integer getA() {
        return a;
    }

    public Integer getB(){
        return b;
    }

    public void setSum(Integer sum){
        this.sum = sum;
    }
}