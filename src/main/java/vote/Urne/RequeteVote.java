package vote.Urne;

import java.math.BigInteger;

public class RequeteVote extends Requete {
    final private BigInteger[] voteChiffre;
    private static final long serialVersionUID = -4214054064882241130L;

    public RequeteVote(BigInteger[] voteChiffre){
        super("vote");
        this.voteChiffre = voteChiffre;
    }

}
