/*

Look over prof code. This looks familiar. Code from BlockJ.java
Starting from src directory, type into console:
javac D/ProjectD.java
java D/ProjectD.java [args]

Does not want to work from other starting points, such as being in the D folder and doing:
Correction: does work this way now. Not sure why it start working. My code was done on
IntelliJ, this project can be done  at any folder now.
javac ProjectD.java
java ProjectD.java [args]

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

package D;


public class ProjectD  {

    public static void main(String[] args) {

        //From Prof Elliot BlockJ.java
        System.out.println("\n----- Running utility to designate process ID -----\n");
        int processNum;

        if (args.length > 2) System.out.println("There is additional functionality. \n");

        if (args.length < 1) processNum = 0;
        else if (args[0].equals("0")) processNum = 0;
        else if (args[0].equals("1")) processNum = 1;
        else if (args[0].equals("2")) processNum = 2;
        else processNum = 0; //if there was an argument that is not acceptable
        /* Single process (0) will use ports 4710 and 4810. Process 1 adds 1 to both port
         * numbers and process 2 add 2 to each port number */

        System.out.println("Hello from Process: " + processNum + "\n");
    }
}
