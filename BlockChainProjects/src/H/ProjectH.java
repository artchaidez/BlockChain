/* This project combines ProjectF and ProjectG.
javac src/H/ProjectH.java
java src/H/ProjectH.java 0

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

Prof Elliot bc.java
http://www.javacodex.com/Concurrency/PriorityBlockingQueue-Example
* */

package H;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.Base64;

public class ProjectH {
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

    //Note: it is sending the same object
    public void sendKeyToAll() throws NoSuchAlgorithmException, NoSuchProviderException {
        Socket sock;
        //changed this from PrintStream to ObjectOutputStream
        ObjectOutputStream toServer;
        PrintStream out;

        PublicKeyRecords pubKeyR = new PublicKeyRecords();
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG", "SUN");
        rng.setSeed(999);
        keyGenerator.initialize(1024, rng);
        KeyPair keys = keyGenerator.generateKeyPair();
        //publicKey = keys.getPublic(); //probably not necessary?
        //privateKey = keys.getPrivate();

        String strPubKey = Base64.getEncoder().encodeToString(keys.getPublic().getEncoded());
        pubKeyR.setProcessID(processID);
        pubKeyR.setPubKey(strPubKey);

        try{
            for(int i=0; i< numProcesses; i++){
                sock = new Socket(serverName, Ports.PubKeyPortBase + (i * 1000));
                //out = new PrintStream(sock.getOutputStream());

                //change public key to JSON and send it to each process
                //Use StringWriter, not FileWriter
                StringWriter writer = new StringWriter();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(strPubKey, writer);
                String jsonKey = writer.toString();

                toServer = new ObjectOutputStream(sock.getOutputStream());
                toServer.writeObject(jsonKey);
                //toServer.writeInt(processID);
                //out.println(writer);
                //out.println("\n" + processID);
                //out.flush();
                toServer.flush();
                sock.close();
            }
        }catch (Exception x) {x.printStackTrace ();}
    }

    public static void main (String[] args) throws Exception {

        //generate key pair
        /* Prof said to use a class member to store PublicKeys. A class member for
        * storing three public keys along with the associated process ID number.*/
        //Tried doing steps outside SendKey, errors returned such as:
        //WARNING: An illegal reflective access operation has occurred
        /*
        createKeyPair(999);
        PublicKeyRecords pubKeyR = new PublicKeyRecords();
        String strPubKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        //place ID and public key in class to store it
        pubKeyR.setProcessID(processID);
        pubKeyR.setPubKey(strPubKey); */

        ProjectH s = new ProjectH();
        s.run(args);
    }

    public void run(String[] args) throws Exception {

        System.out.println("\n---Arturo Chaidez's ProjectH running---\n");
        processID = (args.length < 1) ? 0 : Integer.parseInt(args[0]);

        System.out.println("This is processID: " + processID + "\n");
        new Ports().setPorts();
        new Thread(new PubKeyServer()).start();

        try{Thread.sleep(20000);}catch(Exception e){}
        sendKeyToAll();
    }
}

class Ports {

    public static int PubKeyPortBase = 6050;
    public static int PubKeyPort;

    public void setPorts() {
        //by doing *1000, each process has their own port and can be multicast to
        //Process 0 is 6050, process 1 is 7050, 2 is 7050
        PubKeyPort = PubKeyPortBase + (ProjectH.processID * 1000);
    }
}

class PubKeyWorker extends Thread {
    Socket projectHSock;
    PubKeyWorker(Socket s) {
        projectHSock = s;
    }
    public void run(){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(projectHSock.getInputStream()));
            String line = in.readLine();
            //show messages received from all three processes
            System.out.println(line);
            projectHSock.close();
        } catch (IOException x){x.printStackTrace();}
    }
}

class PubKeyServer implements Runnable {

    public void run(){
        int queueLen = 6;
        Socket projectHSock;
        //removed Integer.toString for Ports.SayHelloPort, works fine
        System.out.println("Starting Pub Key input thread using " + Ports.PubKeyPort);
        try{
            ServerSocket servSock = new ServerSocket(Ports.PubKeyPort, queueLen);
            while (true) {
                projectHSock = servSock.accept();
                new PubKeyWorker(projectHSock).start();
            }
        }catch (IOException ioe) {System.out.println(ioe);}
    }
}

class PublicKeyRecords {
    String PubKey;
    int processID;

    //get and set methods for public keys
    //only need set for now
    public void setPubKey(String PK){
        this.PubKey = PK;
    }

    public void setProcessID(int ID){
        this.processID = ID;
    }

    public String getPubKey(){
        return this.PubKey;
    }

    public int getProcessID(){
        return this.processID;
    }

}


