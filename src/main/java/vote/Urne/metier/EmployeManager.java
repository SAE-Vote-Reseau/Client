package vote.Urne.metier;

import vote.Urne.metier.Stockage.Stockage;
import vote.Urne.metier.Stockage.StockageEmployeScrub;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class EmployeManager {
    private Stockage<Employe,String> stockage = StockageEmployeScrub.getInstance();
    private static EmployeManager instance = null;

    private EmployeManager(){

    }

    public static EmployeManager getInstance() {
        if(instance == null){
            instance = new EmployeManager();
        }
        return instance;
    }

    public Employe creerEmploye(String email, String nom, String prenom, String motDePasse, boolean estAdmin){
        Employe e = new Employe(email,nom,prenom,hashSHA256(motDePasse),estAdmin);
        stockage.enregistrer(e);

        return e;
    }

    public byte[] hashSHA256(String toHash) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(toHash.getBytes(StandardCharsets.UTF_8));
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void mettreAJourEmploye(Employe e){
        stockage.mettreAJour(e);
    }

    public List<Employe> getAll(){
        return stockage.getAll();
    }

    public Employe getEmploye(String email){
        return stockage.get(email);
    }

    public void supprimerEmploye(String email){
        stockage.supprimer(email);
    }

}
