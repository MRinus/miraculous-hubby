package de.holisticsystems.musicdeduper;


import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: michaelrinus
 * Date: 19.07.12
 * Time: 08:14
 * To change this template use File | Settings | File Templates.
 */
public class FileTypeTest extends Specification {
    void "test constructor"() {
        given:
        def file = new File(filename)
        def theFileType = new FileType(filename, file)

        expect:
        theFileType.file == file
        theFileType.name == filename

        where:
        filename   | dummy
        "TestFile" | "dummy"
    }
}
