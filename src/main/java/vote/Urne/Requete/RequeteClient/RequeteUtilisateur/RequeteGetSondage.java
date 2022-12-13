package vote.Urne.Requete.RequeteClient.RequeteUtilisateur;

import vote.Urne.Requete.RequeteClient.Requete;



public class RequeteGetSondage extends Requete {

    private final String ssId;
    private static final long serialVersionUID = -880529969933408134L;
    public RequeteGetSondage(String ssid){
        super("getSondage");
        this.ssId = ssid;
    }

}
