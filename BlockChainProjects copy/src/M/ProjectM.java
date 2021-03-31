/* This code was implemented from:
* https://www.javacodex.com/Concurrency/PriorityBlockingQueue-Example
* Link was provided in Prof Elliot bc.java */

package M;


import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class ProjectM {
    static String serverName = "localhost";
    static String blockchain = "[First block]";
    static int numProcesses = 3; // Set this to match your batch execution file that starts N processes with args 0,1,2,...N
    static int PID = 0;
    LinkedList<BlockRecord> recordList = new LinkedList<BlockRecord>();

    // This queue of UVBs must be concurrent because it is shared by producer threads and the consumer thread
    final PriorityBlockingQueue<BlockRecord> ourPriorityQueue = new PriorityBlockingQueue<>(100, BlockTSComparator);

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

    public void KeySend (){ // Multicast our public key to the other processes
        Socket sock;
        PrintStream toServer;
        try{
            for(int i=0; i< numProcesses; i++){// Send our public key to all servers.
                sock = new Socket(serverName, Ports.KeyServerPortBase + (i * 1000));
                toServer = new PrintStream(sock.getOutputStream());
                toServer.println("FakeKeyProcess" + bc.PID); toServer.flush();
                sock.close();
            }
        }catch (Exception x) {x.printStackTrace ();}
    }

    /* Create some simple Unverified Blocks (TimeStamp and some simple data) -- UVBs. Multicast to all the processes.

     We include the sending of an Object over a socket connection as another tool (the BlockRecord object).
     Be sure that the object (in this case the BlockRecord) is declared as Serializable.
    */
    public void UnverifiedSend (){ // Multicast some unverified blocks to the other processes

        Socket UVBsock; // Will be client connection to the Unverified Block Server for each other process.
        BlockRecord tempRec;

        String fakeBlockData;
        String T1;
        String TimeStampString;
        Date date;
        Random r = new Random();

        //  Thread.sleep(1000); // wait for public keys to settle, normally would wait for an ack that it was received.
        try{
            for (int i=0; i < 4; ++i){
                BlockRecord BR = new BlockRecord();
                fakeBlockData = "(Block#" + Integer.toString(((bc.PID+1)*10)+i) + " from P"+ bc.PID + ")";
                BR.setData(fakeBlockData);
                date = new Date();
                T1 = String.format("%1$s %2$tF.%2$tT", "", date); // Create the TimeStamp string.
                TimeStampString = T1 + "." + i; // Use process num extension. No timestamp collisions!
                // System.out.println("Timestamp: " + TimeStampString);
                BR.setTimeStamp(TimeStampString); // Will be able to priority sort by TimeStamp
                recordList.add(BR);
            }
            Collections.shuffle(recordList); // Shuffle the list to later demonstrate how the priority queue sorts them.

            Iterator<BlockRecord> iterator = recordList.iterator();
	    /* // In case you want to see the shuffled version.
      while(iterator.hasNext()){
	tempRec = iterator.next();
	System.out.println(tempRec.getTimeStamp() + " " + tempRec.getData());
      }
      System.out.println("");
	    */

            ObjectOutputStream toServerOOS = null; // Stream for sending Java objects
            for(int i = 0; i < numProcesses; i++){// Send some sample Unverified Blocks (UVBs) to each process
                System.out.println("Sending UVBs to process " + i + "...");
                iterator = recordList.iterator(); // We saved our samples in a list, restart at the beginning each time.
                while(iterator.hasNext()){
                    // Client connection. Triggers Unverified Block Worker in other process's UVB server:
                    UVBsock = new Socket(serverName, Ports.UnverifiedBlockServerPortBase + (i * 1000));
                    toServerOOS = new ObjectOutputStream(UVBsock.getOutputStream());
                    Thread.sleep((r.nextInt(9) * 100)); // Sleep up to a second to randominze when sent.
                    tempRec = iterator.next();
                    // System.out.println("UVB TempRec for P" + i + ": " + tempRec.getTimeStamp() + " " + tempRec.getData());
                    toServerOOS.writeObject(tempRec); // Send the unverified block record object
                    toServerOOS.flush();
                    UVBsock.close();
                }
            }
            Thread.sleep((r.nextInt(9) * 100)); // Sleep up to a second to randominze when sent.
        }catch (Exception x) {x.printStackTrace ();}
    }

    public static void main(String args[]) {
        bc s = new bc();
        s.run(args); // Break out of main to avoid static reference conflicts.
    }

    /* Set up the PID and port number schemes. Start the three servers for incoming public keys, UVBs and
     BlockChains. Multicast the public key and the sample UVBs. Start consuming the UBVs, verifying them
     and adding them to the blockchain. */

    public void run(String args[]) {
        System.out.println("Running now\n");
        // int q_len = 6; /* Number of requests for OpSys to queue. Not interesting. */
        PID = (args.length < 1) ? 0 : Integer.parseInt(args[0]); // Process ID is passed to the JVM
        System.out.println("Clark Elliott's Block Coordination Framework. Use Control-C to stop the process.\n");
        System.out.println("Using processID " + PID + "\n");
        new Ports().setPorts(); // Establish OUR port number scheme, based on PID

        new Thread(new PublicKeyServer()).start(); // New thread to process incoming public keys
        new Thread(new UnverifiedBlockServer(ourPriorityQueue)).start(); // New thread to process incoming unverified blocks
        new Thread(new BlockchainServer()).start(); // New thread to process incomming new blockchains
        try{Thread.sleep(1000);}catch(Exception e){} // Wait for servers to start.
        KeySend();
        try{Thread.sleep(1000);}catch(Exception e){}
        new bc().UnverifiedSend(); // Multicast some new unverified blocks out to all servers as data
        try{Thread.sleep(1000);}catch(Exception e){} // Wait for multicast to fill incoming queue for our example.
        new Thread(new UnverifiedBlockConsumer(ourPriorityQueue)).start(); // Start consuming the queued-up unverified blocks
    }

}

class BlockRecord implements Serializable { // Because here we send this object over socket stream, must be Serializable
    /* Examples of block fields. You should pick, and justify, your own more extensive set: */
    String TimeStamp;
    String Data;
    /* Examples of accessors for the BlockRecord fields: */
    public String getTimeStamp() {return TimeStamp;}
    public void setTimeStamp(String TS){this.TimeStamp = TS;}
    public String getData() {return Data;}
    public void setData(String DATA){this.Data = DATA;}
}

