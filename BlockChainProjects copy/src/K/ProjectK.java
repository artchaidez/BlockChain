/*
https://mkyong.com/java/how-to-parse-json-with-gson/
http://www.java2s.com/Code/Java/Security/SignatureSignAndVerify.htm
https://www.mkyong.com/java/java-digital-signatures-example/ (not so clear)
https://javadigest.wordpress.com/2012/08/26/rsa-encryption-example/
https://www.programcreek.com/java-api-examples/index.php?api=java.security.SecureRandom
https://www.mkyong.com/java/java-sha-hashing-example/
https://stackoverflow.com/questions/19818550/java-retrieve-the-actual-value-of-the-public-key-from-the-keypair-object
https://www.java67.com/2014/10/how-to-pad-numbers-with-leading-zeroes-in-Java-example.html

https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.2/
All 3 BlockInput files are in this folder. I assume we can use any data to be read, but as these
are the files for the project so I am using these.
*/

package K;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/* From Prof Elliot's BlockInputG.java and BlockJ.java.*/
class ProjectK {
    String blockNum;
    String timeStamp;
    String VerificationProcessID;
    String previousHash;
    UUID uuid;
    String firstName;
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

    public UUID getUUID() {return uuid;} // Later will show how JSON marshals as a string. Compare to BlockID.
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

    private static final int iFIRSTNAME = 0;
    private static final int iLASTNAME = 1;
    private static final int iDOB = 2;
    private static final int iSSNUM = 3;
    private static final int iDIAGNOSIS = 4;
    private static final int iTREATMENT = 5;
    private static final int iPRESCRIPTION = 6;

    public Blockchain(String[] argv) {
    }

    public static void main(String[] argv) {
        Blockchain s = new Blockchain(argv);
        s.run(argv);
    }

    public void run(String[] argv) {

        System.out.println("Reading BlockInput file\n");
        try{
            readBlockInput(argv);
        } catch (Exception ignored) {}
    }

    public void readBlockInput(String[] args) throws Exception {

        ArrayList<ProjectK> recordList2 = new ArrayList<>();

        int processID;
        //int UnverifiedBlockPort;
        //int BlockChainPort;

        if (args.length > 1) System.out.println("Special functionality is present \n");

        processID = (args.length < 1) ? 0 : Integer.parseInt(args[0]);
        //UnverifiedBlockPort = 4710 + processID;
        //BlockChainPort = 4820 + processID;

        System.out.println("Process number is: " + processID );

        String FILENAME = switch (processID) {
            case 1 -> "BlockInput1.txt";
            case 2 -> "BlockInput2.txt";
            default -> "BlockInput0.txt";
        };

        System.out.println("Read BlockInput file: " + FILENAME);

        try {
            BufferedReader br = new BufferedReader(new FileReader(FILENAME));
            String[] tokens;    //to hold string it is reading
            String stringLineInput;
            UUID uuidForBlock;

            int blockCount = 0;
            while ((stringLineInput = br.readLine()) != null) {

                ProjectK BR = new ProjectK();

                try {
                    Thread.sleep(1001);
                } catch (InterruptedException e) {
                }
                Date date = new Date();
                String T1 = String.format("%1$s %2$tF.%2$tT", "", date);
                String TimeStampString = T1 + "." + processID; // No timestamp collisions!
                System.out.println("Timestamp: " + TimeStampString);
                BR.setTimeStamp(TimeStampString); // Will be able to priority sort by TimeStamp

                uuidForBlock = UUID.randomUUID();
                BR.setBlockNum(String.valueOf(blockCount));
                BR.setUUID(uuidForBlock);
                tokens = stringLineInput.split(" +");
                BR.setFirstName(tokens[iFIRSTNAME]);
                BR.setLastName(tokens[iLASTNAME]);
                BR.setSsNum(tokens[iSSNUM]);
                BR.setDOB(tokens[iDOB]);
                BR.setDiagnosis(tokens[iDIAGNOSIS]);
                BR.setTreatment(tokens[iTREATMENT]);
                BR.setPrescription(tokens[iPRESCRIPTION]);

                recordList2.add(BR);
                blockCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
