package N;

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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class ProjectN {

}

class BlockRecord{
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

class Blockchain {

    private static final int iFIRSTNAME = 0;
    private static final int iLASTNAME = 1;
    private static final int iDOB = 2;
    private static final int iSSNUM = 3;
    private static final int iDIAGNOSIS = 4;
    private static final int iTREATMENT = 5;
    private static final int iPRESCRIPTION = 6;
    //Not using linkedList like prof
    public static ArrayList recordList2 = new ArrayList<>();

    final static PriorityBlockingQueue<BlockRecord> ourPriorityQueue = new PriorityBlockingQueue<>(100, BlockTSComparator);

    public static Comparator<BlockRecord> BlockTSComparator = new Comparator<BlockRecord>()
    {
        @Override
        public int compare(BlockRecord b1, BlockRecord b2)
        {
            //System.out.println("In comparator");
            String s1 = b1.getTimeStamp();
            String s2 = b2.getTimeStamp();
            if (s1 == s2) {return 0;}
            if (s1 == null) {return -1;}
            if (s2 == null) {return 1;}
            return s1.compareTo(s2);
        }
    };

    public static int processID = 0;

    //need a constructor to take in argv to get to run
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

        chainToFile(recordList2);
        //chainToFile(recordList);

        System.out.println(recordList2.size());
        //System.out.println(recordList.size());

        new Ports().setPorts();
        new Thread(new UBCServer(ourPriorityQueue)).start();

        try{Thread.sleep(25000);}catch(Exception e){}
        SendChain ubcSend = new SendChain();
        ubcSend.sendUBCToAll();   //this will send the blocks
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

                ProjectL blockInfo = new ProjectL();

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

    public static void chainToFile(ArrayList blockArray) {

        String FILENAME = switch (processID) {
            case 1 -> "ProjectL1.json";
            case 2 -> "ProjectL2.json";
            default -> "ProjectL0.json";
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

//Below this is a from ProjectJ
class SendChain {

    String serverName = "localhost";
    int numProcesses = 3;


    public static final String ALGORITHM = "RSA";
    public static PublicKey publicKey;
    public static PrivateKey privateKey;

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

        Gson gson = new Gson();

        String FILENAME = switch (Blockchain.processID) {
            case 1 -> "ProjectL1.json";
            case 2 -> "ProjectL2.json";
            default -> "ProjectL0.json";
        };

        try (Reader reader = Files.newBufferedReader(Paths.get(FILENAME))) {
            //suggestion from classmate on D2L
            ArrayList<ProjectL> blocks = gson.fromJson(reader,
                    new TypeToken<ArrayList<ProjectL>>() {
                    }.getType());

            //this works in an awful way, print out the blocks
            String strBlocks = gson.toJson(blocks);

            //thought about using the array, but wont work and wont get points anyway
            //ArrayList newList = Blockchain.recordList2;

            try {
                for (int i = 0; i < numProcesses; i++) {
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
    }
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

//Prof placed worker within Server, to be able to use queue
class UBCServer<lockingQueue> implements Runnable {
    BlockingQueue<BlockRecord> queue;

    //this needs to be in run(); look at Prof bc.java
    UBCServer(BlockingQueue<BlockRecord> queue) {
        this.queue = queue; // Constructor binds our prioirty queue to the local variable.
    }

    public static Comparator<BlockRecord> BlockTSComparator = new Comparator<BlockRecord>() {
        @Override
        public int compare(BlockRecord b1, BlockRecord b2) {
            String s1 = b1.getTimeStamp();
            String s2 = b2.getTimeStamp();
            if (s1 == s2) { return 0; }
            if (s1 == null) { return -1; }
            if (s2 == null) { return 1; }
            return s1.compareTo(s2);
        }
    };

    public void run() {
        int queueLen = 6;   //stick with usual 6.
        Socket UBCSock;
        System.out.println("Starting unverified blockchain input thread using " + Ports.UBCServerPort);
        try {
            ServerSocket servSock = new ServerSocket(Ports.UBCServerPort, queueLen);
            while (true) {
                UBCSock = servSock.accept();
                new UBCWorker(UBCSock).start();
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    class UBCWorker extends Thread {
        Socket ubcSock;

        UBCWorker(Socket s) { ubcSock = s; }
        BlockRecord BR = new BlockRecord();

        //need to read in objects, line string lines for unverified blocks
        //we are sending over block objects at this point, for the processes can work on them
        //Bad practice, but keep it for now. It's how Prof did it
        public void run() {
            try {
                ObjectInputStream unverifiedIn = new ObjectInputStream(ubcSock.getInputStream());
                BR = (BlockRecord) unverifiedIn.readObject(); // Read in the UVB as an object
                System.out.println("Received UVB: " + BR.getTimeStamp() );
                queue.put(BR);
                ubcSock.close();
            } catch (IOException | InterruptedException | ClassNotFoundException x) {
                x.printStackTrace();
            }
        }
    }
}
