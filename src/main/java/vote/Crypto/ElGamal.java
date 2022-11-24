package vote.Crypto;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

public class ElGamal {

    private static BigInteger randomMax(Random rng, BigInteger max){
        BigInteger v;

        do{ //On n'a pas le choix je crois
            v = new BigInteger(max.bitLength(),rng);
        }while(v.compareTo(max) > 0 );

        return v;
    }

    public static BigInteger[] keyGen(int nbBits){
        Random rng = new SecureRandom();
        BigInteger p;
        BigInteger q;
        do {
            q = BigInteger.probablePrime(nbBits, rng);
            p = BigInteger.valueOf(2).multiply(q).add(BigInteger.valueOf(1));
        }while (!p.isProbablePrime(100)); //schnorr prime: https://crypto.stackexchange.com/questions/9006/how-to-find-generator-g-in-a-cyclic-group

        BigInteger g;
        do {
            g = randomMax(rng,p.subtract(BigInteger.valueOf(1)));
        } while (g.modPow(q,p).compareTo(BigInteger.valueOf(1)) != 0);


        BigInteger sk = randomMax(rng,q.subtract(BigInteger.valueOf(1)));
        BigInteger pk = g.modPow(sk,p);

        return new BigInteger[]{p,g,q,sk,pk};
    }

    public static BigInteger[] encrypt(BigInteger m, BigInteger pk, BigInteger p, BigInteger g, BigInteger q){
        Random rng = new SecureRandom();
        BigInteger r = randomMax(rng,q.subtract(BigInteger.valueOf(1))); //cle ephemere

        BigInteger c1 = g.modPow(r,p); //g^r (mod p)
        BigInteger c2 = pk.modPow(r,p).multiply(g.modPow(m,p)); // m * pk^r (mod p)

        return new BigInteger[]{c1,c2};
    }

    private static int trouverMessage(BigInteger g, BigInteger m, int nbReponsePossible){
        for (int i= 0; i < nbReponsePossible; i++){
            if (g.pow(i).compareTo(m) == 0){
                return i;
            }
        }
        return -1;
    }

    public static int decrypt(BigInteger c1, BigInteger c2, BigInteger sk, BigInteger p,BigInteger g){
        BigInteger u = c1.modPow(sk,p).modInverse(p); // (c1 ^ sk)^-1
        return trouverMessage(g,c2.multiply(u).mod(p),2);
    }

    public static BigInteger[] Agreger(BigInteger[] c1,BigInteger[] c2, BigInteger pk){
        BigInteger c1Agreger = (c1[0].multiply(c2[0])).mod(pk);
        BigInteger c2Agreger = (c1[1].multiply(c2[1])).mod(pk);
        return new BigInteger[]{c1Agreger,c2Agreger};
    }

    public static void main(String[] argv){

        BigInteger[] keys = keyGen(100);

        BigInteger[] encrypted = encrypt(BigInteger.valueOf(1),keys[4],keys[0],keys[1],keys[2]);
        System.out.println(Arrays.toString(encrypted));

        int m = decrypt(encrypted[0],encrypted[1],keys[3],keys[0],keys[1]);
        System.out.println("message" + m);

        BigInteger[] encrypted2 = encrypt(BigInteger.valueOf(0),keys[4],keys[0],keys[1],keys[2]);
        int m2 = decrypt(encrypted2[0],encrypted2[1],keys[3],keys[0],keys[1]);
        System.out.println("message:" + m2);

BigInteger[] encrypted3 = Agreger(encrypted,encrypted2,keys[0]);
        int m3 = decrypt(encrypted3[0],encrypted3[1],keys[3],keys[0],keys[1]);
        System.out.println(m3);


    }
}
