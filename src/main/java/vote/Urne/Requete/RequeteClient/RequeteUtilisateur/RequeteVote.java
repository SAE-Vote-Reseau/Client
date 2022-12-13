package vote.Urne.Requete.RequeteClient.RequeteUtilisateur;

import vote.Urne.Requete.RequeteClient.Requete;
import vote.crypto.Message;

public class RequeteVote extends Requete {
    private final Message voteChiffre;
    private final String ssId;
    private static final long serialVersionUID = -4214054064882241130L;

    public RequeteVote(Message voteChiffre, String ssId){
        super("vote");
        this.voteChiffre = voteChiffre;
        this.ssId = ssId;
    }

}
