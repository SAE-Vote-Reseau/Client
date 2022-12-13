package vote.Urne.Requete.RequeteClient.RequeteAdmin.RequeteEtat;


import vote.Urne.Requete.RequeteClient.Requete;


public class RequeteArreterSondage extends Requete {
    private static final long serialVersionUID = 2406585980453091613L;
    private String ssid;

    public RequeteArreterSondage(String ssid) {
        super("arreter_sondage");
        this.ssid = ssid;
    }
}
