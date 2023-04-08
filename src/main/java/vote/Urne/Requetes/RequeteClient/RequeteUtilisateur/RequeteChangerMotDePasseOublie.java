package vote.Urne.Requetes.RequeteClient.RequeteUtilisateur;



import vote.Urne.Requetes.RequeteClient.Requete;


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
