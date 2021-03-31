/* Using ProjectE code, which is based on Prof Elliot's bc.java code

How to compile in console:
javac src/F/ProjectF.java
In three different consoles:
java src/F/ProjectF.java
java src/F/ProjectF.java 1
java src/F/ProjectF.java 2

http://www.javacodex.com/Concurrency/PriorityBlockingQueue-Example
*/

package F;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ProjectF {

    String serverName = "localhost";
    int numProcesses = 3;
    static int processID = 0;

    public void multicastHello(){
        Socket sock;
        PrintStream toServer;

        try{
            for(int i=0; i< numProcesses; i++){
                sock = new Socket(serverName, Ports.SayHelloPortBase + (i * 1000));
                toServer = new PrintStream(sock.getOutputStream());
                toServer.println(("Hello multicast message from Process " + processID));
                toServer.flush();
                sock.close();
            }
        }catch (Exception x) {x.printStackTrace ();}
    }

    public static void main(String[] args) {
        ProjectF s = new ProjectF();
        s.run(args);
    }

    public void run(String[] args) {
        System.out.println("\n---Arturo Chaidez's ProjectF running---\n");

        processID = (args.length < 1) ? 0 : Integer.parseInt(args[0]);

        System.out.println("This is processID: " + processID + "\n");
        new Ports().setPorts();

        new Thread(new SayHelloServer()).start();

        try{Thread.sleep(10000);}catch(Exception e){}
        multicastHello();
    }

}

class Ports{
    //Formally KeyServerPortBase and KeyServerPort
    public static int SayHelloPortBase = 6050;
    public static int SayHelloPort;

    public void setPorts(){
        //by doing *1000, each process has their own port and can be multicast to
        //Process 0 is 6050, process 1 is 7050, 2 is 7050
        SayHelloPort = SayHelloPortBase + (ProjectF.processID * 1000);
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
            //show messages received from all three processes
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
        System.out.println("Starting Say Hello input thread using " + Ports.SayHelloPort);
        try{
            ServerSocket servSock = new ServerSocket(Ports.SayHelloPort, queueLen);
            while (true) {
                projectESock = servSock.accept();
                new SayHelloWorker(projectESock).start();
            }
        }catch (IOException ioe) {System.out.println(ioe);}
    }
}