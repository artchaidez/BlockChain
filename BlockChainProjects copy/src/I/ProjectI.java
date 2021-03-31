/*
Most of this code came from this link, a reference from Prof Elliot.
https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa

Built from ProjectA. The site above included work, so earlier Projects kept it. Changes
were made, such as faking work with sleep(), to meet the requirements. Since my work
has difficulty, something that was discussed in lectures, this is another way to
adjust work.
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
*/

package I;

import com.google.gson.GsonBuilder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.security.MessageDigest;
import java.util.UUID;

public class ProjectI {

    public String hash;
    public String previousHash;
    public String data;
    public long timeStamp;
    private int nonce;
    public static int difficulty = 1;   //how tough the "work" is.
    public static ArrayList<ProjectI> blockchain = new ArrayList<>();
    UUID uuid;
    public int blockNum;
    public int loopDifficulty;  //another way to fake work

    public ProjectI(String data, String previousHash  ) {
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
                StringBuilder hexString = new StringBuilder();
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

    /* Difficulty can be adjusted to make work take longer. Add sleep for one second and
    * made the difficulty at 1. Now it takes around the same time as earlier Projects. We can
    * stop work after a certain amount of loops; however, this code also checks if mine is
    * valid. In most cases, blockchain will not be validated.*/
    public void mineBlock(int difficulty) throws InterruptedException {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring( 0, difficulty).equals(target)) {
            Thread.sleep(1000);
            nonce++;
            hash = calculateHash();
            //if (loopDifficulty < 3 ) break;
            //loopDifficulty++;
        }
        System.out.println("Block Mined! Hash is: " + hash);
    }

    public static void addBlock(ProjectI newBlock) throws InterruptedException {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }

    public static Boolean validChainCheck() {
        ProjectI currentBlock;
        ProjectI lastBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        for(int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            lastBlock = blockchain.get(i-1);
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("Current Hashes do not match!");
                return false;
            }
            if(!lastBlock.hash.equals(currentBlock.previousHash) ) {
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

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Mining block 0... ");
        addBlock(new ProjectI("Block 0: ", "0"));

        System.out.println("Mining block 1... ");
        addBlock(new ProjectI("Block 1: ",blockchain.get(blockchain.size()-1).hash));

        System.out.println("Mining block 2... ");
        addBlock(new ProjectI("Block 2: ",blockchain.get(blockchain.size()-1).hash));

        System.out.println("Mining block 3... ");
        addBlock(new ProjectI("Block 3: ",blockchain.get(blockchain.size()-1).hash));

        System.out.println("\nBlockchain is Valid: " + validChainCheck());

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        //String blockchainJson = StringUtil.getJson(blockchain);
        System.out.println("\nThe block chain: ");
        System.out.println(blockchainJson);
    }
}

