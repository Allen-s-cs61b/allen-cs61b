package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.Utils.*;


import static gitlet.Utils.*;


/**
 *  Blobs class: The saved contents of files. Since Gitlet saves many versions of files,
 *  a single file might correspond to multiple blobs: each being tracked in a different commit.
 *  each blob represent a version of a file and has its unique ID generated by the sha1 hash
 */
public class Blobs implements Serializable {
    /** The name of the file, which could be the same as the previous files */
    private String fileName;
    /**
     *  The unique ID of a file, generated by sha1 hash
     *  The ID can be used to compare whether the content of two same name files are the same
     */
    private String contentID;
    /** The content of the file in String */
    private String content;
    /** Directory for storing blobs, each file has name blobID */
    public static final File BLOBS_DIR = join(Repository.GITLET_DIR, "blobs");
    /**  Constructor of Blobs, if someone uses add/rm they add or remove a file, the file name will
     * be taken to find the file in the current working directory, and then we generate the blobs ID
     * based on its contents
     * @param fileName
     */
    public Blobs(String fileName) {
        this.fileName = fileName;
        // guessing the "Hello.txt" file added are from the working directory
        File inFile = join(Repository.CWD, fileName);
        // Read the inFile("Hello.txt") and set the ID to its sha1 value
        this.contentID = generateFileID(inFile);  //??? maybe deleted
        this.content = readContentsAsString(inFile);
        // Add the blob to the BLOBS_DIR
        File blobDIR = join(BLOBS_DIR, this.generateBlobID());
        writeObject(blobDIR,this);
    }
    /** Set up blobs directory */
    public static void setUpBlobs() {
        if(!BLOBS_DIR.exists()) {
            BLOBS_DIR.mkdir();
        }
    }
    /** Return the file name in the current blob */
    public String getFileName() {
        return this.fileName;
    }
    /** Return the file ID in the current blob */
    public String getFileID() {
        return this.contentID;
    }
    /** Return the content of the file in the blob */
    public String getContent() {
        return this.content;
    }
    /** Generate ID based on sha1 hash */
    private String generateFileID(File file) {
        return sha1(readContentsAsString(file));
    }
    /** Generate blob ID based on sha1 hash */
    public String generateBlobID() {
        return sha1(serialize(this));
    }
    /** Return the blob based on the blobID */
    public static Blobs getBlob(String blobID) {
        File blobFile = join(BLOBS_DIR, blobID);
        Blobs blob = readObject(blobFile, Blobs.class);
        return blob;
    }
    public static String getContent(String blobID) {
        if(blobID == null) {
            return "";
        }
        Blobs blob = getBlob(blobID);
        return blob.getContent();
    }
}
