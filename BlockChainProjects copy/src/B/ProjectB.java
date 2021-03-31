/*
Most of this code came from this link, a reference from Prof Elliot.
https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa

Built from ProjectA2. What was added was to write blockchain into JSON and put it on disk.
Prof Elliot's BlockJ.java and BlockInputG.java was partially used and changed to accomplish
this project.
http://www.fredosaurus.com/notes-java/data/strings/96string_examples/example_stringToArray.html
https://beginnersbook.com/2013/12/linkedlist-in-java-with-example/
https://www.javacodegeeks.com/2013/07/java-priority-queue-priorityqueue-example.html

https://mkyong.com/java/how-to-parse-json-with-gson/
http://www.java2s.com/Code/Java/Security/SignatureSignAndVerify.htm
https://www.mkyong.com/java/java-digital-signatures-example/ (not so clear)
https://javadigest.wordpress.com/2012/08/26/rsa-encryption-example/
https://www.programcreek.com/java-api-examples/index.php?api=java.security.SecureRandom
https://www.mkyong.com/java/java-sha-hashing-example/
https://stackoverflow.com/questions/19818550/java-retrieve-the-actual-value-of-the-public-key-from-the-keypair-object
https://www.java67.com/2014/10/how-to-pad-numbers-with-leading-zeroes-in-Java-example.html
https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.2/

* */

package B;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.security.MessageDigest;
import java.util.UUID;

public class ProjectB {

    public String hash;
    public String previousHash;
    public String data;
    public long timeStamp;  //not required, an option for part A2
    private int nonce;
    public static int difficulty = 5;    //how tough the "work" is
    public static ArrayList<ProjectB> blockchain = new ArrayList<>();
    public UUID uuid;
    public int blockNum;

    public ProjectB(String data, String previousHash  ) {
        this.uuid = UUID.randomUUID();
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
        this.blockNum = blockchain.size();
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

    public String calculateHash() {
        //what Prof is requiring for the concatenation
        return StringUtil.applySha256(previousHash + nonce + data );
    }

    //Not Needed until ProjectI.
    /* Difficulty can be adjusted to make work take longer. Professor wants this to be low and use
     * sleep() to take fake real work. For now, leave this at 2. -> changed to 1 -> wrong as it
     * Takes too long. Simplify Data string in main -> I was concatenating time, not hash which
     * is why it took so long. Increasing difficultly */
    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined! Hash is: " + hash);
    }

    public static void addBlock(ProjectB newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }

    //Not Needed until ProjectI.
    public static Boolean isChainValid() {
        ProjectB currentBlock;
        ProjectB previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        for(int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("Current Hashes do not match!");
                return false;
            }
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("Previous Hashes do not match!");
                return false;
            }
            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("This block has yet to be mined!");
                return false;
            }
        }
        return true;
    }

    //Code from Prof Elliot BlockJ.java. Turned it into a function
    public static void chainToFile (ArrayList blockArray) {
        try (FileWriter writer = new FileWriter("ProjectB.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(blockArray, writer);
            System.out.println("Turned blockchain into json using the function!");
            //System.out.println("Turned " + blockArray + " into json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        System.out.println("Mining block 0... ");
        addBlock(new ProjectB("Block 0: ", "0"));

        System.out.println("Mining block 1... ");
        addBlock(new ProjectB("Block 1: ",blockchain.get(blockchain.size()-1).hash));

        System.out.println("Mining block 2... ");
        addBlock(new ProjectB("Block 2: ",blockchain.get(blockchain.size()-1).hash));

        System.out.println("Mining block 3... ");
        addBlock(new ProjectB("Block 3: ",blockchain.get(blockchain.size()-1).hash));

        System.out.println("\nBlockchain is Valid: " + isChainValid());

        //this is needed for part B+C? Keep it for now.
        //String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String blockchainJson = gson.toJson(blockchain);
        System.out.println("\nThe block chain: ");

        System.out.println(blockchainJson);

        chainToFile(blockchain);

        //Works! From Prof Elliot's BlockJ.java -> turned this into a function chainToFile
        /*
        try (FileWriter writer = new FileWriter("blockRecord.json")) {
            gson.toJson(blockchain, writer);
            System.out.println("Turned blockchain into json");
        } catch (IOException e) {
            e.printStackTrace();
        }   */
    }
}