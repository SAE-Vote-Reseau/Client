package vote.Urne.Requete.RequeteClient.RequeteAdmin.RequeteEtat;



import vote.Urne.Requete.RequeteClient.Requete;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class RequetePublierResultat extends Requete {
    private static final long serialVersionUID = 8398689807929469595L;
    private String ssid;

    public RequetePublierResultat(String ssid) {
        super("publier_resultat");
        this.ssid = ssid;
    }


}
