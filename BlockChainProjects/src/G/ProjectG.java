/*
Most of this code came from this link, a reference from Prof Elliot.
https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa

Implemented code from Prof Elliot BlockJ.java
https://mkyong.com/java/how-to-parse-json-with-gson/
http://www.java2s.com/Code/Java/Security/SignatureSignAndVerify.htm
https://www.mkyong.com/java/java-digital-signatures-example/ (not so clear)
https://javadigest.wordpress.com/2012/08/26/rsa-encryption-example/
https://www.programcreek.com/java-api-examples/index.php?api=java.security.SecureRandom
https://www.mkyong.com/java/java-sha-hashing-example/
https://stackoverflow.com/questions/19818550/java-retrieve-the-actual-value-of-the-public-key-from-the-keypair-object
https://www.java67.com/2014/10/how-to-pad-numbers-with-leading-zeroes-in-Java-example.html
https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.2/
*/

package G;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.crypto.Cipher;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class ProjectG {

    public static final String ALGORITHM = "RSA";
    public static PublicKey publicKey;
    public static PrivateKey privateKey;

    //From Prof Elliot's BlockJ.java
    public static void createKeyPair(long seed) throws Exception {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG", "SUN");
        rng.setSeed(seed);
        keyGenerator.initialize(1024, rng);
        KeyPair keys = keyGenerator.generateKeyPair();
        publicKey = keys.getPublic();
        privateKey = keys.getPrivate();
        //System.out.println("Keypair is: " + keys);
    }

    //Code from Prof Elliot BlockJ.java. Turned it into a function
    public static void pubKeyToFile (String pubKey) {
        try (FileWriter writer = new FileWriter("ProjectG.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(pubKey, writer);
            System.out.println("Turned public key to Json!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //From Prof Elliot's BlockJ.java. Changed some things to fit this project
    public static String ReadJSON(){
        System.out.println("\n-----Trying to read JSON-----\n");

        Gson gson = new Gson();

        try (Reader reader = new FileReader("ProjectG.json")) {
            return gson.fromJson(reader, new TypeToken<String>() {}.getType());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*it takes a string and applies SHA256 algorithm to it, and returns the generated
     * signature as a string. This will work for previous and current hash. */
    public static class StringUtil {
        public static String applySha256(String input){
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
                StringBuffer hexString = new StringBuffer();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            }
            catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* From Prof Elliot BlockJ.java. This won't be needed for actual project, prof
    * gave it to us for mini projects. -> changed parameter from PublicKey to
    * PrivateKey */
    public static byte[] encrypt(String text, PrivateKey key) {
        byte[] cipherText = null;
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    /* From Prof Elliot BlockJ.java. This won't be need for actual project, prof
     * gave it to us either for mini projects. -> changed parameter from
     * PrivateKey to PublicKey*/
    public static String decrypt(byte[] text, PublicKey key) {
        byte[] decryptedText = null;
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            decryptedText = cipher.doFinal(text);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        assert decryptedText != null;
        return new String(decryptedText);
    }

    public static void main (String[] args) throws Exception {

        createKeyPair(999);

        //Public Key needs to be turned into bytes, THEN string
        byte[] bytePubKey = publicKey.getEncoded();
        String strPubKey = Base64.getEncoder().encodeToString(bytePubKey);

        //Turn string into JSON
        pubKeyToFile(strPubKey);

        //get back string that was in JSON
        String jsonPubKey = ReadJSON();
        //System.out.println(strPubKey);
        //System.out.println("Print out bytes in string: \n" + jsonPubKey);

        //Turn string back to bytes
        byte[] jsonBytePubKey = Base64.getDecoder().decode(jsonPubKey);

        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(jsonBytePubKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PublicKey RestoredKey = keyFactory.generatePublic(pubKeySpec);
        //System.out.println(RestoredKey);

        //Check if the bytes before turning the json is the same
        //System.out.println(bytePubKey);
        //System.out.println(jsonBytePubKey);

        /* Encrypt the string we got back from the JSON. Signature is made
        * from function used in earlier Projects. Use privateKey to sign it*/
        String wow = StringUtil.applySha256("Will this work?");
        final byte[] encryptText = encrypt(wow,privateKey);

        final String decryptedText = decrypt(encryptText, RestoredKey);

        //Added extra encryption using SHA256 function
        System.out.println("The hash string/signature is: " + wow);
        System.out.println("The encrypted hash: " +
                Base64.getEncoder().encodeToString(encryptText));
        System.out.println("The decrypted hash: " + decryptedText );

        if (wow.equals(decryptedText)) {
            System.out.println("The hash and decrypted hash match!");
        } else {
            System.out.println("The hash and decrypted hash do not match....");
        }
    }
}
