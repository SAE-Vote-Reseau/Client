package vote.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class ElGamal {

    private static BigInteger randomMax(Random rng, BigInteger max){
        BigInteger v;

        do{ //On n'a pas le choix je crois
            v = new BigInteger(max.bitLength(),rng);
        }while(v.compareTo(max) > 0 );

        return v;
    }

    public static KeyInfo[] keyGen(int nbBits){
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

        KeyInfo secret = new KeyInfo(sk,g,q,p);
        KeyInfo shared = new KeyInfo(pk,g,q,p);

        return new KeyInfo[]{secret,shared};
    }

    public static Message encrypt(BigInteger m, KeyInfo publicKeyInfo){

        Random rng = new SecureRandom();
        BigInteger r = randomMax(rng,publicKeyInfo.getQ().subtract(BigInteger.valueOf(1))); //cle ephemere

        BigInteger c1 = publicKeyInfo.getG().modPow(r,publicKeyInfo.getP()); //g^r (mod p)
        BigInteger c2 = publicKeyInfo.getKey().modPow(r,publicKeyInfo.getP()).multiply(publicKeyInfo.getG().modPow(m,publicKeyInfo.getP())); // m * pk^r (mod p)

        return new Message(c1,c2);
    }

    private static int trouverMessage(BigInteger g, BigInteger m,BigInteger p, int nbReponsePossible){
        for (int i= 0; i <= nbReponsePossible; i++){
            if (g.pow(i).mod(p).compareTo(m) == 0){
                return i;
            }
        }
        return -1;
    }

    public static int decrypt(Message m, KeyInfo privateInfo ,int nbParticipant){
        BigInteger u = m.getC1().modPow(privateInfo.getKey(),privateInfo.getP()).modInverse(privateInfo.getP()); // (c1 ^ sk)^-1
        return trouverMessage(privateInfo.getG(),m.getC2().multiply(u).mod(privateInfo.getP()),privateInfo.getP(),nbParticipant);
    }

    public static Message Agreger(Message m1,Message m2, BigInteger p){
        BigInteger c1Agreger = (m1.getC1().multiply(m2.getC1())).mod(p);
        BigInteger c2Agreger = (m1.getC2().multiply(m2.getC2())).mod(p);
        return new Message(c1Agreger,c2Agreger);
    }

    public static void main(String[] argv){
        KeyInfo[] keys = keyGen(100);
        System.out.println("sk:" + keys[0].getKey());
        Message encrypted = encrypt(BigInteger.valueOf(1),keys[1]);
        System.out.println(encrypted);

        int m = decrypt(encrypted,keys[0],1);
        System.out.println("message" + m);

       Message encrypted2 = encrypt(BigInteger.valueOf(1),keys[1]);
        int m2 = decrypt(encrypted2,keys[0],1);
        System.out.println("message:" + m2);

        Message encrypted3 = Agreger(encrypted,encrypted2,keys[1].getP());
        int m3 = decrypt(encrypted3,keys[0],2);
        System.out.println(m3);


    }
}
