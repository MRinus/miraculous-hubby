package de.holisticsystems.musicdeduper

/**
 * Created with IntelliJ IDEA.
 * User: michaelrinus
 * Date: 09.05.12
 * Time: 12:06
 * To change this template use File | Settings | File Templates.
 */
def getExtension(filename) {
    String fileExtRegEx = /(\.[^\.]*)$/
    def returned_value = ""
    m = (filename =~ fileExtRegEx)
    if (m.size() > 0) returned_value = ((m[0][0].size() > 0) ? m[0][0].substring(1).trim().toLowerCase() : "");
    return returned_value
}