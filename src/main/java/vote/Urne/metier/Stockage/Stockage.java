package vote.Urne.metier.Stockage;

import java.util.List;

public interface Stockage<T,Key> {
    void enregistrer(T t);
    void supprimer(Key id);
    void mettreAJour(T t);
    List<T> getAll();
    T get(Key id);
}
