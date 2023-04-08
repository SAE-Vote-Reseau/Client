package vote.Urne.Requetes.RequeteClient.RequeteAdmin.RequeteEtat;



import vote.Urne.Requetes.RequeteClient.Requete;

public class RequetePublierResultat extends Requete {
    private static final long serialVersionUID = 8398689807929469595L;
    private String ssid;

    public RequetePublierResultat(String ssid) {
        super("publier_resultat");
        this.ssid = ssid;
    }


}
