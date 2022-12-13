package vote.Urne.Requete.RequeteClient.RequeteAdmin;

import vote.Urne.Requete.RequeteClient.Requete;

public class RequeteGetAllUsers extends Requete {
    private static final long serialVersionUID = -1005850150066274606L;
    private String ssid;

    public RequeteGetAllUsers(String ssid) {
        super("getAll");
        this.ssid = ssid;
    }


}
