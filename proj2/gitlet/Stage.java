package gitlet;


import java.io.CharArrayWriter;
import java.io.File;
import java.util.List;

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
 *  IMPORTANT: The staging files should be named after its file name not sha1 hash name
 *
 *  @author Allen Liang
 */
public class Stage {
    /** The stage working directory that stores addition and removal directories */
    public static final File STAGE_DIR = join(Repository.GITLET_DIR, "stage");
    /** The addition directory */
    public static final File ADDITION = join(STAGE_DIR, "addition");
    /** The remove directory */
    public static final File REMOVE = join(STAGE_DIR, "remove");
    /** Set up the working directory in staging area */
    public static void setupStage() {
        if(!STAGE_DIR.exists()) {
            STAGE_DIR.mkdir();
        }
        if(!ADDITION.exists()) {
            ADDITION.mkdir();
        }
        if(!REMOVE.exists()) {
            REMOVE.mkdir();
        }
    }
    /** Check if two file have the same content by comparing the contentID */
    public static boolean fileContentCheck(File file1, File file2) {
        return sha1(readContentsAsString(file1)) == sha1(readContentsAsString(file2));
    }
    /**
     * Staging file1 if it meets the conditions
     * if it is identical to file2, don't do anything
     * if it is different, overwrite it with writeContents
     * ?? the content of the file is blobID?
     */
    public static void stagingSameNameFile(File file1, File file2) {
        // The staging area has the file(means 1. the file is not the same as the file
        // in the current commit if there is one 2. there is no same file in the REMOVE)
        // if it is the same as the file in ADDITION, do nothing
        // if it is not the same, overwrite the file,
        // then check if the current commit has the file, if same content remove the ADDITION file
        if(!fileContentCheck(file1, file2)) {
            writeContents(file2, readContentsAsString(file1));
        }
        // The staging are does not have the file,
        // Check if the current commit has the same file, if not add file to stage remove file in REMOVE
        // if yes check REMOVE
    }
    /**
     * Staging a file
     */
    public static void stagingFile(String fileName) {
        // The file exist!
        // Check if the file is in the currentCommit
        Commit currentCommit = Commit.getCommit(readContentsAsString(Repository.HEAD));
        Blobs blobStaging = new Blobs(fileName);
        if(currentCommit.findFile(fileName)) {
            // If the file is tracked in current commit
            String blobID = currentCommit.getBlobID(fileName);
            Blobs blobExisted = Blobs.getBlob(blobID);
            if(blobStaging.getContent().equals(blobExisted.getContent())) {
                // If identical, check the ADDITION and delete whatever is there
                if(findFileADDITION(fileName)) {
                    File deleteFile = join(ADDITION, fileName);
                    deleteFile.delete();
                }
            } else {
                // If not identical, just write to the ADDITION folder
                stagingFileADDTION(fileName);
            }
        } else {
            // If the file is not tracked in the current commit
            stagingFileADDTION(fileName);
        }
        fileRemove(fileName);
    }
    /**
     * Remove a file from REMOVE if it exist
     */
    private static void fileRemove(String fileName) {
        if(findFileREMOVE(fileName)) {
            File deleteFile = join(REMOVE, fileName);
            deleteFile.delete();
        }
    }
    /**
     * Staging a file in the current stage
     */
    /**
     * Staging a file that is not in the staging area
     */
    public static void stagingFileADDTION(String fileName) {
        File file = join(Repository.CWD, fileName);
        File inFile = join(ADDITION, fileName);
        writeContents(inFile, readContentsAsString(file));
    }
    /**
     * Check if a file is already in the ADDITION staging area
     */
    public static boolean findFileADDITION(String fileName) {
        File file = join(ADDITION, fileName);
        return file.exists();
    }
    /**
     * Check if a file is already in the REMOVE staging area
     */
    public static boolean findFileREMOVE(String fileName) {
        File file = join(REMOVE, fileName);
        return file.exists();
    }
    /** Check if the stage is empty */
    public static boolean stageEmpty() {
        List<String> fileAddList = plainFilenamesIn(".gitlet/stage/addition");
        List<String> fileRemoveList = plainFilenamesIn(".gitlet/stage/remove");
        return (fileAddList.isEmpty() && fileRemoveList.isEmpty());
    }
    /** Clean up the staging area */
    public static void stageClean() {
        List<String> fileAddList = plainFilenamesIn(".gitlet/stage/addition");
        if(fileAddList != null) {
            for(String each : fileAddList) {
                File deleteFile = join(ADDITION, each);
                deleteFile.delete();
                //restrictedDelete(each);
            }
        }
        List<String> fileRemoveList = plainFilenamesIn(".gitlet/stage/remove");
        if(fileRemoveList != null) {
            for(String each : fileRemoveList) {
                File deleteFile = join(REMOVE, each);
                deleteFile.delete();
            }
        }
    }
}
