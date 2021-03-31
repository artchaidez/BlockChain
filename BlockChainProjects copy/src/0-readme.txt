Explanation of each Project and what is accomplished in each. There are explanations of
known bugs and notes of what can be implemented or improved upon, but mainly as a self-reminder.

**** INCLUDE ALL THE SITES PROF ELLIOT HAS IN JAVA FILES IF THAT CODE WAS USED ***

ProjectA:
This a basic blockchain. Each block has data (a string saying block and its number), uuid, the
previous block's hash, nonce, and its own hash. Class StringUtil uses SHA256 to create
the hash, and createHash method calls it. MineBlock method creates the block with
its hash. AddBlock is exactly what is sounds like, as well as isChainValid. In main, the actual
block chain is made using everything above and places it in an array. Some code was from
Prof Elliot's BlockInputG.java. A link provided by Prof Elliot was used heavily, and
the link is in the file. Some of these features are not required at this point, see comments
in the project for more.

ProjectA2:
Built upon A. Added timestamp and blockNum to the components of the blockchain.

ProjectB:
Built upon ProjectA2. Turns the block chain into a JSON file on the disk. Prof Elliot's BlockJ.java
and BlockInputG.java was used as a source.
NOTE: Need to implement simple block numbers starting from 0. Come back to later -> accomplished

ProjectC:
Built upon ProjectB. Writes JSON on disk, reads JSON, which is an array, and prints out everything
in it. Some code from Prof Elliot's BlockJ.java was implemented and changed work.

ProjectD:
Take a command line argument to determine the process ID. If there is no argument or 0, it is
process 0. Process 1 takes in 1, process 2 takes in 2. Code from Code from Prof Elliot BlockJ.java
was implemented.

ProjectE:
Open up three processes, define each with a command line number. Each process will have a port
to be able to receive messages, and process 0 will say hello to all 3 processes.
Used Prof Elliot's bc.java code as a template.

ProjectF:
Built upon ProjectE. Only difference is instead of process 0 saying hello to the other processes,
all processes say hello.

ProjectG:
Create a public/private key pair, then turn the public key into JSON and back into the key
(with additional steps not mentioned), and finally prove the key is correct by doing an
encryption + decryption. Implemented code from Prof Elliot BlockJ.java, as well as a reference
Prof gave us. See comments to get the link.

ProjectH:
This project combines ProjectF and ProjectG, using Prof Elliot BlockJ.java and bc.java code.
This project is to send a string format of public keys to each process, with each process
storing its key with its ID somewhere.
Note: My code seems to making the same key and string regardless of how the code
is designed. Unsure if the 3 strings on the console have to be different to be truly different
keys.

ProjectI:
Built upon ProjectB, difficulty was reduced and sleep was implemented. This Project
takes around the same amount of work in terms of time to complete, but does much less
actual work. It uses hard coded fake data.

ProjectJ:
Copy and combine code so that you multicast your blockchain (with fake data) in JSON format
from each process to each other process. A blockchain receiving server is needed, just like the
key receiving server.

ProjectK:
This project reads in all of the data for each respective process.
Create an unverified block for each line of data in the data file.

ProjectL(not working):
Built upon ProjectK and previous projects. Read data, put them in blocks, convert into JSON
and send them to all 3 processes. Print out the name of each patient (12 total) and
translate each JSON block-object into a regular Java block-object.
Two errors:
1- Need all the inputs on json. Right now only BlockInput1 is being made into JSON ->
Use a switch statement to make 3 files, then use another switch statement to read the.
Ugly and probably not what prof wants, but I'll figure it out one day :(
2- Only reading first peron in json, three times -> it now sending only the first person in
each json. Unsure why it is not looping -> sending strings of the blocks, works but ugly

ProjectM (NOT WORKING):
Implement a priority queue. Create a couple of fake unverified blocks with timestamps, then
insert the blocks into the priority queue, sorted (queued) by timestamp priority.