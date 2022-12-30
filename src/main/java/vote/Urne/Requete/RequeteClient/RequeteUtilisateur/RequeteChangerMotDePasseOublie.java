package vote.Urne.Requete.RequeteClient.RequeteUtilisateur;



import vote.Urne.Requete.RequeteClient.Requete;
import vote.Urne.metier.Employe;
import vote.Urne.metier.EmployeManager;


import java.io.IOException;
import java.io.ObjectOutputStream;

public class RequeteChangerMotDePasseOublie extends Requete {
    private static final long serialVersionUID = -3874105142477244763L;
    private String id;
    private String password;

    public RequeteChangerMotDePasseOublie(String id, String password){
        super("changeForgetPassword");
        this.id = id;
        this.password = password;
    }

}
