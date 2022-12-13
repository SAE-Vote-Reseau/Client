package vote.Urne.metier.Stockage;

import vote.Urne.metier.Employe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StockageEmployeScrub implements Stockage<Employe,String> {
    private ArrayList<Employe> scrub;
    private static StockageEmployeScrub instance = null;

    private StockageEmployeScrub(){
        scrub = new ArrayList<>();
    }

    static public StockageEmployeScrub getInstance(){
        if(instance == null){
            instance = new StockageEmployeScrub();
        }
        return  instance;
    }

    @Override
    public void enregistrer(Employe employe) {
        scrub.add(employe);
    }

    @Override
    public void supprimer(String email) {
        scrub.remove(get(email));
    }

    @Override
    public void mettreAJour(Employe employe) {
        supprimer(employe.getEmail());
        enregistrer(employe);
    }

    @Override
    public List<Employe> getAll() {
        return scrub;
    }

    @Override
    public Employe get(String email) {
        List<Employe> l = scrub.stream().filter((e) -> e.getEmail().equals(email)).collect(Collectors.toList()); //il n'y en a qu'un max
        return l.size() == 0 ? null: l.get(0);
    }

}
