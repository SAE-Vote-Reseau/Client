package vote.Urne;

import vote.Urne.Requete;

public class RequeteVote extends Requete {
    private String voteChiffre;

    public RequeteVote(String voteChiffre){
        super("vote");
        this.voteChiffre = voteChiffre;
    }

}
