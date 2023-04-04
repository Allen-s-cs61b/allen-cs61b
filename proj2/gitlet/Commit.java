package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import static gitlet.Utils.*;

import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.*;


/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *  Description: Saves a snapshot of tracked files in the current commit and staging area so
 *  they can be restored at a later time, creating a new commit. The commit is said to be tracking
 *  the saved files. By default, each commit’s snapshot of files will be exactly the same as its
 *  parent commit’s snapshot of files; it will keep versions of files exactly as they are, and not
 *  update them. A commit will only update the contents of files it is tracking that have been staged
 *  for addition at the time of commit, in which case the commit will now include the version of the
 *  file that was staged instead of the version it got from its parent. A commit will save and start
 *  tracking any files that were staged for addition but weren’t tracked by its parent. Finally, files
 *  tracked in the current commit may be untracked in the new commit as a result being staged for
 *  removal by the rm command (below).
 *
 *  @author Allen Liang
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    public static final File COMMIT_DIR = join(Repository.GITLET_DIR, "commit");
    /** The message of this Commit. */
    private String message;
    /** The time stamp of this Commit */
    private String timeStamp;
    /** The staging area for the files */
    /** The parent SHA1 hash */
    private String parent;
    /** The parent commmit, used for clone*/
    private Commit parentCommit;
    /**
     *  The map that stores blobs
     *  First string represents the file name which can be the same
     *  Second string represents the unique sha1 hash of the blob
     */
    private Map<String, String> blobsMap = new HashMap<>();
    /** Store the ID of the initCommit */
    public static final String initCommitID = new Commit().generateID();
    /** Set up COMMIT_DIR */
    public static void setupCommit() {
        if(!COMMIT_DIR.exists()) {
            COMMIT_DIR.mkdir();
        }
    }
    /** Constructor for normal commit */
    public Commit(String message) {
        //file name of the parent file(sha1)
        String parentFileName = readContentsAsString(Repository.HEAD);
        //clone the previous version of commit object
        this.parentCommit = clonePrevious(parentFileName);
        this.message = message;
        //parent is the previous commit sha1 hash
        this.parent = parentFileName;
        Date date = new Date();
        this.timeStamp = simpleDateFormatter(date);
        this.blobsMap = parentCommit.blobsMap;
        this.updateBlobsMap();
    }
    /** Create initial commit */
    public Commit() {
        this.message = "initial commit";
        Date date = new Date(0);
        this.timeStamp = simpleDateFormatter(date);
    }
    /** Generate a unique ID for the commit object */
    public String generateID() {
        return sha1(serialize(this));
    }
    /** Get the current based on the commitID */
    public static Commit getCommit(String commitID) {
        //String CommitID = readContentsAsString(Repository.HEAD);
        File CommitFile = join(COMMIT_DIR, commitID);
        Commit commit = readObject(CommitFile, Commit.class);
        return commit;
    }
    /** Print each commit */
    private void printCommit() {
        // Print the current commit
        System.out.println("===" + "\n" + "commit " + this.generateID()
                + "\n" + "Date: " + this.timeStamp
                + "\n" + this.message + "\n");
    }
    /** Print commit for log */
    public void printCommitLog() {
        // Print the current commit
        this.printCommit();
        // If parent is null
        if(this.generateID().equals(initCommitID)) {
            return;
        }
        String parentID = this.parent;
        File inFile = join(Commit.COMMIT_DIR, parentID);
        Commit currentCommit = readObject(inFile, Commit.class);
        currentCommit.printCommitLog();
    }
    /** Print commit for global-log */
    public static void printCommitGlobalLog() {
        // Iterate through the commit directory and print the commit despite the order
        List<String> commitList = plainFilenamesIn(COMMIT_DIR);
        for(String each : commitList) {
            File inFile = join(COMMIT_DIR, each);
            Commit commit = readObject(inFile, Commit.class);
            commit.printCommit();
        }
    }
    /** Find commits with certain message in the Commit directory, return true if it has at least one */
    public static boolean find(String message) {
        // Record if the commit folder has such commit, change to true if find one
        boolean exist = false;
        // Iterate through the commit directory and print the commit despite the order
        List<String> commitList = plainFilenamesIn(COMMIT_DIR);
        for(String each : commitList) {
            File inFile = join(COMMIT_DIR, each);
            Commit commit = readObject(inFile, Commit.class);
            if(commit.message.equals(message)) {
                System.out.println(commit.generateID());
                exist = true;
            }
        }
        return exist;
    }
    /** Find commit based on the commit ID, return true if the ID exist, false if not */
    public static boolean findWithID(String commitID) {
        List<String> commitList = plainFilenamesIn(".gitlet/commit");
        return commitList.contains(commitID);
    }
    /** Formatting date */
    private static String simpleDateFormatter(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(("EEE MMM d HH:mm:ss yyyy Z"));//"HH:mm:ss zzz, EEEE, d MMMM yyyy"
        formatter.setTimeZone(TimeZone.getTimeZone("EST"));
        return formatter.format(date);
    }
    /** Return the value of the previous version of commit to clone*/
    private Commit clonePrevious(String fileName) {
        File inFile = join(COMMIT_DIR, fileName);
        Commit parentCommit = readObject(inFile, Commit.class);
        return parentCommit;
    }
    /** Change the blobsMap based on the staging area, and delete file in staging area */
    private void updateBlobsMap() {
        // Addition first: go through each file(should be Map too)
        List<String> fileAdditionList = plainFilenamesIn(".gitlet/stage/addition");
        for(String each : fileAdditionList) {
            // Adding a key value pair in a map will change the value if it already exists in the map
            // Just simply add all the file in regardless 不管怎么加 可以replace 可以加新的 可以不变 所以直接加就可以了
            Blobs blob = new Blobs(each);
            String blobID = blob.generateBlobID();
            blobsMap.put(each, blobID);
            // Clean up the ADDITION stage
            File deleteFile = join(Stage.ADDITION, each);
            deleteFile.delete();
        }
        // Remove second: go through each file and remove it from the map
        // (the map should have it for it to on the remove stage)
        List<String> fileRemoveList = plainFilenamesIn(".gitlet/stage/remove");
        for(String each : fileRemoveList) {
            blobsMap.remove(each);
            // Clean up the REMOVE stage
            File deleteFile = join(Stage.REMOVE, each);
            deleteFile.delete();
        }
    }
    /** Check if a file is already in the current commit's blobsMap */
    public boolean findFile(String fileName) {
        return blobsMap.containsKey(fileName);
    }
    /** Get the blobID using the file name */
    public String getBlobID(String fileName) {
        return blobsMap.get(fileName);
    }
    public List<Blobs> getBlobsList() {
        List<Blobs> blobsList = new ArrayList<>();
        for(String value : blobsMap.values()) {
            blobsList.add(Blobs.getBlob(value));
        }
        return blobsList;
    }
    /** Check if a file is tracked in the HEAD commit */
    public boolean trackFile(String fileName) {
        //Commit currentCommit = getCommit(readContentsAsString(Repository.HEAD));
        return this.findFile(fileName);
    }
}
