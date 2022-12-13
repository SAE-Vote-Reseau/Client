package vote.Urne.metier;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class Employe implements Serializable {
    private static final long serialVersionUID = -3716016828868854333L;
    private String email,nom,prenom;
    private byte[] motDePasse;
    boolean estAdmin;

    public Employe(String email,String nom,String prenom, byte[] motDePasse, boolean estAdmin){
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.motDePasse = motDePasse;
        this.estAdmin = estAdmin;
    }

    public String getEmail(){
        return email;
    }

    public byte[] getMotDePasse(){
        return motDePasse;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getNom() {
        return nom;
    }

    public boolean isEstAdmin() {
        return estAdmin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employe employe = (Employe) o;
        return Objects.equals(email, employe.email) && Objects.equals(nom, employe.nom) && Objects.equals(prenom, employe.prenom) && Objects.equals(motDePasse, employe.motDePasse) && Objects.equals(estAdmin,employe.estAdmin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, nom, prenom, motDePasse,estAdmin);
    }

    @Override
    public String toString(){
        return email + " " + prenom + " " + nom + " " + Arrays.toString(motDePasse) + " " +estAdmin;

    }
}
