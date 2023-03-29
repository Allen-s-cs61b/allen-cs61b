package gitlet;

import java.io.File;
import java.io.IOException;
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
    /** Store the ID of the initCommit */
    public static final String initCommitID = new Commit().generateID();

    /** Set up all the directories */
    public static void setupRepository() {
        if(!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
        }
        // Set up stage directories STAGE_DIR (ADDITION, REMOVE)(think about static method or non static or constructor
        Stage.setupStage();
        // Set up commit directory COMMIT_DIR
        Commit.setupCommit();
        // Set up more
    }
    /** Make the initial commit */
    public static void makeInitCommit() {
        Commit initCommit = new Commit();
        File commitFile = join(Commit.COMMIT_DIR, initCommit.generateID());
        if(commitFile.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        writeObject(commitFile, initCommit);
        writeContents(HEAD, initCommit.generateID());
        writeContents(master, initCommit.generateID());
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
        // Check if it is already in the ADDITION Staging area
        if(Stage.findFile(fileName)){
            File inFile = join(Stage.ADDITION, fileName);
            // Staging the file
            Stage.stagingSameNameFile(currentFile, inFile);
        } else {
            // Staging the file
            Stage.stagingFile(currentFile, fileName);
        }
    }
    /**
     *  Description: Unstage the file if it is currently staged for addition. If the file
     *  is tracked in the current commit, stage it for removal and remove the file from
     *  the working directory if the user has not already done so (do not remove it unless
     *  it is tracked in the current commit).
     */
    public static void rm(String fileName) throws IOException {
        //current commit is the HEAD
        File inFile = join(Commit.COMMIT_DIR, readContentsAsString(HEAD));
        Commit currentCommit = readObject(inFile, Commit.class);
        // Check if the staging area has the file and if the current commit has the file
        if(!Stage.findFile(fileName) && !currentCommit.findFile(fileName)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        // If the file is in the addition stage, remove it
        if(Stage.findFile(fileName)) {
            File deleteFile = join(Stage.ADDITION, fileName);
            deleteFile.delete();
        }
        // If the file is in the current commit, add the file to remove stage(you dont need to know the file content)
        if(currentCommit.findFile(fileName)) {
            File outFile = join(Stage.REMOVE, fileName);
            if(!outFile.exists()) {
                outFile.createNewFile();
            }
        }
        // If the file is in the working directory, remove it
        File CWDFile = join(CWD, fileName);
        if(CWDFile.exists()) {
            CWDFile.delete();
        }
    }
    /** Make a commit */
    public static void makeCommit(String message) {
        Commit commit = new Commit(message);
        File commitFile = join(Commit.COMMIT_DIR, commit.generateID());
        writeObject(commitFile, commit);
        writeContents(HEAD, commit.generateID());
        writeContents(master, commit.generateID());
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
        String commitID = readContentsAsString(HEAD);
        File inFile = join(Commit.COMMIT_DIR, commitID);
        Commit currentCommit = readObject(inFile, Commit.class);
        currentCommit.printCommit();
    }

}
