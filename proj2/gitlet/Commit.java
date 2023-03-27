package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import static gitlet.Utils.*;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Date; // TODO: You'll likely use this in this class


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
    private Blobs blobsID;
    public Commit(String message) {
        //file name of the parent file(sha1)
        String parentFileName = Repository.HEAD;
        //clone the previous version
        parentCommit = clonePrevious(parentFileName);
        this.message = message;
        this.parent = parentFileName;
        Date date = new Date();
        this.timeStamp = simpleDateFormatter(date);
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
    private static String simpleDateFormatter(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(("HH:mm:ss zzz, EEEE, d MMMM yyyy"));
        formatter.setTimeZone(TimeZone.getTimeZone("ETD"));
        return formatter.format(date);
    }
    /** Return the value of the previous version of commit to clone*/
    public Commit clonePrevious(String fileName) {
        File inFile = join(COMMIT_DIR, fileName);
        Commit parentCommit = readObject(inFile, Commit.class);
        return parentCommit;
    }
}
