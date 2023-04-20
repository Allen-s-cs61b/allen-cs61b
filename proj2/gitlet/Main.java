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
            case "global-log":
                Repository.global_log();
                break;
            case "find":
                if(args.length == 1){
                    System.out.println("Please enter a message.");
                    System.exit(0);
                }
                Repository.find(args[1]);
                break;
            case "status":
                Repository.status();
                break;
            case "checkout":
                switch(args.length) {
                    case 1:
                        System.out.println("Please enter a message.");
                        System.exit(0);
                        break;
                    case 3:
                        if(!args[1].equals("--")) {
                            System.out.println("Incorrect operands.");
                            System.exit(0);
                        }
                        Repository.checkout(Repository.getContentAsString(Repository.HEAD), args[2]);
                        break;
                    case 4:
                        if(!args[2].equals("--")) {
                            System.out.println("Incorrect operands.");
                            System.exit(0);
                        }
                        Repository.checkout(args[1], args[3]);
                        break;
                    case 2:
                        Repository.checkout(args[1]);
                        break;
                }
                break;
            case "branch":
                if(args.length == 1){
                    System.out.println("Please enter a branch name.");
                    System.exit(0);
                }
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                if(args.length == 1){
                    System.out.println("Please enter a branch name.");
                    System.exit(0);
                }
                Repository.rmBranch(args[1]);
                break;
            case "reset":
                if(args.length == 1){
                    System.out.println("Please enter a commit ID.");
                    System.exit(0);
                }
                Repository.reset(args[1]);
                break;
            case "merge":
                if(args.length == 1){
                    System.out.println("Please enter a branch name");
                    System.exit(0);
                }
                Repository.merge(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
}
