#!/usr/bin/env groovy
/**
 * Created by IntelliJ IDEA.
 * User: michaelrinus
 * Date: 20.03.12
 * Time: 12:56
 * To change this template use File | Settings | File Templates.
 */
package de.holisticsystems.musicdeduper

import com.thoughtworks.xstream.XStream

class MusicDeDuper {
    static start = new Date()
//All NAS stored Files
    static File nas = new File("/Volumes/Musik/")
//All locally stored Files
    static File local = new File("/Users/michaelrinus/Music/iTunes/iTunes Media/")
//Files for serialization
    final static localMusicTxt = "./localMusicFiles.txt"
    final static String localMusicXML = "./localMusicFiles.xml"
    final static nasMusicTxt = "./nasMusicFiles.txt"
    final static String nasMusicXML = "./nasMusicFiles.xml"


    static ArrayList<FileType> localFiles = new ArrayList<FileType>()
    static ArrayList<FileType> localMusicFiles = new ArrayList<FileType>()
    static ArrayList<FileType> localDupeFiles = new ArrayList<FileType>()
    static ArrayList<FileType> nasFiles = new ArrayList<FileType>()
    static ArrayList<FileType> nasMusicFiles = new ArrayList<FileType>()



    static xStream = new XStream()

    static {
        xStream.alias("FileType", FileType.class)
    }
//define stub

    public static void main(String[] args) {
        def dirLister = {
        }

        def decide = {it, arrayList ->
            if (!it.isDirectory() && it.name =~ /.*\.(mp3)|(m4a)|(m4p)|(wav)|(aiff)/) {
                //println it.getName()
                def newFile = new FileType(it.getName(), it)
                arrayList.add newFile
                //print "."
            } else if (it.isDirectory()) {
                if (it != null && arrayList != null)
                    dirLister it, arrayList
            }
        }
//handles a directory
        dirLister = {
            it, arrayList ->
//    println "called dirLister"
            it.listFiles().each() {
                decide it, arrayList
            }
            it.listFiles().each() {
                decide it, arrayList
            }
        }
//decides if File or Directory is handled


        handleNAS(nasMusicXML, nasMusicTxt, nasMusicFiles, nas, decide, nasFiles)

        handleLocalFiles(localMusicXML, localMusicTxt, localMusicFiles, local, decide, localFiles, nasMusicFiles, localDupeFiles)

        println "Local DupeFiles:"
        println localDupeFiles.size()

        def end = new Date()

//work on Files to find and delete dupes
        float startDecTime = start.getHours() + start.getMinutes() / 60 + start.getSeconds() / 3600

        float endDecTime = end.getHours() + end.getMinutes() / 60 + end.getSeconds() / 3600
        float diffDecTime = (endDecTime - startDecTime) * 60

//some statistics
        println "Used time:"
        println " ${diffDecTime.round(2)} m "
        println " ${(diffDecTime * 3600.0).round(2).toString()} s "

    }

    private static void logFileArraySize(String storage, ArrayList<FileType> fileArray) {
        println storage + " MusicFiles:"
        println fileArray.size()
    }
/**
 * Works on a given path on the LOCAL Storage and scans all Music Files to be put in a list of FileType
 * @param localMusicXML
 * @param localMusicTxt
 * @param localMusicFiles
 * @param local
 * @param decide
 * @param localFiles
 * @param nasMusicFiles
 * @param localDupeFiles
 */
    private static void handleLocalFiles(String localMusicXML, String localMusicTxt, ArrayList<FileType> localMusicFiles, File local, Closure<Void> decide, ArrayList<FileType> localFiles, ArrayList<FileType> nasMusicFiles, ArrayList<FileType> localDupeFiles) {
        println "Handle local..."
        if (new File(localMusicXML).exists()) {
            println "DeSerialize Local's Music ArrayList"

            deserializeFile(localMusicTxt, localMusicFiles)

            logFileArraySize("Local", localMusicFiles)
        }
        else {
            println "Scan local..."
            handleStorage(local, decide, localFiles)
            logFileArraySize("Local", localMusicFiles)
//work on Files to kick out noMusicFiles
            localFiles.each() {FileType entry ->
                //println entry.name
                //Kick out NoMusic
                if (entry.name =~ /.*\.(mp3)|(m4a)|(m4p)|(wav)|(aiff)/)
//        println "Music found!"
                    localMusicFiles += entry
            }

            println "Local MusicFiles:"
            println localMusicFiles.size()

            //free some Memory
            localFiles = null

            xStream.classLoader = localMusicFiles.getClass().classLoader

            new File(localMusicTxt).withObjectOutputStream { out ->
                out << localMusicFiles
                0
            }

            new File(localMusicXML).withOutputStream { out ->
                xStream.toXML(localMusicFiles, out)
            }
        }
        nasMusicFiles.each() {FileType entry ->
            //println entry.name
            //Kick out NoMusic
            if (localMusicFiles.contains(entry)) {
                //println "Dupe found!"
                localDupeFiles += entry
            }
        }
    }

/**
 * Deserializes a textFile to a given ArrayList<FileType>
 * @param fileName
 * @param FileTypeArray
 */
    private static void deserializeFile(String fileName, def FileTypeArray) {
        new File(fileName).withObjectInputStream { inStream ->
            inStream.eachObject {def object ->
                object.each() {def FileTypeItem ->
                    FileTypeArray.add(FileTypeItem)
                }
            }
            0
        }
    }

/**
 * Works on a given path on the NAS and scans all Music Files to be put in a list of FileType
 * @param nasMusicXML
 * @param nasMusicTxt
 * @param nasMusicFiles
 * @param nas
 * @param decide
 * @param nasFiles
 */
    private static void handleNAS(String nasMusicXML, String nasMusicTxt, ArrayList<FileType> nasMusicFiles, File nas, Closure<Void> decide, ArrayList<FileType> nasFiles) {
        println "Handle nas..."
//Decide if we already have xml Files containing the serialized ArrayLists
        if (new File(nasMusicXML).exists()) {
            println "DeSerialize NAS' Music ArrayList"
            deserializeFile(nasMusicTxt, nasMusicFiles)

            logFileArraySize("NAS", nasMusicFiles)
        }
        else {
            println "Scan NAS..."
            handleStorage(nas, decide, nasFiles)

            logFileArraySize("NAS", nasMusicFiles)
//work on Files to kick out noMusicFiles
            nasFiles.each() {FileType entry ->
                //println entry.name
                //Kick out NoMusic
                if (entry.name =~ /.*\.(mp3)|(m4a)|(m4p)|(wav)|(aiff)/)
//        println "Music found!"
                    nasMusicFiles += entry
            }

            logFileArraySize("NAS", nasMusicFiles)

//free some Memory
            nasFiles = null;

            xStream.classLoader = nasMusicFiles.getClass().classLoader

            new File(nasMusicTxt).withObjectOutputStream { out ->
                out << nasMusicFiles
                0
            }

            new File(nasMusicXML).withOutputStream { out ->
                xStream.toXML(nasMusicFiles, out)
            }

        }
    }

    private static Collection handleStorage(File location, decide, ArrayList<FileType> FileTypeArrayList) {
        return location.listFiles().findAll() {it.name =~ /.*\.(mp3)|(m4a)|(m4p)|(wav)|(aiff)/ || it.isDirectory()}.each() {
            decide it, FileTypeArrayList
        }
    }
}