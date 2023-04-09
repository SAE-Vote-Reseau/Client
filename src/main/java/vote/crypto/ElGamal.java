package vote.crypto;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import vote.crypto.Message;
import vote.crypto.KeyInfo;

public class ElGamal {

    private static BigInteger randomMax(Random rng, BigInteger max) {
        BigInteger v;

        do { // On n'a pas le choix je crois
            v = new BigInteger(max.bitLength(), rng);
        } while (v.compareTo(max) > 0);

        return v;
    }

    public static KeyInfo[] keyGen(int nbBits) {
        Random rng = new SecureRandom();
        BigInteger p;
        BigInteger q;
        do {
            q = BigInteger.probablePrime(nbBits, rng);
            p = BigInteger.valueOf(2).multiply(q).add(BigInteger.valueOf(1));
        } while (!p.isProbablePrime(100)); // schnorr prime:
                                           // https://crypto.stackexchange.com/questions/9006/how-to-find-generator-g-in-a-cyclic-group

        BigInteger g;
        do {
            g = randomMax(rng, p.subtract(BigInteger.valueOf(1)));
        } while (g.modPow(q, p).compareTo(BigInteger.valueOf(1)) != 0);

        BigInteger sk = randomMax(rng, q.subtract(BigInteger.valueOf(1)));
        BigInteger pk = g.modPow(sk, p);

        KeyInfo secret = new KeyInfo(sk, g, q, p);
        KeyInfo shared = new KeyInfo(pk, g, q, p);

        return new KeyInfo[] { secret, shared };
    }

    public static VerifiedMessage encrypt(BigInteger m, KeyInfo publicKeyInfo) throws NoSuchAlgorithmException {

        Random rng = new SecureRandom();
        BigInteger r = randomMax(rng, publicKeyInfo.getQ().subtract(BigInteger.valueOf(1))); // cle ephemere

        BigInteger c1 = publicKeyInfo.getG().modPow(r, publicKeyInfo.getP()); // g^r (mod p)
        BigInteger c2 = publicKeyInfo.getKey().modPow(r, publicKeyInfo.getP())
                .multiply(publicKeyInfo.getG().modPow(m, publicKeyInfo.getP())); // m * pk^r (mod p)

        // A = pi 0 dans le cas ou m = 0
        // B = pi 1

        ///////
        BigInteger rpB = randomMax(rng, publicKeyInfo.getQ().subtract(BigInteger.valueOf(1)));
        BigInteger challB = randomMax(rng, publicKeyInfo.getQ().subtract(BigInteger.valueOf(1)));

        BigInteger Ab = publicKeyInfo.getG().modPow(rpB, publicKeyInfo.getP())
                .multiply(c1.modPow(challB, publicKeyInfo.getP())).mod(publicKeyInfo.getP());


        BigInteger Bb = BigInteger.valueOf(0);
        if(m.equals(BigInteger.ONE)){
            Bb = publicKeyInfo.getKey().modPow(rpB, publicKeyInfo.getP()).multiply(c2.modPow(challB, publicKeyInfo.getP())).mod(publicKeyInfo.getP());
        }
        else {
            Bb = publicKeyInfo.getKey().modPow(rpB, publicKeyInfo.getP()).multiply(c2.divide(publicKeyInfo.getG()).modPow(challB, publicKeyInfo.getP())).mod(publicKeyInfo.getP());

        }
        ///////
        BigInteger w = randomMax(rng, publicKeyInfo.getQ().subtract(BigInteger.valueOf(1))); // ???
        BigInteger Aa = publicKeyInfo.getG().modPow(w, publicKeyInfo.getP());
        BigInteger Ba = publicKeyInfo.getKey().modPow(w, publicKeyInfo.getP());

        // Chall0
        BigInteger chall;
        if(m.equals(BigInteger.ONE)){
            chall = new BigInteger(
                c1.toString() + c2.toString() + Ab.toString() + Bb.toString() + Aa.toString() + Ba.toString());
        }else {
            chall = new BigInteger(
                c1.toString() + c2.toString() + Aa.toString() + Ba.toString() + Ab.toString() + Bb.toString());
        }

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(chall.toByteArray());
        
        BigInteger challA = new BigInteger(hash).mod(publicKeyInfo.getQ()).subtract(challB).mod(publicKeyInfo.getQ());

        // rep0
        BigInteger rpA;
        rpA = w.subtract(r.multiply(challA).mod(publicKeyInfo.getQ())).mod(publicKeyInfo.getQ());
        Proof proof;

        if (m.equals(BigInteger.ONE)) {
            proof = new Proof(rpB, rpA, challB, challA);
        } else {
            proof = new Proof(rpA, rpB, challA, challB);
        }

        return new VerifiedMessage(new Message(c1, c2), proof);
    }

    public static boolean proofIsValid(VerifiedMessage vm, KeyInfo publicKeyInfo) throws NoSuchAlgorithmException {
        Message m = vm.getM();
        Proof p = vm.getP();


        BigInteger A0 = publicKeyInfo.getG().modPow(p.getRep0(), publicKeyInfo.getP())
                .multiply(m.getC1().modPow(p.getChall0(), publicKeyInfo.getP())).mod(publicKeyInfo.getP()); //ok


        BigInteger B0 = publicKeyInfo.getKey().modPow(p.getRep0(), publicKeyInfo.getP())
                .multiply(m.getC2().modPow(p.getChall0(), publicKeyInfo.getP())).mod(publicKeyInfo.getP());//ok



        BigInteger A1 = publicKeyInfo.getG().modPow(p.getRep1(), publicKeyInfo.getP())
                .multiply(m.getC1().modPow(p.getChall1(), publicKeyInfo.getP())).mod(publicKeyInfo.getP());//ok


        BigInteger B1 = publicKeyInfo.getKey().modPow(p.getRep1(), publicKeyInfo.getP())
                .multiply(m.getC2().divide(publicKeyInfo.getG()).mod(publicKeyInfo.getP()).modPow(p.getChall1(), publicKeyInfo.getP())).mod(publicKeyInfo.getP());

        BigInteger h = new BigInteger(m.getC1().toString() + m.getC2().toString() + A0.toString() + B0.toString()
                + A1.toString() + B1.toString());
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        BigInteger toVerify = new BigInteger(md.digest(h.toByteArray())).mod(publicKeyInfo.getQ());
        BigInteger toCompare = p.getChall0().add(p.getChall1()).mod(publicKeyInfo.getQ());

        System.out.println();
        System.out.println("l:" + toVerify.toString());
        System.out.println("r:" + toCompare.toString());

        return toVerify.equals(toCompare);

    }

    private static int trouverMessage(BigInteger g, BigInteger m, BigInteger p, int nbReponsePossible) {
        for (int i = 0; i <= nbReponsePossible; i++) {
            if (g.modPow(BigInteger.valueOf(i), p).compareTo(m) == 0) {
                return i;
            }
        }
        return -1;
    }

    public static int decrypt(Message m, KeyInfo privateInfo, int nbParticipant) {
        BigInteger u = m.getC1().modPow(privateInfo.getKey(), privateInfo.getP()).modInverse(privateInfo.getP()); // (c1
                                                                                                                  // ^
                                                                                                                  // sk)^-1
        return trouverMessage(privateInfo.getG(), m.getC2().multiply(u).mod(privateInfo.getP()), privateInfo.getP(),
                nbParticipant);
    }

    private static BigInteger[] euclideEtendu(BigInteger a, BigInteger b) { // Ne sert a rien en fait, mais je laisse au
                                                                            // cas ou
        if (b.equals(BigInteger.valueOf(0))) {
            return new BigInteger[] { a, BigInteger.valueOf(1), BigInteger.valueOf(0) };
        }
        BigInteger[] duv = euclideEtendu(b, a.mod(b));
        BigInteger q = a.subtract(a.mod(b)).divide(b);

        return new BigInteger[] { duv[0], duv[2], duv[1].subtract(q.multiply(duv[2])) };
    }

    public static Message Agreger(Message m1, Message m2, BigInteger p) {
        BigInteger c1Agreger = (m1.getC1().multiply(m2.getC1())).mod(p);
        BigInteger c2Agreger = (m1.getC2().multiply(m2.getC2())).mod(p);
        return new Message(c1Agreger, c2Agreger);
    }

    public static void main(String[] argv) {

        KeyInfo[] keys = keyGen(100);
        System.out.println("sk:" + keys[0].getKey());
        try {
            VerifiedMessage encrypted = encrypt(BigInteger.valueOf(0), keys[1]);
            System.out.println(encrypted.toString());

            System.out.println(proofIsValid(encrypted, keys[1]));
        } catch (Exception e) {
            System.out.println("Auuugh: " + e.getMessage());
        }
        /*
         * int m = decrypt(encrypted,keys[0],1);
         * System.out.println("message" + m);
         * 
         * Message encrypted2 = encrypt(BigInteger.valueOf(1),keys[1]);
         * int m2 = decrypt(encrypted2,keys[0],1);
         * System.out.println("message:" + m2);
         * 
         * Message encrypted3 = Agreger(encrypted,encrypted2,keys[1].getP());
         * int m3 = decrypt(encrypted3,keys[0],2);
         * System.out.println(m3);
         * 
         * System.out.println("-----");
         * 
         * BigInteger[] arr =
         * euclideEtendu(BigInteger.valueOf(8),BigInteger.valueOf(5));
         * System.out.println(Arrays.toString(arr));
         * System.out.println(arr[1].multiply(BigInteger.valueOf(8)).add(arr[2].multiply
         * (BigInteger.valueOf(5))));
         */

    }
}
