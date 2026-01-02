package de.visualdigits.klanglicht.hardware.harmony.model.protocol.activity

import com.fasterxml.jackson.databind.ObjectMapper
import de.visualdigits.klanglicht.hardware.harmony.protocol.OAReplyParser
import org.jivesoftware.smack.packet.IQ

/*
* Parser
*/
class StartActivityReplyParser : OAReplyParser() {
    override fun parseReplyContents(statusCode: String?, errorString: String?, contents: String?): IQ {
        return ObjectMapper().convertValue(
            parseKeyValuePairs(statusCode, errorString, contents),
            StartActivityReply::class.java
        )
    }

    override fun validResponseCode(code: String?): Boolean {
        //sometimes the start activity will return a 401 if a device is not setup correctly
        return super.validResponseCode(code) || code == "401"
    }
}
