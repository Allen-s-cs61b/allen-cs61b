package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *  Set up all the directories?
 *  It has all the methods thats gonna be used in Main?
 *
 *  @author Allen Liang
 */
public class Repository {
    /**
     * Create all the necessary directory and file
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The HEAD pointer that points to the current working place ID */
    public static File HEAD = join(GITLET_DIR, "HEAD");
    /** The master pointer that points to the latest branch ID*/
    public static File master = join(GITLET_DIR, "master");
    /** The current branch */
    private static File currentBranch = join(GITLET_DIR, "currentBranch");
    /** Branch list that stores all the branches */
    private static File branchList = join(GITLET_DIR, "branchList");
    /** Track the file in the repository, set up everytime commit is called */
    private static File repository = join(GITLET_DIR, "repository");

    /** Set up all the directories */
    public static void setupRepository() {
        if(!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
        }
        if(!branchList.exists()) {
            branchList.mkdir();
        }
        if(!repository.exists()) {
            repository.mkdir();
        }
        // Set up stage directories STAGE_DIR (ADDITION, REMOVE)(think about static method or non static or constructor
        Stage.setupStage();
        // Set up commit directory COMMIT_DIR
        Commit.setupCommit();
        // Set up more
        Blobs.setUpBlobs();
    }
    /** Copy all the files in CWD to the repository directory */
    private static void parseCopyRepository() throws IOException {
        // Clean up the Repo
        cleanRepository();
        repository.mkdir();
        List<String> fileCWD = plainFilenamesIn(CWD);
        for(String each : fileCWD) {
            File newFile = join(repository, each);
            newFile.createNewFile();
        }
    }
    /** Return the List<String> of repository */
    private static List<String> listRepository() {
        return plainFilenamesIn(".gitlet/repository");
    }
    /** Clean all the files in the repository */
    private static void cleanRepository() {
        List<String> fileRepo = listRepository();
        for(String each : fileRepo) {
            File file = join(repository, each);
            file.delete();
        }
    }
    /** Make the initial commit */
    public static void makeInitCommit() throws IOException {
        Commit initCommit = new Commit();
        File commitFile = join(Commit.COMMIT_DIR, initCommit.generateID());
        if(commitFile.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        writeObject(commitFile, initCommit);
        writeContents(HEAD, initCommit.generateID());
        writeContents(master, initCommit.generateID());
        // Set the currentBranch to master (overwrite whatever)
        writeContents(currentBranch, "master");
        // Add the name of master in the branchList
        File branch = join(branchList, "master");
        branch.createNewFile();
        // List of the files in the current working directory which will be the future repository
        parseCopyRepository();
    }
    /** Get the content as string(ID to commit) in the pointer */
    public static String getContentAsString(File file) {
        return readContentsAsString(file);
    }
    /**
     * Description: Adds a copy of the file as it currently exists to the staging area (see the
     * description of the commit command). For this reason, adding a file is also called staging
     * the file for addition. Staging an already-staged file overwrites the previous entry in the
     * staging area with the new contents. The staging area should be somewhere in .gitlet. If the
     * current working version of the file is identical to the version in the current commit, do
     * not stage it to be added, and remove it from the staging area if it is already there (as can
     * happen when a file is changed, added, and then changed back to it’s original version). The
     * file will no longer be staged for removal (see gitlet rm), if it was at the time of the command.
     *
     */
    public static void add(String fileName) {
        // Create File object for the current working file
        File currentFile = join(CWD, fileName);
        if(!currentFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Stage.stagingFile(fileName);
    }
    /**
     *  Description: Unstage the file if it is currently staged for addition. If the file
     *  is tracked in the current commit, stage it for removal and remove the file from
     *  the working directory if the user has not already done so (do not remove it unless
     *  it is tracked in the current commit).
     */
    public static void rm(String fileName) throws IOException {
        //current commit is the HEAD
        Commit currentCommit = Commit.getCommit(getContentAsString(HEAD));
        // Check if the staging area has the file and if the current commit has the file
        if(!Stage.findFileADDITION(fileName) && !currentCommit.findFile(fileName)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        // If the file is in the addition stage, remove it
        if(Stage.findFileADDITION(fileName)) {
            File deleteFile = join(Stage.ADDITION, fileName);
            deleteFile.delete();
        }
        // If the file is in the current commit, add the file to remove stage(you don't
        // need to know the file content)
        if(currentCommit.findFile(fileName)) {
            File outFile = join(Stage.REMOVE, fileName);
            if(!outFile.exists()) {
                outFile.createNewFile();
            }
            // If the file is in the working directory, remove it
            File CWDFile = join(CWD, fileName);
            if(CWDFile.exists()) {
                CWDFile.delete();
            }
        }

    }
    /** Make a commit */
    public static void makeCommit(String message) throws IOException {
        if(message.equals("")) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        if(Stage.stageEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit commit = new Commit(message);
        File commitFile = join(Commit.COMMIT_DIR, commit.generateID());
        writeObject(commitFile, commit);
        writeContents(HEAD, commit.generateID());
        parseCopyRepository();
    }
    /**
     * Description: Starting at the current head commit, display information about each
     * commit backwards along the commit tree until the initial commit, following the
     * first parent commit links, ignoring any second parents found in merge commits.
     * (In regular Git, this is what you get with git log --first-parent). This set of
     * commit nodes is called the commit’s history. For every node in this history, the
     * information it should display is the commit id, the time the commit was made,
     * and the commit message. Here is an example of the exact format it should follow:
     *         ===
     *         commit sha1 ID
     *         Date:
     *         commit message
     *         \n
     */
    public static void log() {
        // Iterate commit starting from HEAD, then next one is its parent commit, until parent commit == null return;
        // HEAD commit, HEAD is the ID of the commit file, to find the commit, use the ID in HEAD to find the actual
        // commit file in the COMMIT_DIR, then readObject as commit
        Commit currentCommit = Commit.getCommit(getContentAsString(HEAD));
        currentCommit.printCommitLog();
    }
    /**
     * Description: Like log, except displays information about all commits ever made.
     * The order of the commits does not matter. Hint: there is a useful method in gitlet.Utils
     * that will help you iterate over files within a directory.
     */
    public static void global_log() {
        Commit.printCommitGlobalLog();
    }
    /**
     * Description: Prints out the ids of all commits that have the given commit message, one per
     * line. If there are multiple such commits, it prints the ids out on separate lines. The commit
     * message is a single operand; to indicate a multiword message, put the operand in quotation
     * marks, as for the commit command below. Hint: the hint for this command is the same as the
     * one for global-log
     */
    public static void find(String message) {
        boolean exist = Commit.find(message);
        if(!exist) {
            System.out.println("Found no commit with that message.");
        }
    }
    /**
     * Description: Displays what branches currently exist, and marks the current branch with a *.
     * Also displays what files have been staged for addition or removal. An example of the exact
     * format it should follow is as follows.
     */
    public static void status() {
        // Printing all the branches
        System.out.println("=== Branches ===");
        List<String> branchList = branchList();
        Collections.sort(branchList);
        for(String each : branchList) {
            if(each.equals(readContentsAsString(currentBranch))) {
                System.out.print("*");
            }
            System.out.println(each);
        }
        // Printing staged-files
        System.out.println("\n" + "=== Staged Files ===");
        List<String> additionList = plainFilenamesIn(".gitlet/stage/addition");
        Collections.sort(additionList);
        for(String each : additionList) {
            System.out.println(each);
        }
        // Printing remove files
        System.out.println("\n" + "=== Removed Files ===");
        List<String> removeList = plainFilenamesIn(".gitlet/stage/remove");
        Collections.sort(removeList);
        for(String each : removeList) {
            System.out.println(each);
        }
        // Iterate through each file in the CWD and see if the content in the file is the same
        // as the files in the current commit
        System.out.println("\n" + "=== Modifications Not Staged For Commit ===");
        // Printing the untracked files
        System.out.println("\n" + "=== Untracked Files ===");
        List<String> untrackedFiles = untrackedFile();
        Collections.sort(untrackedFiles);
        if(!untrackedFiles.isEmpty()) {
            for(String each : untrackedFiles) {
                System.out.println(each);
            }
        }
    }
    /**
     * Parse all files and find modified but not staged file
     */
    /**
     * Parse all the files in the repository to find the untracked file
     * Untracked file 出现在repository 但是没有在之前的repository里 且没有在addition stage里
     * nor tracked 指的是不在current commit(head)里面？commit's findFile()
     */
    private static List<String> untrackedFile() {
        List<String> repository = listRepository();
        List<String> workingDir = plainFilenamesIn(CWD);
        List<String> untrackedFile = new ArrayList<>();
        List<String> additionList = plainFilenamesIn(".gitlet/stage/addition");
        for(String each : workingDir) {
            if(!repository.contains(each) && !additionList.contains(each)) {
                untrackedFile.add(each);
            }
        }
        return untrackedFile;
    }
    /**
     * Descriptions:
     * Takes the version of the file as it exists in the head commit and puts it in the working
     * directory, overwriting the version of the file that’s already there if there is one. The
     * new version of the file is not staged.
     *
     * Takes the version of the file as it exists in the commit with the given id, and puts it in
     * the working directory, overwriting the version of the file that’s already there if there is
     * one. The new version of the file is not staged.
     *
     * Takes all files in the commit at the head of the given branch, and puts them in the working
     * directory, overwriting the versions of the files that are already there if they exist. Also,
     * at the end of this command, the given branch will now be considered the current branch (HEAD).
     * Any files that are tracked in the current branch but are not present in the checked-out branch
     * are deleted. The staging area is cleared, unless the checked-out branch is the current branch
     * (see Failure cases below).
     */
    public static void checkout(String commitID, String name) {
        // Check if the ID exists
        if(!Commit.findWithID(commitID)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        // Check the HEAD commit, if we can't find the file "name", print and exit
        // If you find the file, write it to the working directory, overwrite or create a new one
        Commit currentCommit = Commit.getCommit(commitID);
        if(!currentCommit.findFile(name)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        // Use the file name to find the blobID
        String blobID = currentCommit.getBlobID(name);
        Blobs blob = Blobs.getBlob(blobID);
        // Write the file in the blob to the current directory
        File outFile = join(CWD, blob.getFileName());
        writeContents(outFile, blob.getContent());
    }

    /**
     * If no branch with that name exists, print No such branch exists. If that branch
     * is the current branch, print No need to checkout the current branch. If a working
     * file is untracked in the current branch and would be overwritten by the checkout,
     * print There is an untracked file in the way; delete it, or add and commit it first.
     * and exit; perform this check before doing anything else. Do not change the CWD.
     * @param branchName
     */
    public static void checkout(String branchName) {
        List<String> branchList = branchList();
        if(!branchList.contains(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        //needs test, getName
        if(branchName.equals(readContentsAsString(currentBranch))) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        List<String> untrackedFile = untrackedFile();
        if(untrackedFile.contains(branchName)) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }
        // Clean all untracked file
        for(String each : untrackedFile) {
            File file = join(CWD, each);
            file.delete();
        }
        // overwrite all files in commit(because the files in CWD in either untracked or files
        // that has been tracked)
        Commit currentCommit = Commit.getCommit(readContentsAsString(HEAD));
        List<Blobs> blobsList = currentCommit.getBlobsList();
        for(Blobs each : blobsList) {
            File outFile = join(CWD, each.getFileName());
            writeContents(outFile, each.getContent());
        }
        // Clean stage
        Stage.stageClean();
    }
    /** Return a List<String> of branch names */
    private static List<String> branchList() {
        return plainFilenamesIn(branchList);
    }
    /**
     * Description: Creates a new branch with the given name, and points it at the current head
     * commit. A branch is nothing more than a name for a reference (a SHA-1 identifier) to a commit
     * node. This command does NOT immediately switch to the newly created branch (just as in real Git).
     * Before you ever call branch, your code should be running with a default branch called “master”.
     *
     * Failure cases: If a branch with the given name already exists, print the error message A
     * branch with that name already exists.
     */
    public static void branch(String name) throws IOException {
        //points at the current head means the parent is the current head
        File newBranch = join(GITLET_DIR, name);
        if(newBranch.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        // Write the content in the current HEAD commit to the new branch head
        writeContents(newBranch, getContentAsString(HEAD));
        // Add the branch to the list
        File branch = join(branchList, name);
        branch.createNewFile();
    }
}
