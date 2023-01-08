package vote.Urne.Requete.RequeteClient.RequeteUtilisateur;



import vote.Urne.Requete.RequeteClient.Requete;
import vote.Urne.metier.Employe;
import vote.Urne.metier.EmployeManager;


import java.io.IOException;
import java.io.ObjectOutputStream;

public class RequeteChangePassword extends Requete {
    private static final long serialVersionUID = -8228560866132091777L;
    private String ssid;
    private String newPassword;
    //cela deconnectera la personne
    public RequeteChangePassword(String newPassword, String ssid) {
        super("changerMdp");
        this.ssid = ssid;
        this.newPassword = newPassword;
    }


}
