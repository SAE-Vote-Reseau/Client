package vote.Urne;

import vote.crypto.Message;

public class RequeteVote extends Requete{
    private final Message voteChiffre;
    private static final long serialVersionUID = -4214054064882241130L;

    public RequeteVote(Message voteChiffre){
        super("vote");
        this.voteChiffre = voteChiffre;
    }

}
