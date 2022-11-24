package vote.Urne;

import vote.crypto.KeyInfo;

import java.io.Serializable;

public class Sondage implements Serializable {

    private static final long serialVersionUID = 7339119561699635756L;
    private String consigne;

    private String choix1;
    private String choix2;
    private Integer resultat;
    private Integer nbVotant;
    private KeyInfo publicKeyInfo;
    //peut etre rajouter le stockage des votes ici

    public Sondage(String consigne, String choix1, String choix2, KeyInfo publicKeyInfo){
        this.consigne = consigne;
        this.choix1 = choix1;
        this.choix2 = choix2;
        this.resultat = resultat;
        this.publicKeyInfo = publicKeyInfo;
        this.nbVotant = 0;
    }


    public KeyInfo getPublicKeyInfo(){
        return publicKeyInfo;
    }

    public void setResultat(Integer resultat) {
        this.resultat = resultat;
    }

    public Integer getResultat(){
        return resultat;
    }

    public void setNbVotant(int x){
        this.nbVotant = x;
    }

    public Integer getNbVotant(){
        return this.nbVotant;
    }


    public String getConsigne() {
        return consigne;
    }

    public String getChoix1() {
        return choix1;
    }

    public String getChoix2() {
        return choix2;
    }

    @Override
    public String toString(){
        return consigne + ": " + choix1 + "/" + choix2 + (resultat == null ? ", le resultat n'est pas encore disponible":resultat);
    }

}
