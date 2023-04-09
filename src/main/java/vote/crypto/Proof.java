package vote.crypto;

import java.io.Serializable;
import java.math.BigInteger;

public class Proof implements Serializable {
    private final BigInteger rep0;
    private final BigInteger rep1;
    private final BigInteger chall0;
    private final BigInteger chall1;
    private static final long serialVersionUID = 1311517355806698101L;


    public Proof(BigInteger rep0, BigInteger rep1, BigInteger chall0, BigInteger chall1){
        this.rep0 = rep0;
        this.rep1 = rep1;
        this.chall0 = chall0;
        this.chall1 = chall1;
    }

    public BigInteger getRep0() {
        return rep0;
    }

    public BigInteger getRep1() {
        return rep1;
    }

    public BigInteger getChall0() {
        return chall0;
    }

    public BigInteger getChall1() {
        return chall1;
    }

    @Override
    public String toString() {
        return "Proof [rep0=" + rep0 + ", rep1=" + rep1 + ", chall0=" + chall0 + ", chall1=" + chall1 + "]";
    }

}
