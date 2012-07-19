/**
 * Created by IntelliJ IDEA.
 * User: michaelrinus
 * Date: 20.03.12
 * Time: 12:56
 * To change this template use File | Settings | File Templates.
 */
package de.holisticsystems.musicdeduper
class FileType implements Serializable {
    private static final long serialVersionUID = 7943575331976561023
    String name
    File file

    public FileType(String nameIn, File fileIn) {
        name = nameIn
        file = fileIn
    }

    @Override
    public String toString() {
        "Name: " + name + ", File: " + file
    }
}