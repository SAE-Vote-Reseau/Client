package vote.Urne.Requetes.RequeteClient.RequeteAdmin;


import vote.Urne.Requetes.RequeteClient.Requete;


public class RequeteAddUser extends Requete {
    private static final long serialVersionUID = -4206407180651779866L;
    private String email;
    private String prenom;
    private String nom;
    private String motDePasse;
    private boolean estAdmin;
    private String ssid;

    public RequeteAddUser(String email, String prenom, String nom, String motDePasse, boolean estAdmin, String ssid) {
        super("addUser");

        this.email = email;
        this.prenom = prenom;
        this.nom = nom;
        this.motDePasse = motDePasse;
        this.estAdmin = estAdmin;
        this.ssid = ssid;
    }


}
