# Gitlet Design Document

**Name**: Allen Liang

**Starting time**: 3.26

## Classes and Data Structures

### Main

#### Fields

1. 


### Repository

#### Fields

1. <pre>public static File HEAD</pre> 
2. <pre>public static File master</pre>
3. <pre>public static final File CWD = new File(System.getProperty("user.dir"))</pre>
4. <pre>public static final File GITLET_DIR = join(CWD, ".gitlet")</pre>
5. <pre>public static Stage stage</pre>
6. <pre>public static final String initCommitID = new Commit().generateID()</pre>


### Commit

#### Fields

1. <pre>public static final File COMMIT_DIR = join(Repository.GITLET_DIR, "commit")</pre>
2. <pre>private String message</pre>
3. <pre>private String timeStamp</pre>
4. <pre>private String parent</pre>
5. <pre>private Commit parentCommit</pre>
6. <pre>private Map(String, String) blobsMap</pre>


### Stage

#### Fields

1. <pre>public static final File STAGE_DIR = join(Repository.GITLET_DIR, "stage")</pre>
2. <pre>public static final File ADITTION = join(STAGE_DIR, "addition")</pre>
3. <pre>public static final File REMOVE = join(STAGE_DIR, "remove")</pre>

### Blobs

#### Fields

1. <pre>private String fileName</pre>
2. <pre>private String contentID</pre>
3. ??<pre>public static final File BLOBS_DIR = join(Repository.GITLET_DIR, "blobs")</pre>



## Algorithms

## Persistence

The Repository will set up all the persistence
1. GITLET_DIR .gitlet directory that stores all the directories and files
2. STAGE_DIR ADDITION REMOVE stage directory that represents staging area
3. COMMIT_DIR commit directory that stores all the commit file(sha1 ID)
4. HEAD directory that stores the head pointer(current commit)
5. master directory that stores the master pointer(the master commit)
6. BLOBS_DIR directory that stores blobs, using the ID as the file name

