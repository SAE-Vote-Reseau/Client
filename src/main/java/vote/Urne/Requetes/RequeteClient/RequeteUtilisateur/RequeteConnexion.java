package vote.Urne.Requetes.RequeteClient.RequeteUtilisateur;

import vote.Urne.Requetes.RequeteClient.Requete;

public class RequeteConnexion extends Requete {
    private static final long serialVersionUID = -4031012286226927617L;
    private String email;
    private String password;

    public RequeteConnexion(String email, String password){
        super("connect");
        this.email =email;
        this.password = password;
    }
}
