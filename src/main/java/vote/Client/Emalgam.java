package vote.Client;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

public class Emalgam {


    static Random rnd = new Random();
    public static ArrayList<BigInteger> keyGeneration(){
        ArrayList<BigInteger> keys = new ArrayList<>(4);
        BigInteger p = BigInteger.valueOf(0);
        BigInteger g = BigInteger.probablePrime(16,rnd);
        BigInteger x = BigInteger.probablePrime(16,rnd);
        BigInteger p2= BigInteger.valueOf(0);
        while (!p2.isProbablePrime(100)){
            p= BigInteger.probablePrime(16,rnd);
            p2 = (p.multiply(BigInteger.valueOf(2))).add(BigInteger.valueOf(1)) ;
        }

        int pInt = p.intValue();
        while (g.compareTo(p2.subtract(BigInteger.valueOf(1)))>0 && !g.pow( pInt).equals(BigInteger.valueOf(1).mod(p2))){
            g= BigInteger.probablePrime(16,rnd);
        }

        while (x.compareTo(p.subtract(BigInteger.valueOf(1)))>0){
            x= BigInteger.probablePrime(16,rnd);
        }
        int xInt= x.intValue();
        BigInteger h = (g.pow(xInt)).mod(p2);
        keys.add(p2);
        keys.add(g);
        keys.add(h);
        keys.add(x);
        return keys;
    }

    public static ArrayList<BigInteger> chiffrer(int m, ArrayList<BigInteger> clePublique){
        ArrayList<BigInteger> chiffre = new ArrayList<>(2);
        BigInteger p = clePublique.get(0);
        BigInteger g = clePublique.get(1);
        BigInteger h = clePublique.get(2);
        BigInteger pPrime = (p.subtract(BigInteger.valueOf(1))).divide(BigInteger.valueOf(2));
        BigInteger r = BigInteger.probablePrime(16,rnd);
        while (r.compareTo(pPrime.subtract(BigInteger.valueOf(1)))>0){
            r = BigInteger.probablePrime(16,rnd);
        }
        int rInt = r.intValue();
        BigInteger u = (g.pow(rInt)).mod(p);
        BigInteger v = ((g.pow(m)).multiply(h.pow(rInt))).mod(p);
        chiffre.add(u);
        chiffre.add(v);
        return chiffre;
    }

    public static ArrayList<BigInteger> getPublique(ArrayList<BigInteger> cles){
        ArrayList<BigInteger> publique = new ArrayList<>(cles);
        publique.remove(publique.size()-1);
        return publique;
    }

}
