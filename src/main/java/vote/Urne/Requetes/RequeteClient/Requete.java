package vote.Urne.Requetes.RequeteClient;

import java.io.Serializable;

public abstract class Requete implements Serializable {
    private static final long serialVersionUID = -880529969933408134L;

    private String prefix;

    public Requete(String prefix){
        this.prefix = prefix;
    }

   /* public abstract void repondre(BureauDeVote bureau, ObjectOutputStream out) throws IOException;*/
}
