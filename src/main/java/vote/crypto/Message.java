package vote.crypto;

import java.io.Serializable;
import java.math.BigInteger;

public class Message implements Serializable {
    private static final long serialVersionUID = 8101479853715775815L;

    private final BigInteger c1;
    private final BigInteger c2;

    public Message(BigInteger c1, BigInteger c2){
        this.c1 = c1;
        this.c2 = c2;
    }

    public BigInteger getC1() {
        return c1;
    }

    public BigInteger getC2() {
        return c2;
    }

    @Override
    public String toString(){
        return "c1: " + c1 + " c2: " + c2;
    }

}
