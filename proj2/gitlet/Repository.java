package gitlet;

import java.io.File;
import static gitlet.Utils.*;

// TODO: any imports you need here

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
     * TODO: add instance variables here.
     *
     * Create all the necessary directory and file
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    /** The HEAD pointer that points to the current working place ID */
    public static String HEAD;
    /** The master pointer that points to the latest branch ID*/
    public static String master;
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The stage directory */
    public static Stage stage;

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
        writeObject(commitFile, initCommit);
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
    public static void add() {

    }
}
