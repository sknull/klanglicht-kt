package de.visualdigits.klanglicht.hardware.harmony.model.protocol.auth

import com.fasterxml.jackson.databind.ObjectMapper
import de.visualdigits.klanglicht.hardware.harmony.protocol.OAReplyParser
import org.jivesoftware.smack.packet.IQ

/*
* Parser
*/
class AuthReplyParser : OAReplyParser() {
    override fun parseReplyContents(statusCode: String?, errorString: String?, contents: String?): IQ {
        return ObjectMapper().convertValue(parseKeyValuePairs(statusCode, errorString, contents), AuthReply::class.java)
    }
}
