package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        // If args is empty, print an error msg and exit
        if(args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.setupRepository();
                Repository.makeInitCommit();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                // The second args is the fileName
                if(args.length == 1){
                    System.out.println("Please enter a file name.");
                    System.exit(0);
                }
                Repository.add(args[1]);
                break;
            case "rm":
                if(args.length == 1){
                    System.out.println("Please enter a file name.");
                    System.exit(0);
                }
                Repository.rm(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                if(args.length == 1){
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                //create a commit
                Repository.makeCommit(args[1]);
                break;
            case "log":
                Repository.log();
                break;
        }
    }
}
