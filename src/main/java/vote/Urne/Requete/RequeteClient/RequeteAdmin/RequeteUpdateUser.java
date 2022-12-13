package vote.Urne.Requete.RequeteClient.RequeteAdmin;


import vote.Urne.Requete.RequeteClient.Requete;


public class RequeteUpdateUser extends Requete {
    private String email;
    private String prenom;
    private String nom;
    private String motDePasse;
    private boolean estAdmin;

    private String ssid;
    private static final long serialVersionUID = 6751544680743635897L;

    /* l'email n'est pas modiable, car c'est sa clé primaire*/
    public RequeteUpdateUser(String email, String prenom, String nom, String motDePasse, Boolean estAdmin, String sessionId) {
        super("update");

        this.email = email;
        this.prenom = prenom;
        this.nom = nom;
        this.motDePasse = motDePasse;
        this.estAdmin = estAdmin;

        this.ssid = sessionId;
    }

}
