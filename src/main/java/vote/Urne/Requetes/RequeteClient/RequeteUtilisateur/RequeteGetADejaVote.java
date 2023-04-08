package vote.Urne.Requetes.RequeteClient.RequeteUtilisateur;


import vote.Urne.Requetes.RequeteClient.Requete;

public class RequeteGetADejaVote extends Requete {
    private static final long serialVersionUID = -726741156287474558L;
    private String ssId;
    public RequeteGetADejaVote(String ssid){
        super("getADejaVote");
        this.ssId = ssid;
    }


}
