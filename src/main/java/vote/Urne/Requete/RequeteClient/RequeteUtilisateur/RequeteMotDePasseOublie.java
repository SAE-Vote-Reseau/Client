package vote.Urne.Requete.RequeteClient.RequeteUtilisateur;

import vote.Urne.Requete.RequeteClient.Requete;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class RequeteMotDePasseOublie extends Requete {
    private static final long serialVersionUID = -785501772790984993L;
    private String email;

    public RequeteMotDePasseOublie(String email){
        super("forgetPassword");
        this.email = email;
    }

}
