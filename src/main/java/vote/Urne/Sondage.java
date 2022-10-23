package vote.Urne;

import java.io.Serial;
import java.io.Serializable;

public class Sondage implements Serializable {
    @Serial
    private static final long serialVersionUID = 7339119561699635756L;
    private final String consigne;
    private final String choix1;
    private final String choix2;
    //peut etre rajouter le stockage des votes ici

    public Sondage(String consigne, String choix1, String choix2){
        this.consigne = consigne;
        this.choix1 = choix1;
        this.choix2 = choix2;
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
        return consigne + ": " + choix1 + "/" + choix2;
    }

}
