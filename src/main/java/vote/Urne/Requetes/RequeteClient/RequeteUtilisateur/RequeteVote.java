package vote.Urne.Requetes.RequeteClient.RequeteUtilisateur;

import java.io.IOException;
import java.io.ObjectOutputStream;

import vote.Urne.Requetes.RequeteClient.Requete;
import vote.crypto.Message;
import vote.crypto.VerifiedMessage;

public class RequeteVote extends Requete {
    private final VerifiedMessage voteChiffre;
    private final String ssId;
    private static final long serialVersionUID = -4214054064882241130L;

    public RequeteVote(VerifiedMessage voteChiffre, String ssId){
        super("vote");
        this.voteChiffre = voteChiffre;
        this.ssId = ssId;
    }
}
