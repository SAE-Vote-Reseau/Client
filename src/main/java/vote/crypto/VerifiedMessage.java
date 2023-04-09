package vote.crypto;

import java.io.Serializable;

public class VerifiedMessage implements Serializable{ // En fait c'est juste pour contenir le message et sa preuve pour eviter d'utiliser des tableau partout

    private Message m;
    private Proof p;
    private static final long serialVersionUID = 1311761525806698101L;



    public VerifiedMessage(Message m, Proof p){
        this.m = m;
        this.p = p;
    }

    public Message getM() {
        return m;
    }

    public Proof getP() {
        return p;
    }

    @Override
    public String toString() {
        return "VerifiedMessage [m=" + m + ", p=" + p + "]";
    }

    
}
