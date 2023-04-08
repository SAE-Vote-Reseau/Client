package vote.Urne.Requetes.RequeteClient.RequeteUtilisateur;

import vote.Urne.Requetes.RequeteClient.Requete;

public class RequeteEstConnecte extends Requete {
    private static final long serialVersionUID = -730437664822465754L;
    private String ssid;
    //cela deconnectera la personne
    public RequeteEstConnecte(String ssid) {
        super("est connecte");
        this.ssid = ssid;
    }


}
