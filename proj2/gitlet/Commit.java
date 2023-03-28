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
        this.message = "This is the initial commit!";
        this.timeStamp = "00:00:00 UTC, Thursday, 1 January 1970";
    }
    /** Set up COMMIT_DIR */
    public static void setupCommit() {
        if(!COMMIT_DIR.exists()) {
            COMMIT_DIR.mkdir();
        }
    }
    /** Generate a unique ID for the commit object */
    public String generateID() {
        return sha1(serialize(this));
    }
    /** Formatting date */
    private static String simpleDateFormatter(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(("HH:mm:ss zzz, EEEE, d MMMM yyyy"));
        formatter.setTimeZone(TimeZone.getTimeZone("ETD"));
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
        if(Stage.stageEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        // Addition first: go through each file(should be Map too)
        List<String> fileAdditionList = plainFilenamesIn(".gitlet/stage/addition");
        for(String each : fileAdditionList) {
            // Adding a key value pair in a map will change the value if it already exists in the map
            // Just simply add all the file in regardless 不管怎么加 可以replace 可以加新的 可以不变 所以直接加就可以了
            Blobs blob = new Blobs(each);
            String blobID = blob.generateBlobID();
            blobsMap.put(each, blobID);
            File deleteFile = join(Stage.ADDITION, each);
            deleteFile.delete();
        }
        // Remove second: go through each file and remove it from the map
        List<String> fileRemoveList = plainFilenamesIn(".gitlet/stage/remove");
        for(String each : fileRemoveList) {
            blobsMap.remove(each);
            File deleteFile = join(Stage.REMOVE, each);
            deleteFile.delete();
        }
    }
    /** Find a blob in the blobMap */
    public String findBlobID(String fileName) {
        String ID = blobsMap.get(fileName);
        return ID;
    }
    /** Check if a file is already in the current commit's blobsap */
    public boolean findFile(String fileName) {
        return blobsMap.containsKey(fileName);
    }

}
