/* Using Prof Elliot's bc.java code, changed to fit project

How to run in console:
javac src/E/ProjectE.java
In three different consoles:
java src/E/ProjectE.java
java src/E/ProjectE.java 1
java src/E/ProjectE.java 2

http://www.javacodex.com/Concurrency/PriorityBlockingQueue-Example

*/


package E;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ProjectE {

    String serverName = "localhost";
    int numProcesses = 3;
    static int processID = 0;

    //Only process 0 runs and will say hello to all process on their console
    public void multicastHello(){
        Socket sock;
        PrintStream toServer;

        try{
            for(int i=0; i< numProcesses; i++){
                sock = new Socket(serverName, Ports.SayHelloPortBase + (i * 1000));
                toServer = new PrintStream(sock.getOutputStream());
                //only process 0 sent it to itself in this if statement
                //if (i == 0 ) toServer.println(("Hello multicast message from Process 0"));
                toServer.println(("Hello multicast message from Process 0"));
                toServer.flush();
                sock.close();
            }
        } catch (Exception x) {x.printStackTrace ();}
    }

    public static void main(String[] args) {
        ProjectE s = new ProjectE();
        s.run(args);
    }

    public void run(String[] args) {

        System.out.println("\n---Arturo Chaidez's ProjectE running---\n");

        processID = (args.length < 1) ? 0 : Integer.parseInt(args[0]);

        System.out.println("This is processID: " + processID + "\n");
        new Ports().setPorts();

        new Thread(new SayHelloServer()).start();

        try{Thread.sleep(10000);}catch(Exception ignored){}
        if (processID == 0 ) multicastHello();
    }
}

class Ports{
    //Formally KeyServerPortBase and KeyServerPort
    public static int SayHelloPortBase = 6050;
    public static int SayHelloPort;

    public void setPorts(){
        //by doing *1000, each process has their own port and can be multicast to
        //Process 0 is 6050, process 1 is 7050, 2 is 7050
        SayHelloPort = SayHelloPortBase + (ProjectE.processID * 1000);
    }
}

class SayHelloWorker extends Thread {
    Socket projectESock;
    SayHelloWorker(Socket s) {
        projectESock = s;
    }
    public void run(){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(projectESock.getInputStream()));
            String line = in.readLine();
            //show message sent from process 0
            System.out.println(line);
            projectESock.close();
        } catch (IOException x){x.printStackTrace();}
    }
}

class SayHelloServer implements Runnable {

    public void run(){
        int queueLen = 6;   //stick with usual 6.
        Socket projectESock;
        //removed Integer.toString for Ports.SayHelloPort, works fine
        System.out.println("Starting Say Hello Server input thread using " + Ports.SayHelloPort);
        try{
            ServerSocket servSock = new ServerSocket(Ports.SayHelloPort, queueLen);
            while (true) {
                projectESock = servSock.accept();
                new SayHelloWorker(projectESock).start();
            }
        }catch (IOException ioe) {System.out.println(ioe);}
    }
}
