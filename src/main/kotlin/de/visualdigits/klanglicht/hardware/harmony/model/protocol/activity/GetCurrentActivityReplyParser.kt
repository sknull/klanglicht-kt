package de.visualdigits.klanglicht.hardware.harmony.model.protocol.activity

import com.fasterxml.jackson.databind.ObjectMapper
import de.visualdigits.klanglicht.hardware.harmony.protocol.OAReplyParser
import org.jivesoftware.smack.packet.IQ

/*
* Parser
*/
class GetCurrentActivityReplyParser : OAReplyParser() {
    override fun parseReplyContents(statusCode: String?, errorString: String?, contents: String?): IQ {
        return ObjectMapper().convertValue(
            parseKeyValuePairs(statusCode, errorString, contents),
            GetCurrentActivityReply::class.java
        )
    }
}
