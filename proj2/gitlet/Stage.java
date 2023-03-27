package gitlet;


import java.io.File;
import static gitlet.Utils.*;

/** Represents a gitlet stage object.
 *
 *  Description: Adds a copy of the file as it currently exists to the staging area (see the
 *  description of the commit command). For this reason, adding a file is also called staging
 *  the file for addition. Staging an already-staged file overwrites the previous entry in the
 *  staging area with the new contents. The staging area should be somewhere in .gitlet. If the
 *  current working version of the file is identical to the version in the current commit, do
 *  not stage it to be added, and remove it from the staging area if it is already there (as can
 *  happen when a file is changed, added, and then changed back to it’s original version). The
 *  file will no longer be staged for removal (see gitlet rm), if it was at the time of the command.
 *
 *  @author Allen Liang
 */
public class Stage {
    /** The stage working directory that stores addition and removal directories */
    public static final File STAGE_DIR = join(Repository.GITLET_DIR, "stage");
    /** The addition directory */
    public static final File ADITTION = join(STAGE_DIR, "addition");
    /** The remove directory */
    public static final File REMOVE = join(STAGE_DIR, "remove");
    public static void setupStage() {
        if(!STAGE_DIR.exists()) {
            STAGE_DIR.mkdir();
        }
        if(!ADITTION.exists()) {
            ADITTION.mkdir();
        }
        if(!REMOVE.exists()) {
            REMOVE.mkdir();
        }
    }
}
