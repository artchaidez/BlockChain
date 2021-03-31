package L2;

import L.ProjectL;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.*;

public class ProjectL2 {
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
class Blockchain {

    public static ProjectL2 blockInfo = new ProjectL2();

    private static final int iFIRSTNAME = 0;
    private static final int iLASTNAME = 1;
    private static final int iDOB = 2;
    private static final int iSSNUM = 3;
    private static final int iDIAGNOSIS = 4;
    private static final int iTREATMENT = 5;
    private static final int iPRESCRIPTION = 6;
    //Not using linkedList like prof
    public static ArrayList recordList2 = new ArrayList<>();
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
        //s.runBlockchain(argv);

        //chainToFile(recordList2);
        //chainToFile(recordList);

        System.out.println("Blocks created: " + recordList2.size());
        //System.out.println(recordList.size());

        new Ports().setPorts();
        new Thread(new UBCServer()).start();

        try{Thread.sleep(25000);}catch(Exception e){}
        SendChain ubcSend = new SendChain();
        ubcSend.sendUBCToAll();
    }

    public void readBlockInput(String[] args) throws Exception {

        if (args.length > 1) System.out.println("Extra functionality requested in console \n");

        processID = (args.length < 1) ? 0 : Integer.parseInt(args[0]);

        int PubKeyPortBase = 6050 + processID;
        int BCPort = 6051 + processID;
        int UBCPort = 6052 + processID;

        System.out.println("ProcessID: " + processID + " PublicKeyPort: " + PubKeyPortBase +
                " Unverified blockchain port: " + UBCPort + " Blockchain port: " + BCPort );

        String FILENAME = switch (processID) {
            case 1 -> "BlockInput1.txt";
            case 2 -> "BlockInput2.txt";
            default -> "BlockInput0.txt";
        };

        System.out.println("Read BlockInput file: " + FILENAME);

        try {
            BufferedReader br = new BufferedReader(new FileReader(FILENAME));
            String[] strArray;    //to hold string it is reading
            String stringLineInput;
            UUID uuidForBlock;    //uuid for block

            int blockCount = 0;

            while ((stringLineInput = br.readLine()) != null) {

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

}

//From ProjectJ
class SendChain {

    String serverName = "localhost";
    int numProcesses = 3;

    public static final String ALGORITHM = "RSA";
    public static PublicKey publicKey;
    public static PrivateKey privateKey;
    private Object ArrayList;

    //tried doing key steps outside sendKeyToAll, get warnings and errors
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

    //UBC stands for unverified blockchain
    public void sendUBCToAll() {
        Socket sock;
        ObjectOutputStream toServer;
        PrintStream out;

        //get arraylist that contains all the block info
        //sending this will be java objects
        ArrayList blockInfo = Blockchain.recordList2;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(blockInfo);
        System.out.println("String: " + json);

        ProjectL2 blocks = Blockchain.blockInfo; //allows to use get methods

        try {
            for (int i = 0; i < numProcesses; i++) {
                sock = new Socket(serverName, Ports.UBCServerPortBase + (i * 1000));
                //toServer = new ObjectOutputStream(sock.getOutputStream());
                out = new PrintStream(sock.getOutputStream());

                //toServer.writeObject(ArrayList);
                //out.println(json); //only prints out [
                System.out.println(json);

                //toServer.flush();
                out.flush();
                sock.close();
            }
        } catch (Exception x) {
            x.printStackTrace();
        }

    }

    /* public void sendUBCToAll() {
        Socket sock;
        ObjectOutputStream toServer;
        PrintStream out;

        Gson gson = new Gson();

        //use switch statement to read correct file
        String FILENAME = switch (Blockchain.processID) {
            case 1 -> "ProjectL1.json";
            case 2 -> "ProjectL2.json";
            default -> "ProjectL0.json";
        };

        //Reading Json from disk. Doubt I figure a better way by due date :(
        try (Reader reader = Files.newBufferedReader(Paths.get(FILENAME))) {
            //suggestion from classmate on D2L
            ArrayList<ProjectL2> blocks = gson.fromJson(reader,
                    new TypeToken<ArrayList<ProjectL2>>() {
                    }.getType());

            //this works in an awful way, print out the blocks
            String strBlocks = gson.toJson(blocks);

            //thought about using the array, but wont work and wont get points anyway
            //ArrayList newList = Blockchain.recordList2;

            //Send patient name to each server!
            try {
                for (int i = 0; i < numProcesses; i++) {
                    //In class ports, each process has their own port to get a message
                    sock = new Socket(serverName, Ports.UBCServerPortBase + (i * 1000));
                    //toServer = new ObjectOutputStream(sock.getOutputStream());
                    out = new PrintStream(sock.getOutputStream());

                    //print out the whole block :(
                    out.println(strBlocks);


                    out.flush();
                    //toServer.flush();
                    sock.close();
                }
            } catch (Exception x) {
                x.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    } */

}

class Ports {

    public static int PubKeyPortBase = 6050;
    //BC stands for blockchain
    public static int BCServerPortBase = 6051;
    //UBC stands for unverified block chain
    public static int UBCServerPortBase = 6052;

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

//Refactoring some names here for the future
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
