package vote.Urne.Requetes.RequeteClient.RequeteUtilisateur;

import vote.Urne.Requetes.RequeteClient.Requete;

public class RequeteMotDePasseOublie extends Requete {
    private static final long serialVersionUID = -785501772790984993L;
    private String email;

    public RequeteMotDePasseOublie(String email){
        super("forgetPassword");
        this.email = email;
    }

}
