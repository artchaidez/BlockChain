/*--------------------------------------------------------
1. Arturo Chaidez 10/4/2020

2. Java version used:
openjdk version "15" 2020-09-15
OpenJDK Runtime Environment (build 15+36-1562)
OpenJDK 64-Bit Server VM (build 15+36-1562, mixed mode, sharing)

3. Precise command-line compilation examples / instructions:
> javac Blockchain.java

4. Precise examples / instructions to run this program:
Run in separate command windows in this order. Program sleeps for 25 seconds, to give time
to start each process
> java Blockchain 0
> java Blockchain 1
> java Blockchain 2

NOTE: I ran my code using IntelliJ run configuration. It is set up to start all 3 processes.

5. List of files needed for running the program.

1) Blockchain.java

5. Notes:
This is an incomplete blockchain. It reads the BlockInputs and no ledger.
My code does not go far enough to create a ledger.
It prints out unverified blocks as strings. This is pretty much a
sloppy unfinished mini ProjectL, with some other unused code from previous projects.
I know this code is needed but I did not get to these steps (I stopped around project M).
It is hard for me to make excuses why I didn't finish, I am the one who signed up for
a huge workload this semester. However, I am impressed with myself how much I accomplished
and how much I learned.

Also, UBC stands for unverified blockchain, BC stands for blockchain.
These are used throughout code
----------------------------------------------------------

Sources used in Prof Elliot 4 files he gave us:
https://mkyong.com/java/how-to-parse-json-with-gson/
http://www.java2s.com/Code/Java/Security/SignatureSignAndVerify.htm
https://www.mkyong.com/java/java-digital-signatures-example/ (not so clear)
https://javadigest.wordpress.com/2012/08/26/rsa-encryption-example/
https://www.programcreek.com/java-api-examples/index.php?api=java.security.SecureRandom
https://www.mkyong.com/java/java-sha-hashing-example/
https://stackoverflow.com/questions/19818550/java-retrieve-the-actual-value-of-the-public-key-from-the-keypair-object
https://www.java67.com/2014/10/how-to-pad-numbers-with-leading-zeroes-in-Java-example.html
https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.2/
https://www.quickprogrammingtips.com/java/how-to-generate-sha256-hash-in-java.html  @author JJ
https://dzone.com/articles/generate-random-alpha-numeric  by Kunal Bhatia  Â·  Aug. 09, 12 Â· Java Zone
http://www.javacodex.com/Concurrency/PriorityBlockingQueue-Example

This was a reference given by Prof Elliot, I used it in the projects and broke down the
blockchain well for me.
https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa

*/


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

class BlockchainInfo {
    String blockNum;
    String timeStamp;
    String VerificationProcessID;
    String previousHash;
    UUID uuid;
    public String firstName;
    String lastName;
    String ssNum;
    String DOB;
    String RandomSeed;
    String correctHash;
    String diagnosis;
    String treatment;
    String prescription;

    public String getBlockNum() {return blockNum;}
    public void setBlockNum(String blockID){this.blockNum = blockID;}

    public String getTimeStamp() {return timeStamp;}
    public void setTimeStamp(String timeStamp){this.timeStamp = timeStamp;}

    public String getVerificationProcessID() {return VerificationProcessID;}
    public void setVerificationProcessID(String VID){this.VerificationProcessID = VID;}

    public String getPreviousHash() {return this.previousHash;}
    public void setPreviousHash (String previousHash){this.previousHash = previousHash;}

    public UUID getUUID() {return uuid;}
    public void setUUID (UUID number){this.uuid = number;}

    public String getLastName() {return lastName;}
    public void setLastName(String lastName){this.lastName = lastName;}

    public String getFirstName() {return firstName;}
    public void setFirstName(String firstName){this.firstName = firstName;}

    public String getSsNum() {return ssNum;}
    public void setSsNum(String socialSec){this.ssNum = socialSec;}

    public String getDOB() {return DOB;}
    public void setDOB (String date){this.DOB = date;}

    public String getDiagnosis() {return diagnosis;}
    public void setDiagnosis(String diagnosis){this.diagnosis = diagnosis;}

    public String getTreatment() {return treatment;}
    public void setTreatment(String treatment){this.treatment = treatment;}

    public String getPrescription() {return prescription;}
    public void setPrescription(String drugs){this.prescription = drugs;}

    public String getRandomSeed() {return RandomSeed;}
    public void setRandomSeed (String seed){this.RandomSeed = seed;}

    public String getCorrectHash() {return correctHash;}
    public void setCorrectHash(String correctHash){this.correctHash = correctHash;}

}

//From Prof Elliot's BlockInputG.java.
public class Blockchain {

    private static final int iFIRSTNAME = 0;
    private static final int iLASTNAME = 1;
    private static final int iDOB = 2;
    private static final int iSSNUM = 3;
    private static final int iDIAGNOSIS = 4;
    private static final int iTREATMENT = 5;
    private static final int iPRESCRIPTION = 6;

    public static ArrayList recordList2 = new ArrayList<>();
    //trying with LinkedList, same issue
    //public static LinkedList recordList = new LinkedList<ProjectL>();

    public static int processID = 0;

    public Blockchain(String[] argv) {
    }

    public static void main(String[] argv) throws Exception {

        Blockchain createBC = new Blockchain(argv);
        try{
            System.out.println("Trying to read BlockInput file\n");
            createBC.readBlockInput(argv);
            System.out.println("Read BlockInput file");
        } catch (Exception ignored) {}

        chainToFile(recordList2);
        //chainToFile(recordList);

        System.out.println(recordList2.size());
        //System.out.println(recordList.size());

        new Ports().setPorts();

        //TODO: implement blockchain server and public key server
        new Thread(new UBCServer()).start();

        try{Thread.sleep(25000);}catch(Exception e){}
        SendChain ubcSend = new SendChain();
        ubcSend.sendUBCToAll();
    }

    public void readBlockInput(String[] args) throws Exception {

        if (args.length > 1) System.out.println("Extra functionality requested in console \n");

        processID = (args.length < 1) ? 0 : Integer.parseInt(args[0]);

        int PubKeyPortBase = 4710 + processID;
        int BCPort = 4820 + processID;
        int UBCPort = 4930 + processID;

        System.out.println("ProcessID: " + processID + " PublicKeyPort: " + PubKeyPortBase +
                " Unverified blockchain port: " + UBCPort + " Blockchain port: " + BCPort );

        //TODO: All files together in one .txt file
        String FILENAME = switch (processID) {
            case 1 -> "BlockInput1.txt";
            case 2 -> "BlockInput2.txt";
            default -> "BlockInput0.txt";
        };

        System.out.println("Read BlockInput file: " + FILENAME);

        try {
            BufferedReader brInfo = new BufferedReader(new FileReader(FILENAME));
            String[] strArray;
            String stringLineInput;
            UUID uuidForBlock;

            int blockCount = 0;

            while ((stringLineInput = brInfo.readLine()) != null) {

                BlockchainInfo blockInfo = new BlockchainInfo();

                try {
                    Thread.sleep(1001);
                } catch (InterruptedException e) {
                }
                Date date = new Date();
                String T1 = String.format("%1$s %2$tF.%2$tT", "", date);
                String strTimeStamp = T1 + "." + processID;
                System.out.println("Timestamp: " + strTimeStamp);
                blockInfo.setTimeStamp(strTimeStamp);

                uuidForBlock = UUID.randomUUID();

                blockInfo.setBlockNum(String.valueOf(blockCount));
                blockInfo.setUUID(uuidForBlock);
                strArray = stringLineInput.split(" +");
                blockInfo.setFirstName(strArray[iFIRSTNAME]);
                blockInfo.setLastName(strArray[iLASTNAME]);
                blockInfo.setSsNum(strArray[iSSNUM]);
                blockInfo.setDOB(strArray[iDOB]);
                blockInfo.setDiagnosis(strArray[iDIAGNOSIS]);
                blockInfo.setTreatment(strArray[iTREATMENT]);
                blockInfo.setPrescription(strArray[iPRESCRIPTION]);

                recordList2.add(blockInfo);
                blockCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO: Send json straight to ports
    public static void chainToFile(ArrayList blockArray) {

        String FILENAME = switch (processID) {
            case 1 -> "Blockchain0.json";
            case 2 -> "Blockchain1.json";
            default -> "Blockchain2.json";
        };

        try (FileWriter writer = new FileWriter(FILENAME)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println("\nTurned blockchain into json string using the function!");
            gson.toJson(blockArray, writer);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

//From ProjectJ
class SendChain {

    String serverName = "localhost";
    int numProcesses = 3;

    public static final String ALGORITHM = "RSA";

    public static PublicKey publicKey;
    public static PrivateKey privateKey;

    public static void createKeyPair(long seed) throws Exception {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        //Prof uses SHA1PRNG and SUN, just stick with these
        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG", "SUN");
        /*setSeed is random generator based on bytes given, but allows you to
         * reproduce results later */
        //I do not really understand why this would matter for us or the blockchain?
        rng.setSeed(seed);
        /* Parameters for initialize are key size and source of randomness*/
        keyGenerator.initialize(1024, rng);
        //at last, generate the actual keys
        KeyPair keys = keyGenerator.generateKeyPair();
        publicKey = keys.getPublic();
        privateKey = keys.getPrivate();
        //I want to print and see what this looks like
        //System.out.println("Keypair is: " + keys);

    }

    //From ProjectH and ProjectL
    //UBC stands for unverified blockchain
    public void sendUBCToAll() {
        Socket sock;
        PrintStream out;

        Gson gson = new Gson();

        String FILENAME = switch (Blockchain.processID) {
            case 1 -> "Blockchain0.json";
            case 2 -> "Blockchain1.json";
            default -> "Blockchain2.json";
        };

        try (Reader reader = Files.newBufferedReader(Paths.get(FILENAME))) {
            //suggestion from classmate on D2L
            ArrayList<BlockchainInfo> blocks = gson.fromJson(reader,
                    new TypeToken<ArrayList<BlockchainInfo>>() {
                    }.getType());

            String strBlocks = gson.toJson(blocks);

            //thought about using the array, but wont work and wont get points anyway
            //ArrayList newList = Blockchain.recordList2;

            try {
                for (int i = 0; i < numProcesses; i++) {
                    sock = new Socket(serverName, Ports.UBCServerPortBase + (i * 1000));
                    out = new PrintStream(sock.getOutputStream());

                    out.println(strBlocks);

                    /*Its not going through every name in the json, stops after first block.
                    Did a similar thing in ProjectC and it worked, must be
                    ProjectL/BlockchainInfo */
                    /*
                    for (ProjectL block : blocks) {
                        //works, but still only printing first block only
                        out.println("Name using method: " + block.getFirstName());
                        //tried making a constructor like my ProjectC, does not work either
                        //out.println("Name using constructor " + block.firstName);
                        out.flush();
                        //out.println("Name using block: " + block.firstName);
                    } */

                    out.flush();
                    sock.close();
                }
            } catch (Exception x) {
                x.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

//From ProjectH
class Ports {

    public static int PubKeyPortBase = 4710;
    //BC stands for blockchain
    public static int BCServerPortBase = 4820;
    //UBC stands for unverified block chain
    public static int UBCServerPortBase = 4930;

    public static int PubKeyPort;
    public static int BlockchainServerPort;
    public static int UBCServerPort;

    public void setPorts() {
        //by doing *1000, each process has their own port and can be multicast to
        //Process 0 is 6050, process 1 is 7050, 2 is 7050 - for PubKeyPort
        PubKeyPort = PubKeyPortBase + (Blockchain.processID * 1000);
        BlockchainServerPort = BCServerPortBase + (Blockchain.processID * 1000);
        UBCServerPort =  UBCServerPortBase + (Blockchain.processID * 1000);
    }
}

//New Server for unverified blocks
class UBCServer implements Runnable {

    public void run(){
        int queueLen = 6;   //stick with usual 6.
        Socket UBCSock;
        //Make sure its the right ServerPort
        System.out.println("Starting unverified blockchain input thread using " + Ports.UBCServerPort);
        try{
            ServerSocket servSock = new ServerSocket(Ports.UBCServerPort, queueLen);
            while (true) {
                UBCSock = servSock.accept();
                new UBCWorker(UBCSock).start();
            }
        }catch (IOException ioe) {System.out.println(ioe);}
    }
}

//New worker for unverified blocks
class UBCWorker extends Thread {
    Socket ubcSock;
    UBCWorker(Socket s) {
        ubcSock = s;
    }
    public void run(){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(ubcSock.getInputStream()));
            String line = in.readLine();
            //show messages received from all three processes
            System.out.println(line);
            ubcSock.close();
        } catch (IOException x){x.printStackTrace();}
    }
}

class BCServer implements Runnable {

    public void run(){
        int queueLen = 6;   //stick with usual 6.
        Socket BCSock;
        //Make sure its the right ServerPort
        System.out.println("Starting Blockchain input thread using " + Ports.BlockchainServerPort);
        try{
            ServerSocket servSock = new ServerSocket(Ports.BlockchainServerPort, queueLen);
            while (true) {
                BCSock = servSock.accept();
                new BCWorker(BCSock).start();
            }
        }catch (IOException ioe) {System.out.println(ioe);}
    }
}

class BCWorker extends Thread {
    Socket bcSock;
    BCWorker(Socket s) {
        bcSock = s;
    }
    public void run(){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(bcSock.getInputStream()));
            String line = in.readLine();
            //show messages received from all three processes
            System.out.println(line);
            bcSock.close();
        } catch (IOException x){x.printStackTrace();}
    }
}

class PubKeyServer implements Runnable {

    public void run(){
        int queueLen = 6;   //stick with usual 6.
        Socket PubKeySock;
        //removed Integer.toString for Ports.SayHelloPort, works fine
        System.out.println("Starting Pub Key input thread using " + Ports.PubKeyPort);
        try{
            ServerSocket servSock = new ServerSocket(Ports.PubKeyPort, queueLen);
            while (true) {
                PubKeySock = servSock.accept();
                new PubKeyWorker(PubKeySock).start();
            }
        }catch (IOException ioe) {System.out.println(ioe);}
    }
}


class PubKeyWorker extends Thread {
    Socket PubKeySock;
    PubKeyWorker(Socket s) {
        PubKeySock = s;
    }
    public void run(){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(PubKeySock.getInputStream()));
            String line = in.readLine();
            //show messages received from all three processes
            System.out.println(line);
            PubKeySock.close();
        } catch (IOException x){x.printStackTrace();}
    }
}

/* Below here is other code from other projects I know I need. Not all projects were added
* as some are not to be used in the real project (encrypt and decrypt), or used in working code.
*  */

/* This code contains a way to create signatures, hashes, and work (mineblock).
* Still need to refractor this to work with the above code
* */

class ProjectI {

    public String hash;
    public String previousHash;
    public String data;
    public int nonce;
    public int loopDifficulty;  //another way to fake work
    //reduced difficulty and implemented sleep to fake work
    public static int difficulty = 1;   //how tough the "work" is.
    public static ArrayList<ProjectI> blockchain = new ArrayList<>();

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

    /* Should be called after after work is done? */
    public static Boolean validChainCheck() {
        ProjectI currentBlock;
        ProjectI lastBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        for(int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            lastBlock = blockchain.get(i-1);
            //TODO: Change repeated if statements to functions
            //compare registered hash and calculated hash:
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("Current Hashes do not match!");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!lastBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("Previous Hashes do not match!");
                return false;
            }
            //check if current hash has been solved
            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("This block has yet to be mined!");
                return false;
            }
        }
        return true;
    }

}