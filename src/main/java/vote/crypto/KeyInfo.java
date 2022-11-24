package vote.crypto;

import java.io.Serializable;
import java.math.BigInteger;

public class KeyInfo implements Serializable {
    private static final long serialVersionUID = -6691356147897722394L;

    private final BigInteger g;
    private final  BigInteger q;
    private final BigInteger p;
    private final BigInteger key; //shard or secret

    public KeyInfo(BigInteger key,BigInteger g, BigInteger q, BigInteger p){
        this.g = g;
        this.q = q;
        this.p = p;
        this.key = key;
    }
    public BigInteger getG() {
        return g;
    }

    public BigInteger getQ() {
        return q;
    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getKey() {
        return key;
    }
}
