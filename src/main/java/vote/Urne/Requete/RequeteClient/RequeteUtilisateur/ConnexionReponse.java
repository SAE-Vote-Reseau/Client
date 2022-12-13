package vote.Urne.Requete.RequeteClient.RequeteUtilisateur;

import vote.Urne.metier.Employe;

import java.io.Serializable;

public class ConnexionReponse implements Serializable {
    private static final long serialVersionUID = 4049798952701211134L;

    public String getSsid() {
        return ssid;
    }


    private String ssid;

    public Employe getEmploye() {
        return employe;
    }

    private Employe employe;

    public ConnexionReponse(String ssid, Employe e){
        this.ssid = ssid;
        this.employe = e;
    }


    @Override
    public String toString() {
        return "ConnexionReponse{" +
                "ssid='" + ssid + '\'' +
                ", employe=" + employe +
                '}';
    }

}
