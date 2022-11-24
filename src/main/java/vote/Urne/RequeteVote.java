package vote.Urne;

public class RequeteVote extends Requete {
    private String voteChiffre;
    private static final long serialVersionUID = -4214054064882241130L;

    public RequeteVote(String voteChiffre){
        super("vote");
        this.voteChiffre = voteChiffre;
    }

}
