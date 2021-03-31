package J2;

import J.ProjectJ;
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

public class ProjectJ2 {

    public String hash;
    public String previousHash;
    public String data;
    public long timeStamp;
    private int nonce;

    public static int difficulty = 1;   //how tough the "work" is
    public static ArrayList<ProjectJ> blockchain = new ArrayList<>();
    UUID uuid;
    public int blockNum;
    public int loopDifficulty;

    public ProjectJ2(String data, String previousHash  ) {
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
        return ProjectJ.StringUtil.applySha256(previousHash + nonce + data );
    }

    /* Difficulty can be adjusted to make work take longer. Add sleep for one second and
     * made the difficulty at 1. Now it takes around the same time as earlier Projects. We can
     * stop work after a certain amount of loops; however, this code also checks if mine is
     * valid. In most cases, blockchain will not be validated.*/
    public void mineBlock(int difficulty) throws InterruptedException {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring( 0, difficulty).equals(target)) {
            //Thread.sleep(1000);
            nonce++;
            hash = calculateHash();
            //if (loopDifficulty < 3 ) break;
            //loopDifficulty++;
        }
        System.out.println("Block Mined! Hash is: " + hash);
    }

    public static void addBlock(ProjectJ newBlock) throws InterruptedException {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }

    public static Boolean validChainCheck() {
        ProjectJ currentBlock;
        ProjectJ lastBlock;
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

    public void run() throws InterruptedException {
        /* Print out that we are working on a block. */
        System.out.println("Checking if Block 0 exist... ");

        if (blockchain == null ) {
            System.out.println("There is no block 0...");
        } else {
            System.out.println("We are good to go!");
            System.out.println("Mining block 1... ");
            addBlock(new ProjectJ("Block 1: ", blockchain.get(blockchain.size() - 1).hash));

            System.out.println("Mining block 2... ");
            addBlock(new ProjectJ("Block 2: ", blockchain.get(blockchain.size() - 1).hash));

            System.out.println("Mining block 3... ");
            addBlock(new ProjectJ("Block 3: ", blockchain.get(blockchain.size() - 1).hash));
        }
    }

    public static void chainToFile (ArrayList blockArray) {
        try (FileWriter writer = new FileWriter("ProjectJ.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println("\nTurned blockchain into json string using the function!");
            gson.toJson(blockArray, writer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {

        ProjectJ myBlocks = new ProjectJ("Block 0: ", "0");
        addBlock(myBlocks);
        myBlocks.run();

        chainToFile(blockchain);

        SendChain s = new SendChain();
        s.run(args);
    }
}

class SendChain {

    String serverName = "localhost";
    int numProcesses = 3;
    static int processID = 0;

    public static final String ALGORITHM = "RSA";
    public static PublicKey publicKey;
    public static PrivateKey privateKey;

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

    public void sendChainToAll() throws NoSuchAlgorithmException, NoSuchProviderException {
        Socket sock;
        ObjectOutputStream toServer;
        PrintStream out;

        Gson gson = new Gson();
        ArrayList<ProjectJ> blocks = null;

        try (Reader reader = Files.newBufferedReader(Paths.get("ProjectJ.json"))) {
            //suggestion from classmate on D2L
            blocks = gson.fromJson(reader,
                    new TypeToken<ArrayList<ProjectJ>>() {}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            for(int i=0; i< numProcesses; i++){
                sock = new Socket(serverName, Ports.BlockchainServerPortBase + (i * 1000));
                //toServer = new ObjectOutputStream(sock.getOutputStream());
                out = new PrintStream(sock.getOutputStream());

                for (ProjectJ block : blocks) {
                    //toServer.writeObject(block.blockNum);
                    //toServer.writeObject(block.blockNum);
                    out.println("Block 0 hash is: " + block.hash);
                }
                out.flush();
                //toServer.flush();
                sock.close();
            }
        }catch (Exception x) {x.printStackTrace ();}
    }

    public void run(String[] args) throws Exception {

        System.out.println("\n---Arturo Chaidez's ProjectJ running---\n");
        processID = (args.length < 1) ? 0 : Integer.parseInt(args[0]);

        System.out.println("This is processID: " + processID + "\n");
        new Ports().setPorts();

        new Thread(new BCServer()).start();

        try{Thread.sleep(25000);}catch(Exception e){}
        if (processID == 0 ) sendChainToAll();
    }
}

class Ports {

    public static int PubKeyPortBase = 6050;
    public static int BlockchainServerPortBase = 6052;
    public static int PubKeyPort;
    public static int BlockchainServerPort;

    public void setPorts() {
        //by doing *1000, each process has their own port and can be multicast to
        //Process 0 is 6050, process 1 is 7050, 2 is 7050
        PubKeyPort = PubKeyPortBase + (SendChain.processID * 1000);
        BlockchainServerPort = BlockchainServerPortBase + (SendChain.processID * 1000);
    }
}

class BCWorker extends Thread {
    Socket bcSock;
    BCWorker(Socket s) {
        bcSock = s;}
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

class BCServer implements Runnable {

    public void run(){
        int queueLen = 6;   //stick with usual 6.
        Socket BCSock;
        System.out.println("Starting Pub Key input thread using " + Ports.BlockchainServerPort);
        try{
            ServerSocket servSock = new ServerSocket(Ports.BlockchainServerPort, queueLen);
            while (true) {
                BCSock = servSock.accept();
                new BCWorker(BCSock).start();
            }
        }catch (IOException ioe) {System.out.println(ioe);}
    }
}

class BcRecords {
    String blockchain;
    public void setBlockchain(String BC) {this.blockchain = BC;}
    public String getBlockchain(){ return this.blockchain;}
}

//Refracting some names here for the future.
class PubKeyWorker extends Thread {
    Socket PubKeySock;
    PubKeyWorker(Socket s) {
        PubKeySock = s;}
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