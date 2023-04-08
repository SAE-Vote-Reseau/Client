package vote.Urne.Requetes.RequeteClient.RequeteUtilisateur;


import vote.Urne.Requetes.RequeteClient.Requete;

public class RequeteDeconnexion extends Requete {
    private static final long serialVersionUID = 1248828963788962307L;
    private String ssid;

    public RequeteDeconnexion(String ssid){
        super("deconect");
        this.ssid = ssid;
    }


}
